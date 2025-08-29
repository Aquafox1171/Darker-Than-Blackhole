package net.naeryth.dtb;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.util.Identifier;
import net.naeryth.dtb.mixin.AccessorGameRenderer;

public final class DTBPostLoader {
    // Try in this order: short id, new-folder id, old full path id
    private static final Identifier[] CANDIDATES = new Identifier[]{
            Identifier.of("dtb", "dtb_darkspots"),                 // new schema short id
            Identifier.of("dtb", "post_effect/dtb_darkspots"),     // new schema full path
        Identifier.of("dtb", "shaders/post/dtb_darkspots.json")
    };

    private Identifier resolvedId = null;

    public void enable(MinecraftClient client) {
        if (resolvedId == null) {
            resolvedId = probe(client);
            if (resolvedId == null) {
                System.err.println("[DTB] No working post-effect id found. Check resource files.");
                return;
            }
        }
        ((AccessorGameRenderer) client.gameRenderer).dtb$setPostProcessorId(resolvedId);
        ((AccessorGameRenderer) client.gameRenderer).dtb$setPostProcessorEnabled(true);
        System.out.println("[DTB] Enabled post id = " + resolvedId);
    }

    public void disable(MinecraftClient client) {
        ((AccessorGameRenderer) client.gameRenderer).dtb$setPostProcessorEnabled(false);
        ((AccessorGameRenderer) client.gameRenderer).dtb$setPostProcessorId(null);
        System.out.println("[DTB] Disabled post effect");
    }

    /** Try to actually load each candidate via ShaderLoader to find the one your env accepts. */
    private Identifier probe(MinecraftClient client) {
        ShaderLoader loader = client.getShaderLoader();
        for (Identifier id : CANDIDATES) {
            try {
                PostEffectProcessor tmp = loader.loadPostEffect(id, DefaultFramebufferSet.MAIN_ONLY);
                if (tmp != null) {
                    // try to dispose if a close() exists in this mapping; ignore if not
                    try {
                        var m = tmp.getClass().getMethod("close");
                        m.invoke(tmp);
                    } catch (ReflectiveOperationException ignored) {
                        // no close() in this mapping; OK to skip — probe is short-lived
                    }
                    System.out.println("[DTB] Resolved working post id: " + id);
                    return id;
                }
            } catch (Throwable t) {
                // keep trying other candidates
            }
        }
        return null;
    }

    /** Call this on resource reload to force re-probing next time. */
    public void invalidate() { resolvedId = null; }
}
