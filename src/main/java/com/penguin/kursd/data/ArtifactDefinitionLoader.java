package com.penguin.kursd.data;

import com.google.gson.*;
import net.minecraft.resource.ResourceManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ArtifactDefinitionLoader {

    public static void load(ResourceManager manager) {

        // Find all files under data/*/artifacts/*.json
        manager.findResources(
                "artifacts",
                path -> path.getPath().endsWith(".json")
        ).forEach((id, resource) -> {

            try (Reader reader = new InputStreamReader(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            )) {

                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                String name = json.get("name").getAsString();

                int chance = json.get("chance").getAsInt();

                JsonArray values = json.getAsJsonArray("values");

                List<String> items = new ArrayList<>();

                for (JsonElement el : values) {
                    items.add(el.getAsString());
                }

                ArtifactApplicableRegistry.register(name, chance, items);

                System.out.println("[KURSD] Loaded artifact definition: " + name);

            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to load artifact definition: " + id,
                        e
                );
            }
        });
    }
}