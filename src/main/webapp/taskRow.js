class TaskRow {
    constructor(trElement, data, rowId) {
        this.trElement = trElement;
        this.rowId = rowId;
        this.data = {};
        this.adjustData(data);
    }

    adjustData(data) {
        const columns = this.trElement.childNodes;
        this.updateSimpleValue(columns[0], this.rowId + 1);
        this.updateSimpleValue(columns[1], data.status);
        this.updateSimpleValue(columns[2], data.inputName);
        this.updateSimpleValue(columns[3], data.tilesetsName);
        this.updateSimpleValue(columns[4], data.minCompliance);
        this.updateSimpleValue(columns[5], data.time.slice(0, 10).replace('T', ' '));
        this.updateActionColumn(columns[6], data);
        this.trElement.style.display = 'table-row';
    }

    updateSimpleValue(column, newValue) {
        const oldValue = column.innerHTML;
        if (oldValue !== newValue) {
            column.innerHTML = newValue;
        }
    }

    updateActionColumn(column, data) {
        column.innerHTML = '';
        const inputPreview = document.createElement('span');
        inputPreview.innerText = '[I]';
        inputPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/input?id=' + data.id));
        inputPreview.addEventListener('mouseout', hideImageTooltip);
        inputPreview.addEventListener('click', () => window.open('./task/input?id=' + data.id));
        column.appendChild(inputPreview);
        if (data.status === 'FINISHED') {
            const resultPreview = document.createElement('span');
            resultPreview.innerText = '[R]';
            resultPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/result?id=' + data.id));
            resultPreview.addEventListener('mouseout', hideImageTooltip);
            resultPreview.addEventListener('click', () => window.open('./task/result?id=' + data.id));
            column.appendChild(resultPreview);
            const diffPreview = document.createElement('span');
            if (data.hasDiff) {
                diffPreview.innerText = '[D]';
                diffPreview.addEventListener('mouseover', event => showImageTooltip(event, './task/diff?id=' + data.id));
                diffPreview.addEventListener('mouseout', hideImageTooltip);
                diffPreview.addEventListener('click', () => window.open('./task/diff?id=' + data.id));
                column.appendChild(diffPreview);
            }
            const deleteInput = document.createElement('span');
            deleteInput.innerText = '[X]';
            deleteInput.addEventListener('click', () => deleteTask(data.id));
            column.appendChild(deleteInput);
        }
    }

    removeHtml() {
        this.trElement.remove();
    }
}