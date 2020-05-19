document.getElementById('addLocalTaskSubmit').addEventListener('click', function () {
    const formData = new FormData();
    formData.append('tilesetsName', document.getElementById('addLocalTaskTilesetsName').value);
    formData.append('minCompliance', document.getElementById('addLocalTaskMinCompliance').value);
    formData.append('hasDiff', document.getElementById('addLocalTaskHasDiff').checked);
    formData.append('file', document.getElementById('addLocalTaskFile').files[0]);

    fetch('./task/local', {
        method: 'POST',
        body: formData
    })
        .then(result => {
            if (!result.ok) {
                result.json().then(jsonResult => showErrorAlert(jsonResult.message));
            } else {
                refreshTaskData();
                showAlert();
            }
        })
        .catch(result => prepareErrorAlert(result));
});

document.getElementById("addRemoteTaskSubmit").addEventListener("click", function () {
    const data = {};
    serializeArray(document.getElementById('addRemoteTaskForm')).forEach(element => data[element.name] = element.value);
    data.hasDiff = document.getElementById('addRemoteTaskHasDiff').checked;
    fetch('./task/remote', {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data) // body data type must match 'Content-Type' header
    })
        .then(result => {
            if (!result.ok) {
                result.json().then(jsonResult => showErrorAlert(jsonResult.message)).catch(result => prepareErrorAlert(result));
            } else {
                refreshTaskData();
                showAlert();
            }
        })
        .catch(result => prepareErrorAlert(result));
});

function showImageTooltip(event, source) {
    const imgTooltip = document.getElementById('imageInImageTooltip');
    imgTooltip.src = source;
    imgTooltip.style.maxWidth = (window.innerWidth - event.pageX) * 0.9 + 'px';
    imgTooltip.style.maxHeight = (window.innerHeight - event.pageY) * 0.9 + 'px';
    const tooltip = document.getElementById('imageTooltip');
    tooltip.style.left = event.pageX + 3 + 'px';
    tooltip.style.top = event.pageY + 3 + 'px';
    shouldShowTooltip = true;
}

const imageInImageTooltip = document.getElementById('imageInImageTooltip');
let shouldShowTooltip;

imageInImageTooltip.addEventListener('load', function () {
    if (shouldShowTooltip) {
        document.getElementById('imageTooltip').style.visibility = 'visible';
    }
});

function hideImageTooltip() {
    document.getElementById('imageTooltip').style.visibility = 'hidden';
    shouldShowTooltip = false;
}

function deleteTask(id) {
    if (confirm('Are you sure? Map rendering process about 1 hours.')) {
        fetch('./task/', {
            method: 'DELETE',
            body: id
        })
            .then(result => {
                if (result.ok) {
                    refreshTaskData();
                    showAlert();
                } else {
                    result.json()
                        .then(json => showErrorAlert(json.message))
                        .catch(result => prepareErrorAlert(result));
                }
            }).catch(result => prepareErrorAlert(result));
    }
}

const taskRows = [];

function prepareHtmlTaskRow() {
    const row = document.createElement('tr');
    row.className = 'taskRow';
    row.appendChild(document.createElement('th'));
    for (let i = 0; i < 6; i++) {
        row.appendChild(document.createElement('td'));
    }
    row.style.opacity = "0";
    document.getElementById('taskListContent').appendChild(row);
    return row;
}

function updateTask(order, content) {
    let taskRow = taskRows[order];
    if (taskRow == null) {
        const htmlRow = prepareHtmlTaskRow();
        setTimeout(function () {
            taskRows[order] = new TaskRow(htmlRow, content, order);
        }, 1);
    } else {
        taskRow.updateData(content)
    }
}

function removeTasksAfterOrEquals(order) {
    for (let i = order; order < taskRows.length; i++) {
        taskRows[i].removeHtml();
        taskRows.splice(i, 1);
    }
}

const taskProgress = new TaskProgress(document.getElementById('taskStatusContainer'), document.getElementById('taskProgressBar'));

function updateTasks(taskListView) {
    let order = 0;
    taskListView.tasks.forEach(content => updateTask(order++, content));
    removeTasksAfterOrEquals(order);
    taskProgress.update(taskListView);
}

function refreshTaskData() {
    fetch('./task')
        .then(result => result.json()
            .then(result => updateTasks(result)))
}

window.setInterval(refreshTaskData, 1_000);
refreshTaskData();

fetch('map/remote')
    .then(result => {
        if (result.ok) {
            result.json()
                .then(json => autocomplete(document.getElementById('addRemoteTaskMap'), json))
        } else {
            result.json()
                .then(json => showErrorAlert(json.message))
        }
    }).catch(result => prepareErrorAlert(result));

const alertTemplate = document.getElementById('alertTemplate').cloneNode();
const alertContainer = document.getElementById('alertContainer');

function showAlert(message, style) {
    const alert = alertTemplate.cloneNode();
    alert.id = 'tilesetAlert';
    alert.innerText = message == null ? 'Action successfully finished.' : message;
    alert.classList.add(style == null ? 'alert-primary' : style);
    alertContainer.appendChild(alert);
    window.setTimeout(() => alert.style.opacity = 0, 5000);
    window.setTimeout(() => alertContainer.removeChild(alert), 6000);
}

const defaultMessage = 'Something went wrong.';

function showErrorAlert(code) {
    const message = errorCodesToMessage[code];
    if (message === undefined) {
        showAlert(defaultMessage, 'alert-danger');
    } else {
        showAlert(message, 'alert-danger');
    }
}

function prepareErrorAlert(result) {
    const parsedResult = JSON.parse(result);
    showErrorAlert(parsedResult.message);
}

const errorCodesToMessage = {
    'illegalMinCompliance': 'Invalid value in minimum compliance field.',
    'mapName': 'Map not exist.',
    'fileSize': 'File is too large.',
    'repeatedTask': 'Task should be unique.',
    'busy': 'Exceeded max pending task.'
};