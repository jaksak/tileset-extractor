package pl.longhorn.tilesetextractor.task;

import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.longhorn.tilesetextractor.ImageHelper;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ExtractorController {

    private TaskService taskService;

    public ExtractorController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("task/remote")
    public TaskView addTask(@RequestBody RemoteTaskInputData inputData) {
        ExtractorTask task = new ExtractorTask(inputData.getTilesetsName(), inputData.getMapFileName(), getMap(inputData), inputData.getMinCompliance(), inputData.isHasDiff());
        validateTask(task);
        taskService.addTask(task);
        return new TaskView(task);
    }

    @PostMapping("task/local")
    public TaskView addTask(@RequestParam("file") MultipartFile file, @RequestParam String tilesetsName, @RequestParam int minCompliance, @RequestParam boolean hasDiff) throws IOException {
        ExtractorTask task = new ExtractorTask(tilesetsName, file.getOriginalFilename(), getImage(file), minCompliance, hasDiff);
        validateTask(task);
        taskService.addTask(task);
        return new TaskView(task);
    }

    @GetMapping("task")
    public List<TaskView> getTasks() {
        return taskService.getTasks().stream()
                .map(TaskView::new)
                .collect(Collectors.toList());
    }

    @GetMapping("map/remote")
    public List<String> getRemoteMaps() throws URISyntaxException, IOException {
        return Files.walk(ImageHelper.getResourcePath("maps"))
                .skip(1)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping("task/input")
    public void getInput(@RequestParam String id, HttpServletResponse response) throws IOException {
        val task = taskService.getTask(id);
        if (task.isPresent()) {
            val image = task.get().getInput();
            attachImage(response, image);
        }
    }

    @ResponseBody
    @GetMapping("task/result")
    public void getResult(@RequestParam String id, HttpServletResponse response) throws IOException {
        val task = taskService.getTask(id);
        if (task.isPresent()) {
            val image = task.get().getResult();
            attachImage(response, image);
        }
    }

    @ResponseBody
    @GetMapping("task/diff")
    public void getDiff(@RequestParam String id, HttpServletResponse response) throws IOException {
        val task = taskService.getTask(id);
        if (task.isPresent()) {
            val image = task.get().getDiff();
            attachImage(response, image);
        }
    }

    @DeleteMapping("task")
    public void deleteTask(@RequestBody String id) {
        taskService.remove(id);
    }

    private void validateTask(ExtractorTask task) {
        validateTilesetsName(task.getTilesetsName());
        validateMinCompliance(task.getMinCompliance());
        validateUnique(task);
    }

    private void validateUnique(ExtractorTask task) {
        boolean isNotUnique = taskService.getTasks().stream()
                .anyMatch(other -> isNotShallowUnique(task, other));
        if (isNotUnique) {
            throw new RepeatTaskException();
        }
    }

    private boolean isNotShallowUnique(ExtractorTask task, ExtractorTask other) {
        return task.getInputName().equals(other.getInputName()) && task.getMinCompliance() == other.getMinCompliance() && task.getTilesetsName().equals(other.getTilesetsName());
    }

    private void validateMinCompliance(int minCompliance) {
        if (minCompliance < 2 || minCompliance > 99) {
            throw new IllegalMinComplianceException();
        }
    }

    private void attachImage(HttpServletResponse response, BufferedImage resultImage) throws IOException {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resultImage, "png", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        IOUtils.copy(is, response.getOutputStream());
    }

    private void validateTilesetsName(String tilesetsName) {
        try {
            ImageHelper.getResourcePath("tilesets/" + tilesetsName);
        } catch (URISyntaxException | NullPointerException e) {
            throw new BadTilesetsNameException();
        }
    }

    private BufferedImage getMap(RemoteTaskInputData inputData) {
        try {
            return ImageHelper.getImage("maps/" + inputData.getMapFileName());
        } catch (URISyntaxException | IOException e) {
            throw new MapNotExistException();
        }
    }

    private BufferedImage getImage(MultipartFile file) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        return ImageIO.read(bis);
    }
}
