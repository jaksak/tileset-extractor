package pl.longhorn.tileset.extractor.task;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class TaskListView {
    private List<TaskView> tasks;
    private int prediction;
}
