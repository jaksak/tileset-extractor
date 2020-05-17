package pl.longhorn.tilesetextractor.data.migration;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import pl.longhorn.data.holder.client.image.ImageHolderAccessor;
import pl.longhorn.data.holder.client.image.ImageHolderAccessorImpl;
import pl.longhorn.data.holder.common.image.ImageExtension;
import pl.longhorn.data.holder.common.image.ImageInputData;
import pl.longhorn.data.holder.common.image.LocalNameInputData;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.ProjectConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public class DataMigrator implements CommandLineRunner {

    private final ImageHolderAccessor imageHolderAccessor = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);

    @Override
    public void run(String... args) throws Exception {
        imageHolderAccessor.addContext(ProjectConfig.IMAGE_CONTEXT);
        String tilesetsName = "user";
        Files.walk(ImageHelper.getResourcePath("tilesets/" + tilesetsName))
                .filter(path -> Files.isRegularFile(path))
                .forEach(path -> addImage(path, tilesetsName));
        System.out.println("Finish adding all data!");
    }

    @SneakyThrows
    private void addImage(Path path, String tilesetsName) {
        val content = Files.readAllBytes(path);
        val imageInputData = ImageInputData.builder()
                .content(content)
                .extension(ImageExtension.PNG)
                .build();
        val imageId = imageHolderAccessor.addImage(imageInputData);
        val localNameInputData = LocalNameInputData.builder()
                .contextName(ProjectConfig.IMAGE_CONTEXT)
                .imageId(imageId)
                .name(getName(path, tilesetsName))
                .category("tilesets")
                .build();
        imageHolderAccessor.addLocalName(localNameInputData);
    }

    private String getName(Path path, String tilesetsName) {
        val fullName = path.getFileName().toString();
        return tilesetsName + " " + fullName;
    }
}
