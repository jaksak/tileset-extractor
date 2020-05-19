package pl.longhorn.tileset.extractor.error;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@AllArgsConstructor
public class ExceptionResponse implements Serializable {
    private String message;
}
