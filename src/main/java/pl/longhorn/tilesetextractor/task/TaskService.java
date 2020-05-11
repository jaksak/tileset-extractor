package pl.longhorn.tilesetextractor.task;

import lombok.val;
import org.springframework.stereotype.Service;
import pl.longhorn.tilesetextractor.ProjectConfig;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractorParam;
import pl.longhorn.tilesetextractor.map.painter.DiffPainter;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskService {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LimitedQueue<ExtractorTask> tasks = new LimitedQueue<>(10);
    private TilesetExtractor tilesetExtractor = new TilesetExtractor();
    private Map<String, Tilesets> tilesetsByName = new HashMap<>();

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
        Tilesets tilesets = processPrepareTilesets(task);
        val result = processExtractor(task, tilesets);
        processDiff(task, result);
        processPostTask(task);
    }

    private void processDiff(ExtractorTask task, BufferedImage result) {
        if (task.isHasDiff()) {
            task.setStatus(ExtractorTaskStatus.PREPARE_DIFF);
            val diffImage = DiffPainter.paint(task.getInput(), result);
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
        val extractorParam = new TilesetExtractorParam(tilesets, task.getInput(), task.getMinCompliance());
        val result = tilesetExtractor.run(extractorParam);
        task.setResult(result);
        return result;
    }

    private Tilesets processPrepareTilesets(ExtractorTask task) {
        task.setTime(LocalDateTime.now());
        task.setStatus(ExtractorTaskStatus.PREPARE_TILESETS);
        return getTilesets(task.getTilesetsName());
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

    private synchronized Tilesets getTilesets(String tilesetsName) {
        Tilesets tilesets = tilesetsByName.get(tilesetsName);
        if (tilesets == null) {
            tilesets = createTilesets(tilesetsName);
        }
        return tilesets;
    }

    private Tilesets createTilesets(String tilesetsName) {
        try {
            return createTilesetsInternal(tilesetsName);
        } catch (IOException | URISyntaxException e) {
            throw new BadTilesetsNameException();
        }
    }

    private Tilesets createTilesetsInternal(String tilesetsName) throws IOException, URISyntaxException {
        Tilesets tilesets = new Tilesets(tilesetsName);
        tilesetsByName.put(tilesetsName, tilesets);
        return tilesets;
    }

    public void remove(String id) {
        val task = getTask(id);
        if (task.isPresent() && ExtractorTaskStatus.FINISHED.equals(task.get().getStatus())) {
            tasks.remove(task.get());
        }
    }
}
