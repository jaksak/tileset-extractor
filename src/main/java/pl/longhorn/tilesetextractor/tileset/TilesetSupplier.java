package pl.longhorn.tilesetextractor.tileset;

import lombok.val;
import org.springframework.stereotype.Component;
import pl.longhorn.data.holder.client.image.ImageHolderAccessorImpl;
import pl.longhorn.data.holder.client.util.ImageHelper;
import pl.longhorn.data.holder.common.image.ImageDetailsView;
import pl.longhorn.data.holder.common.image.ImageListView;
import pl.longhorn.tilesetextractor.ProjectConfig;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Component
public class TilesetSupplier {

    private Map<String, List<BufferedImage>> tilesestsImagesByCategory = new HashMap<>();
    private Map<String, Tilesets> tilesetsByCategory = new HashMap<>();

    public TilesetSupplier() {
        val imageHolder = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);
        imageHolder.getImagesByCategory("tilesets").forEach(view -> assignByCategory(view, imageHolder));
    }

    private void assignByCategory(ImageListView listView, ImageHolderAccessorImpl imageHolder) {
        int categoryEndPosition = listView.getName().indexOf(" ");
        if (categoryEndPosition > -1) {
            String categoryName = listView.getName().substring(0, categoryEndPosition);
            try {
                addImage(listView, imageHolder, categoryName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Invalid name: " + listView.getName());
        }
    }

    private void addImage(ImageListView listView, ImageHolderAccessorImpl imageHolder, String categoryName) throws IOException {
        val images = tilesestsImagesByCategory.getOrDefault(categoryName, new LinkedList<>());
        ImageDetailsView detailsView = imageHolder.getImageById(listView.getId());
        val image = ImageHelper.getBufferedImage(detailsView);
        images.add(image);
        tilesestsImagesByCategory.put(categoryName, images);
    }

    public Tilesets getTilesets(String tilesetsName) {
        Tilesets tilesets = tilesetsByCategory.get(tilesetsName);
        return Objects.requireNonNullElseGet(tilesets, () -> computeTilesets(tilesetsName));
    }

    private Tilesets computeTilesets(String tilesetsName) {
        List<BufferedImage> imageForTilesets = tilesestsImagesByCategory.get(tilesetsName);
        if (imageForTilesets == null) {
            throw new RuntimeException("Tileset not exist " + tilesetsName);
        } else {
            Tilesets tilesets = new Tilesets(imageForTilesets);
            tilesetsByCategory.put(tilesetsName, tilesets);
            return tilesets;
        }
    }

    public boolean hasTilesetsCategory(String tilesetsName) {
        return tilesetsByCategory.containsKey(tilesetsName) || tilesestsImagesByCategory.containsKey(tilesetsName);
    }
}
