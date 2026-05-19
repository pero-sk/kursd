package com.penguin.kursd.data;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class ArtifactDatapackLoader implements SimpleSynchronousResourceReloadListener {

    @SuppressWarnings("null")
    @Override
    public Identifier getFabricId() {
        return Identifier.of("kursd", "artifact_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        ArtifactDefinitionLoader.load(manager);
    }
}