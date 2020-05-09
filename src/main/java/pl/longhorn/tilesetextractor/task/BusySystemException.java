package pl.longhorn.tilesetextractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BusySystemException extends ResponseStatusException {
    public BusySystemException() {
        super(HttpStatus.BAD_REQUEST, "busy");
    }
}
