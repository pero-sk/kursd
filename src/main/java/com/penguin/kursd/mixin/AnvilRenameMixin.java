package com.penguin.kursd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.penguin.kursd.curse.ArtifactData;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;

@Mixin(AnvilScreenHandler.class)
public class AnvilRenameMixin {

    @Inject(
        method = "updateResult",
        at = @At("HEAD"),
        cancellable = true
    )
    private void blockRename(CallbackInfo ci) {

        AnvilScreenHandler self = (AnvilScreenHandler)(Object)this;

        ItemStack left = self.getSlot(0).getStack();

        ArtifactData data = ArtifactData.readFrom(left);

        if (data == null) return;

        if (!data.isAwoken()) {
            ci.cancel();

            self.getSlot(2).setStack(ItemStack.EMPTY);
        }
    }
}