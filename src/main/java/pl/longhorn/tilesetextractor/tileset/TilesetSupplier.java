package pl.longhorn.tilesetextractor.tileset;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.GenericType;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import pl.longhorn.data.holder.client.exception.InvalidDataHolderResponseException;
import pl.longhorn.data.holder.client.image.ImageHolderAccessor;
import pl.longhorn.data.holder.client.image.ImageHolderAccessorImpl;
import pl.longhorn.data.holder.client.text.TextHolderAccessor;
import pl.longhorn.data.holder.client.text.TextHolderAccessorImpl;
import pl.longhorn.data.holder.client.util.ImageHelper;
import pl.longhorn.tilesetextractor.ProjectConfig;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TilesetSupplier {

    private final Map<String, Tilesets> tilesetsByCategory = new HashMap<>();
    private final Map<String, List<BufferedImage>> notParsedImagesByCategory = new HashMap<>();
    private final ImageHolderAccessor imageHolderAccessor = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);
    private final TextHolderAccessor textHolderAccessor = new TextHolderAccessorImpl(new ObjectMapper(), ProjectConfig.IMAGE_CONTEXT);

    public Tilesets getTilesets(String tilesetsName) {
        Tilesets tilesets = tilesetsByCategory.get(tilesetsName);
        if (tilesets == null) {
            return computeTilesets(tilesetsName);
        } else {
            return tilesets;
        }
    }

    private Tilesets computeTilesets(String tilesetsName) {
        List<BufferedImage> images = notParsedImagesByCategory.get(tilesetsName);
        if (images == null) {
            images = getRemoteImages(tilesetsName);
        }
        Tilesets tilesets = new Tilesets(images);
        tilesetsByCategory.put(tilesetsName, tilesets);
        return tilesets;
    }

    private List<BufferedImage> getRemoteImages(String tilesetsName) {
        try {
            val imageIds = textHolderAccessor.getByName(tilesetsName, new GenericType<List<String>>() {
            });
            return imageIds.stream()
                    .map(this::getImage)
                    .collect(Collectors.toList());
        } catch (InvalidDataHolderResponseException e) {
            return List.of();
        }
    }

    private BufferedImage getImage(String id) {
        try {
            return ImageHelper.getBufferedImage(imageHolderAccessor.getImageById(id));
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }

    public boolean hasTilesetsCategory(String tilesetsName) {
        if (tilesetsByCategory.containsKey(tilesetsName) || notParsedImagesByCategory.containsKey(tilesetsName)) {
            return true;
        } else {
            val imageIds = getRemoteImages(tilesetsName);
            if (imageIds.size() == 0) {
                return false;
            } else {
                notParsedImagesByCategory.put(tilesetsName, imageIds);
                return true;
            }
        }
    }
}
