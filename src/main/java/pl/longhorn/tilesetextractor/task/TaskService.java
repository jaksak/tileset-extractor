package pl.longhorn.tilesetextractor.task;

import lombok.val;
import org.springframework.stereotype.Service;
import pl.longhorn.tilesetextractor.ProjectConfig;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskService {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LimitedQueue<ExtractorTask> tasks = new LimitedQueue<>(10);
    private TilesetExtractor tilesetExtractor = new TilesetExtractor();

    public void addTask(ExtractorTask task) {
        if (isNotBusy()) {
            task.setTime(LocalDateTime.now());
            tasks.add(task);
            executorService.submit(() -> processTask(task));
        } else {
            throw new BusySystemException();
        }
    }

    private void processTask(ExtractorTask task) {
        task.setTime(LocalDateTime.now());
        task.setStatus(ExtractorTaskStatus.IN_PROGRESS);
        val result = tilesetExtractor.run(task.getTilesets(), task.getMapImage());
        task.setTime(LocalDateTime.now());
        task.setResult(result);
        task.setStatus(ExtractorTaskStatus.FINISHED);
    }

    private boolean isNotBusy() {
        return tasks.stream()
                .filter(task -> ExtractorTaskStatus.PENDING.equals(task.getStatus()))
                .count() < ProjectConfig.MAX_PENDING_TASK;
    }

    public LimitedQueue<ExtractorTask> getTasks() {
        return tasks;
    }

    public Optional<ExtractorTask> getTask(String id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findAny();
    }
}
