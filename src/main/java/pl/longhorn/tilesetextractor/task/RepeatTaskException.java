package pl.longhorn.tilesetextractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RepeatTaskException extends ResponseStatusException {
    protected RepeatTaskException() {
        super(HttpStatus.BAD_REQUEST, "repeatedTask");
    }
}
