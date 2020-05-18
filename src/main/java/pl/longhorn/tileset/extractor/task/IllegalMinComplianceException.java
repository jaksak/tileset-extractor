package pl.longhorn.tileset.extractor.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IllegalMinComplianceException extends ResponseStatusException {

    public IllegalMinComplianceException() {
        super(HttpStatus.BAD_REQUEST, "illegalMinCompliance");
    }
}
