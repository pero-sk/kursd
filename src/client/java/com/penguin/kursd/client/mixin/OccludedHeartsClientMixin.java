package com.penguin.kursd.client.mixin;

import com.penguin.kursd.Ksd;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class OccludedHeartsClientMixin {

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void kursd$replaceHeartTexture(
        DrawContext context,
        PlayerEntity player,
        int x,
        int y,
        int lines,
        int regeneratingHeartIndex,
        float maxHealth,
        int lastHealth,
        int health,
        int absorption,
        boolean blinking,
        CallbackInfo ci
    ) {
        if (player.hasStatusEffect(Ksd.OCCLUDED)) {

            int heartCount = (int) Math.ceil(maxHealth / 2.0f);

            for (int i = 0; i < heartCount; i++) {
                int col = i % 10;
                int row = i / 10;
                int drawX = x + col * 8;
                int drawY = y - row * 10;

                // Background
                context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    Identifier.of("minecraft", "hud/heart/container"),
                    drawX, drawY, 9, 9
                );

                context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    Identifier.of("minecraft", "hud/heart/container_hardcore_blinking"),
                    drawX, drawY, 9, 9
                );
            }

            ci.cancel();
        }
    }
}