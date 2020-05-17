package pl.longhorn.tilesetextractor.data.migration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.tinylog.Logger;
import pl.longhorn.data.holder.client.exception.InvalidDataHolderResponseException;
import pl.longhorn.data.holder.client.image.ImageHolderAccessor;
import pl.longhorn.data.holder.client.image.ImageHolderAccessorImpl;
import pl.longhorn.data.holder.client.text.TextHolderAccessor;
import pl.longhorn.data.holder.client.text.TextHolderAccessorImpl;
import pl.longhorn.data.holder.common.image.ImageListView;
import pl.longhorn.data.holder.common.image.UpdateLocalNameInputData;
import pl.longhorn.data.holder.common.text.TextMetaInputData;
import pl.longhorn.tilesetextractor.ProjectConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DataMigrator implements CommandLineRunner {

    private final ImageHolderAccessor imageHolderAccessor = new ImageHolderAccessorImpl(ProjectConfig.IMAGE_CONTEXT);
    private final TextHolderAccessor textHolderAccessor = new TextHolderAccessorImpl(new ObjectMapper(), ProjectConfig.IMAGE_CONTEXT);

    @Override
    public void run(String... args) throws Exception {
        Map<String, LinkedList<String>> tilesetsByCategory = getTilesetsByCategory();
        tilesetsByCategory.entrySet().forEach(this::save);
        fixNames();
    }

    private void fixNames() {
        imageHolderAccessor.getImagesByCategory("tileset").forEach(this::fixName);
    }

    private void fixName(ImageListView listView) {
        int originalNamePosition = listView.getName().indexOf(" ") + 1;
        if (originalNamePosition > 0) {
            updateName(listView, listView.getName().substring(originalNamePosition));
        }
    }

    private void updateName(ImageListView listView, String name) {
        try {
            val inputData = UpdateLocalNameInputData.builder()
                    .id(listView.getNameId())
                    .category("tileset")
                    .imageId(listView.getId())
                    .name(name)
                    .build();

            imageHolderAccessor.updateLocalName(inputData);
        } catch (InvalidDataHolderResponseException e) {
            imageHolderAccessor.deleteLocalName(listView.getNameId());
        }
    }

    private void save(Map.Entry<String, LinkedList<String>> categoryWithImageIds) {
        TextMetaInputData textMetaData = TextMetaInputData.builder()
                .name(categoryWithImageIds.getKey())
                .category("tileset")
                .build();
        try {
            textHolderAccessor.save(categoryWithImageIds.getValue(), textMetaData);
        } catch (JsonProcessingException e) {
            Logger.error(e);
        }
    }

    private Map<String, LinkedList<String>> getTilesetsByCategory() {
        Map<String, LinkedList<String>> result = new HashMap<>();
        imageHolderAccessor.getImagesByCategory("tileset").forEach(view -> {
            String category = getCategoryName(view);
            LinkedList<String> tilesets = result.getOrDefault(category, new LinkedList<>());
            tilesets.add(view.getId());
            result.put(category, tilesets);
        });
        return result;
    }

    private String getCategoryName(ImageListView listView) {
        int categoryEndPosition = listView.getName().indexOf(" ");
        if (categoryEndPosition > -1) {
            return listView.getName().substring(0, categoryEndPosition);
        } else {
            throw new RuntimeException("Invalid name: " + listView.getName());
        }
    }
}