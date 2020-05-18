package pl.longhorn.tileset.extractor.task;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;
import pl.longhorn.tileset.extractor.ProjectConfig;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractor;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractorParam;
import pl.longhorn.tileset.extractor.map.painter.DiffPainter;
import pl.longhorn.tileset.extractor.tileset.TilesetSupplier;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final LimitedQueue<ExtractorTask> tasks = new LimitedQueue<>(15);
    private final TilesetExtractor tilesetExtractor = new TilesetExtractor();

    private final TilesetSupplier tilesetSupplier;

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
        try {
            processTaskInternal(task);
        } catch (Exception e) {
            task.setStatus(ExtractorTaskStatus.ERROR);
            task.setTime(LocalDateTime.now());
            Logger.error(e);
        }
    }

    private void processTaskInternal(ExtractorTask task) {
        Tilesets tilesets = processPrepareTilesets(task);
        processDownloadMap(task);
        val result = processExtractor(task, tilesets);
        processDiff(task, result);
        processPostTask(task);
    }

    private void processDownloadMap(ExtractorTask task) {
        task.setStatus(ExtractorTaskStatus.DOWNLOAD_MAP);
        task.setTime(LocalDateTime.now());
        task.getInput().get();
    }

    private void processDiff(ExtractorTask task, BufferedImage result) {
        if (task.isHasDiff()) {
            task.setStatus(ExtractorTaskStatus.PREPARE_DIFF);
            val diffImage = DiffPainter.paint(task.getInput().get(), result);
            task.setDiff(diffImage);
        }
    }

    private void processPostTask(ExtractorTask task) {
        task.setTime(LocalDateTime.now());
        task.setStatus(ExtractorTaskStatus.FINISHED);
    }

    private BufferedImage processExtractor(ExtractorTask task, Tilesets tilesets) {
        task.setTime(LocalDateTime.now());
        task.setStatus(ExtractorTaskStatus.IN_PROGRESS);
        val extractorParam = new TilesetExtractorParam(tilesets, task.getInput().get(), task.getMinCompliance());
        val result = tilesetExtractor.run(extractorParam);
        task.setResult(result);
        return result;
    }

    private Tilesets processPrepareTilesets(ExtractorTask task) {
        task.setTime(LocalDateTime.now());
        task.setStatus(ExtractorTaskStatus.PREPARE_TILESETS);
        return tilesetSupplier.getTilesets(task.getTilesetsName());
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

    public void remove(String id) {
        val task = getTask(id);
        if (task.isPresent() && ExtractorTaskStatus.FINISHED.equals(task.get().getStatus())) {
            tasks.remove(task.get());
        }
    }
}
