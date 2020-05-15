package pl.longhorn.tilesetextractor.data.migration;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import pl.longhorn.imageholderclient.ImageHolderAccessor;
import pl.longhorn.imageholderclient.ImageHolderAccessorImpl;
import pl.longhorn.imageholdercommon.ImageExtension;
import pl.longhorn.imageholdercommon.ImageInputData;
import pl.longhorn.imageholdercommon.LocalNameInputData;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.ProjectConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public class DataMigrator implements CommandLineRunner {

    private final ImageHolderAccessor imageHolderAccessor = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);

    @Override
    public void run(String... args) throws Exception {
        imageHolderAccessor.addContext(ProjectConfig.IMAGE_CONTEXT);
        Files.walk(ImageHelper.getResourcePath("maps"))
                .filter(path -> Files.isRegularFile(path))
                .forEach(this::addImage);
        System.out.println("Finish adding all data!");
    }

    @SneakyThrows
    private void addImage(Path path) {
        val content = Files.readAllBytes(path);
        val imageInputData = ImageInputData.builder()
                .content(content)
                .extension(ImageExtension.PNG)
                .build();
        val imageId = imageHolderAccessor.addImage(imageInputData);
        val localNameInputData = LocalNameInputData.builder()
                .contextName(ProjectConfig.IMAGE_CONTEXT)
                .imageId(imageId)
                .name(getBaseName(path))
                .category("maps")
                .build();
        imageHolderAccessor.addLocalName(localNameInputData);
    }

    private String getBaseName(Path path) {
        val fullName = path.getFileName().toString();
        return fullName.substring(0, fullName.lastIndexOf('.'));
    }
}
