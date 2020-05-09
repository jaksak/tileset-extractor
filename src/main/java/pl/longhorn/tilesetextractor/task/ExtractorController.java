package pl.longhorn.tilesetextractor.task;

import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ExtractorController {

    private TaskService taskService;
    private Map<String, Tilesets> tilesetsMap;

    public ExtractorController(TaskService taskService) {
        this.taskService = taskService;
        this.tilesetsMap = new HashMap<>();
    }

    @PostMapping("task/remote")
    public TaskView addTask(@RequestBody RemoteTaskInputData inputData) {
        ExtractorTask task = new ExtractorTask(getTilesets(inputData.getTilesetsName()), getMap(inputData));
        taskService.addTask(task);
        return new TaskView(task);
    }

    @PostMapping("task/local")
    public TaskView addTask(@RequestParam("file") MultipartFile file, @RequestParam String tilesetsName) throws IOException {
        ExtractorTask task = new ExtractorTask(getTilesets(tilesetsName), getImage(file));
        taskService.addTask(task);
        return new TaskView(task);
    }

    @GetMapping("task")
    public List<TaskView> getTasks() {
        return taskService.getTasks().stream()
                .map(TaskView::new)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping("task/result")
    public void getResult(@RequestParam String id, HttpServletResponse response) throws IOException {
        val task = taskService.getTask(id);
        if (task.isPresent()) {
            val resultImage = task.get().getResult();
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resultImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    private BufferedImage getMap(RemoteTaskInputData inputData) {
        try {
            return ImageHelper.getImage(inputData.getMapFileName());
        } catch (URISyntaxException | IOException e) {
            throw new MapNotExistException();
        }
    }

    private synchronized Tilesets getTilesets(String tilesetsName) {
        Tilesets tilesets = tilesetsMap.get(tilesetsName);
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
        tilesetsMap.put(tilesetsName, tilesets);
        return tilesets;
    }

    private BufferedImage getImage(MultipartFile file) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        return ImageIO.read(bis);
    }
}
