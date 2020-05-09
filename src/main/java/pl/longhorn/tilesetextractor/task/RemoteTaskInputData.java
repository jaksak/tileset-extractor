package pl.longhorn.tilesetextractor.task;

import lombok.Value;

@Value
public class RemoteTaskInputData {
    private String mapFileName;
    private String tilesetsName;
}
