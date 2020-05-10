package pl.longhorn.tilesetextractor.task;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ExtractorTask {
    private String id;
    private ExtractorTaskStatus status;
    private String tilesetsName;
    private String inputName;
    private BufferedImage input;
    private BufferedImage result;
    private BufferedImage diff;
    private LocalDateTime time;
    private int minCompliance;
    private boolean hasDiff;

    public ExtractorTask(String tilesetsName, String inputName, BufferedImage input, int minCompliance, boolean hasDiff) {
        this.id = UUID.randomUUID().toString();
        this.status = ExtractorTaskStatus.PENDING;
        this.inputName = inputName;
        this.tilesetsName = tilesetsName;
        this.input = input;
        this.time = LocalDateTime.now();
        this.minCompliance = minCompliance;
        this.hasDiff = hasDiff;
    }
}
