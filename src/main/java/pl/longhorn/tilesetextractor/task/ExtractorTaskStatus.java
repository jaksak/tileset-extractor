package pl.longhorn.tilesetextractor.task;

public enum ExtractorTaskStatus {
    PENDING,
    PREPARE_TILESETS,
    DOWNLOAD_MAP,
    IN_PROGRESS,
    PREPARE_DIFF,
    FINISHED,
    ERROR
}
