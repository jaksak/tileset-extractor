class TaskProgress {
    constructor(progress, progressBar) {
        this.progress = progress;
        this.progressBar = progressBar;
        this.isVisible = false;
    }

    update(taskListView) {
        const firstInProgressTask = this.findInProgressTask(taskListView.tasks);
        if (firstInProgressTask == null && this.isVisible) {
            this.progress.style.opacity = "0";
            this.isVisible = false;
            window.setTimeout(() => this.progress.style.display = "none", 1000);
        } else if (firstInProgressTask != null) {
            const operationTime = Date.now() - Date.parse(firstInProgressTask.startTime);
            if (operationTime < taskListView.prediction) {
                this.progressBar.style.width = operationTime / taskListView.prediction * 100 + '%';
            } else {
                this.progressBar.style.width = '99%';
            }
            if (!this.isVisible) {
                this.progress.style.opacity = "1";
                this.isVisible = true;
                this.progress.style.display = "block"
            }
        }
    }

    findInProgressTask(tasks) {
        return tasks.find(task => task.status === 'IN_PROGRESS');
    }
}