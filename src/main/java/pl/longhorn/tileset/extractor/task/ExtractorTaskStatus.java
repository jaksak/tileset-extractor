package pl.longhorn.tileset.extractor.task;

public enum ExtractorTaskStatus {
    PENDING,
    PREPARE_TILESETS,
    DOWNLOAD_MAP,
    IN_PROGRESS,
    PREPARE_DIFF,
    FINISHED,
    ERROR
}
