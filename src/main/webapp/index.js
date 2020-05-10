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
                result.json().then(jsonResult => alert(jsonResult.message));
            } else {
                refreshTaskData();
            }
        })
        .catch(result => alert(result));
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
                result.json().then(jsonResult => alert(jsonResult.message));
            } else {
                refreshTaskData();
            }
        })
        .catch(result => alert(result));
});

let savedTaskData = [];

function showImageTooltip(event, source) {
    const imgTooltip = document.getElementById('imageInImageTooltip');
    imgTooltip.src = source;
    imgTooltip.style.maxWidth = (window.innerWidth - event.pageX) * 0.9 + 'px';
    imgTooltip.style.maxHeight = (window.innerHeight - event.pageY) * 0.9 + 'px';
    const tooltip = document.getElementById('imageTooltip');
    tooltip.style.left = event.pageX + 'px';
    tooltip.style.top = event.pageY + 'px';
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
    fetch('./task/', {
        method: 'DELETE',
        body: id
    })
        .then(result => {
            if (result.ok) {
                refreshTaskData();
                savedTaskData.filter(task => task.id !== id)
            } else {
                result.json()
                    .then(json => alert(json.message));
            }
        });
}

function prepareTask(order, content) {
    savedTaskData[order] = content;
    const container = document.createElement("tr");
    container.id = order;
    const th = document.createElement("th");
    th.scope = "row";
    th.innerHTML = order + 1;
    container.appendChild(th);
    let td = document.createElement("td");
    td.innerHTML = content.status;
    container.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = content.inputName;
    container.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = content.tilesetsName;
    container.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = content.minCompliance;
    container.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = content.time.substring(0, 19).replace('T', ' ');
    container.appendChild(td);
    td = document.createElement("td");
    const inputPreview = document.createElement('span');
    inputPreview.innerHTML = '[I]';
    inputPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/input?id=' + content.id));
    inputPreview.addEventListener('mouseout', hideImageTooltip);
    inputPreview.addEventListener('click', () => window.open('./task/input?id=' + content.id));
    td.appendChild(inputPreview);
    if (content.status === 'FINISHED') {
        const resultPreview = document.createElement('span');
        resultPreview.innerHTML = '[R]';
        resultPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/result?id=' + content.id));
        resultPreview.addEventListener('mouseout', hideImageTooltip);
        resultPreview.addEventListener('click', () => window.open('./task/result?id=' + content.id));
        td.appendChild(resultPreview);
        const diffPreview = document.createElement('span');
        if (content.hasDiff) {
            diffPreview.innerHTML = '[D]';
            diffPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/diff?id=' + content.id));
            diffPreview.addEventListener('mouseout', hideImageTooltip);
            diffPreview.addEventListener('click', () => window.open('./task/diff?id=' + content.id));
            td.appendChild(diffPreview);
        }
        const deleteSpan = document.createElement('span');
        deleteSpan.innerText = '[X]';
        deleteSpan.onclick = () => deleteTask(content.id);
        td.appendChild(deleteSpan);
    }
    container.appendChild(td);
    return container;
}

function updateTask(order, content) {
    const oldTask = savedTaskData[order];
    if (oldTask == null) {
        const domTask = prepareTask(order, content);
        document.getElementById("taskListContent").appendChild(domTask);
    } else if (oldTask.id !== content.id || oldTask.status !== content.status) {
        const element = document.getElementById(order);
        element.parentNode.removeChild(element);
        const domTask = prepareTask(order, content);
        const appender = document.getElementById("taskListContent").childNodes[order + 1];
        if (appender != null) {
            document.getElementById("taskListContent").insertBefore(domTask, document.getElementById("taskListContent").childNodes[order + 1]);
        } else {
            document.getElementById("taskListContent").appendChild(domTask);
        }
    }
}

function updateTasks(freshTask) {
    let order = 0;
    freshTask.forEach(content => updateTask(order++, content))
}

function refreshTaskData() {
    fetch('./task')
        .then(result => result.json()
            .then(result => updateTasks(result)))
}

window.setInterval(refreshTaskData, 10_000);
refreshTaskData();

fetch('map/remote')
    .then(result => {
        if (result.ok) {
            result.json()
                .then(json => autocomplete(document.getElementById('addRemoteTaskMap'), json))
        } else {
            result.json()
                .then(json => alert(json.message))
        }
    });
