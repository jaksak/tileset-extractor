package pl.longhorn.tileset.extractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MapNotExistException extends ResponseStatusException {

    protected MapNotExistException() {
        super(HttpStatus.BAD_REQUEST, "mapName");
    }
}
