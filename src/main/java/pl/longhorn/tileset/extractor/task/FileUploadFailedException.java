package pl.longhorn.tileset.extractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileUploadFailedException extends ResponseStatusException {
    public FileUploadFailedException() {
        super(HttpStatus.BAD_REQUEST, "fileUpload");
    }
}
