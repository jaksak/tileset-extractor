package pl.longhorn.tilesetextractor.task;

import lombok.Getter;
import lombok.Setter;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ExtractorTask {
    private String id;
    private ExtractorTaskStatus status;
    private Tilesets tilesets;
    private BufferedImage mapImage;
    private BufferedImage result;
    private LocalDateTime time;

    public ExtractorTask(Tilesets tilesets, BufferedImage mapImage) {
        this.id = UUID.randomUUID().toString();
        this.status = ExtractorTaskStatus.PENDING;
        this.tilesets = tilesets;
        this.mapImage = mapImage;
        this.time = LocalDateTime.now();
    }
}
