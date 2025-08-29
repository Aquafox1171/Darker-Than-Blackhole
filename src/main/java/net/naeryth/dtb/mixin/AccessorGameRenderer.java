package net.naeryth.dtb.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface AccessorGameRenderer {
    @Accessor("postProcessorId")      Identifier dtb$getPostProcessorId();
    @Accessor("postProcessorId")      void dtb$setPostProcessorId(Identifier id);
    @Accessor("postProcessorEnabled") boolean dtb$getPostProcessorEnabled();
    @Accessor("postProcessorEnabled") void dtb$setPostProcessorEnabled(boolean enabled);
}
