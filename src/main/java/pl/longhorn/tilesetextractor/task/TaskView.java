package pl.longhorn.tilesetextractor.task;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TaskView implements Serializable {
    private String id;
    private ExtractorTaskStatus status;
    private LocalDateTime time;
    private String tilesetsName;
    private String inputName;

    public TaskView(ExtractorTask task) {
        this.id = task.getId();
        this.status = task.getStatus();
        this.time = task.getTime();
        this.tilesetsName = task.getTilesetsName();
        this.inputName = task.getInputName();
    }
}
