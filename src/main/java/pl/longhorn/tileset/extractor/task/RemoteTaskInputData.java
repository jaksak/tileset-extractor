package pl.longhorn.tileset.extractor.task;

import lombok.Value;

@Value
public class RemoteTaskInputData {
    private String mapFileName;
    private String tilesetsName;
    private int minCompliance;
    private boolean hasDiff;
}
