package pl.longhorn.tilesetextractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class MapNotExistException extends HttpStatusCodeException {

    protected MapNotExistException() {
        super(HttpStatus.BAD_REQUEST, "mapName");
    }
}
