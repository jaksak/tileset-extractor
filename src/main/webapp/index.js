document.getElementById('addLocalTaskSubmit').addEventListener('click', function () {
    const formData = new FormData();
    formData.append('tilesetsName', document.getElementById('addLocalTaskTilesetsName').value);
    formData.append('minCompliance', document.getElementById('addLocalTaskMinCompliance').value);
    formData.append('file', document.getElementById('addLocalTaskFile').files[0]);

    fetch('./task/local', {
        method: 'POST',
        body: formData
    })
        .then((response) => response.json())
        .then((result) => {
            console.log('Success:', result);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
});

document.getElementById("addRemoteTaskSubmit").addEventListener("click", function () {
    const data = {};
    serializeArray(document.getElementById('addRemoteTaskForm')).forEach(element => data[element.name] = element.value);
    const response = fetch('./task/remote', {
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
        .then(r => {
            if (!r.ok) {
                r.text().then(text => alert(text))
            }
        })
        .catch(currentResponse => alert(response.statusText))
});

let savedTaskData = [];

function showResultTooltip(event, id) {
    const imgTooltip = document.getElementById('resultImgTooltip');
    imgTooltip.src = './task/result?id=' + id;
    imgTooltip.style.maxWidth = (window.innerWidth - event.pageX) * 0.9 + 'px';
    imgTooltip.style.maxHeight = (window.innerHeight - event.pageY) * 0.9 + 'px';
    const tooltip = document.getElementById('resultTooltip');
    tooltip.style.visibility = 'visible';
    tooltip.style.left = event.pageX + 'px';
    tooltip.style.top = event.pageY + 'px';
}

function hideResultTooltip() {
    document.getElementById('resultTooltip').style.visibility = 'hidden';
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
    td.innerHTML = content.time.substring(0, 19).replace('T', ' ');
    container.appendChild(td);
    td = document.createElement("td");
    if (content.status === 'FINISHED') {
        td.innerHTML = '[ ✔ ]';
        td.addEventListener('mouseover', event => showResultTooltip(event, content.id));
        td.addEventListener('mouseout', hideResultTooltip);
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
        document.getElementById("taskListContent").insertBefore(domTask, document.getElementById("taskListContent").childNodes[order - 1]);
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