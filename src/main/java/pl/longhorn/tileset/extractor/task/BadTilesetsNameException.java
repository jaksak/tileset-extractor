package pl.longhorn.tileset.extractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadTilesetsNameException extends ResponseStatusException {

    public BadTilesetsNameException() {
        super(HttpStatus.BAD_REQUEST, "tilesetsName");
    }
}
