package pl.longhorn.tilesetextractor.task;

import kong.unirest.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.longhorn.imageholderclient.ImageHolderAccessor;
import pl.longhorn.imageholderclient.ImageHolderAccessorImpl;
import pl.longhorn.imageholderclient.LazyInitializer;
import pl.longhorn.imageholdercommon.ImageDetailsView;
import pl.longhorn.imageholdercommon.ImageListView;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.ProjectConfig;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final TaskService taskService;
    private final ImageHolderAccessor imageHolderAccessor = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);

    private final LazyInitializer<List<String>> remoteMapsNames = new LazyInitializer<>(this::getMapNamesRemotely);

    @PostMapping("task/remote")
    public TaskView addTask(@RequestBody RemoteTaskInputData inputData) {
        ExtractorTask task = new ExtractorTask(inputData.getTilesetsName(), inputData.getMapFileName(), getMapLazyInitializer(inputData), inputData.getMinCompliance(), inputData.isHasDiff());
        validateTask(task);
        taskService.addTask(task);
        return new TaskView(task);
    }

    @PostMapping("task/local")
    public TaskView addTask(@RequestParam("file") MultipartFile file, @RequestParam String tilesetsName, @RequestParam int minCompliance, @RequestParam boolean hasDiff) {
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
    public List<String> getRemoteMaps() {
        return remoteMapsNames.get();
    }

    @ResponseBody
    @GetMapping("task/input")
    public void getInput(@RequestParam String id, HttpServletResponse response) throws IOException {
        val task = taskService.getTask(id);
        if (task.isPresent()) {
            val image = task.get().getInput();
            attachImage(response, image.get());
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

    private List<String> getMapNamesRemotely() {
        return imageHolderAccessor.getImagesByCategory("maps").stream()
                .map(ImageListView::getName)
                .filter(name -> !name.contains("dom") && !name.contains("jask"))
                .collect(Collectors.toList());
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
        if (minCompliance < 1 || minCompliance > 99) {
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

    private LazyInitializer<BufferedImage> getMapLazyInitializer(RemoteTaskInputData inputData) {
        verifyRemoteMapName(inputData.getMapFileName());
        return new LazyInitializer<>(() -> getRemoteMap(inputData));
    }

    private void verifyRemoteMapName(String mapFileName) {
        if (!remoteMapsNames.get().contains(mapFileName)) {
            throw new MapNotExistException();
        }
    }

    private BufferedImage getRemoteMap(RemoteTaskInputData inputData) {
        try {
            ImageDetailsView image = imageHolderAccessor.getImageByName(inputData.getMapFileName());
            ByteArrayInputStream bis = new ByteArrayInputStream(image.getContent());
            return ImageIO.read(bis);
        } catch (IOException | UnirestException e) {
            throw new MapNotExistException();
        }
    }

    private LazyInitializer<BufferedImage> getImage(MultipartFile file) {
        return new LazyInitializer<>(() -> {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                return ImageIO.read(bis);
            } catch (IOException e) {
                throw new MapNotExistException();
            }
        });
    }
}
