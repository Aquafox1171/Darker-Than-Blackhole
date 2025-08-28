package net.naeryth.dtb.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.effect.StatusEffects;
import net.naeryth.dtb.DTBClientState;
import net.naeryth.dtb.DTBConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapMixin {

    @Shadow private NativeImage image;

    private static int TD_TICK = 0;
    private static float clamp01(float v){ return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static String tdRGBA(int abgr) {
        int a = (abgr >>> 24) & 0xFF, b = (abgr >>> 16) & 0xFF, g = (abgr >>> 8) & 0xFF, r = (abgr) & 0xFF;
        return String.format("A%02X R%02X G%02X B%02X", a, r, g, b);
    }

    @Inject(method = "update(F)V", at = @At("HEAD"))
    private void td$logHead(float delta, CallbackInfo ci) {
        if ((TD_TICK++ % 60) == 0) System.out.println("[TD] Lightmap.update HEAD delta=" + delta);
    }

    @Inject(
            method = "update(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/NativeImageBackedTexture;upload()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void td$beforeUpload(float delta, CallbackInfo ci) {
        if (image == null) { System.out.println("[TD] beforeUpload: image == null"); return; }

        final MinecraftClient mc = MinecraftClient.getInstance();
        final DTBConfig cfg = DTBClientState.active;
        final boolean nv = mc.player != null && mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION);

        // Tunables (from server)
        final float strength = clamp01(cfg.strength());      // 0..1 (try 0.35–0.65)
        final float gamma    = Math.max(1.0f, cfg.gamma());  // try 1.6–2.4
        final float knee     = 0.60f;                        // only darken pixels with luminance < knee

        if ((TD_TICK % 60) == 0) {
            System.out.println("[TD] cfg enabled=" + cfg.enabled() +
                    " strength=" + strength +
                    " gamma=" + gamma +
                    " respectNV=" + cfg.respectNightVision() +
                    " nvActive=" + nv +
                    " size=" + image.getWidth() + "x" + image.getHeight());
        }

        if (!cfg.enabled()) return;
        if (nv && cfg.respectNightVision()) return;

        final int w = image.getWidth(), h = image.getHeight();
        int sx = Math.min(15, w - 1), sy = Math.min(15, h - 1);
        int before = image.getColor(sx, sy);

        int darkened = 0, kept = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int abgr = image.getColor(x, y);
                int a = (abgr >>> 24) & 0xFF;
                int b = (abgr >>> 16) & 0xFF;
                int g = (abgr >>> 8)  & 0xFF;
                int r = (abgr)        & 0xFF;

                float rf = r / 255f, gf = g / 255f, bf = b / 255f;
                float lum = 0.2126f * rf + 0.7152f * gf + 0.0722f * bf;

                float scale;
                if (lum < knee) {
                    // Normalize into 0..1 under the knee, apply curve/push there only
                    float t = (knee > 1e-6f) ? clamp01(lum / knee) : 0f;
                    float curved = (float)Math.pow(t, gamma);
                    float push   = strength * (1f - (float)Math.pow(t, 0.25f));
                    float t2     = Math.max(0f, curved - push);
                    float target = t2 * knee; // back to 0..knee
                    scale = (lum > 1e-6f) ? clamp01(target / lum) : 0f;
                    darkened++;
                } else {
                    scale = 1f; // keep bright cells intact
                    kept++;
                }

                rf *= scale; gf *= scale; bf *= scale;

                int nr = (int)(rf * 255f + 0.5f);
                int ng = (int)(gf * 255f + 0.5f);
                int nb = (int)(bf * 255f + 0.5f);

                // ABGR
                int newAbgr = (a << 24) | (nb << 16) | (ng << 8) | nr;
                image.setColor(x, y, newAbgr);
            }
        }

        if ((TD_TICK % 60) == 0) {
            int after = image.getColor(sx, sy);
            System.out.println("[TD] sample(" + sx + "," + sy + ") before=" + tdRGBA(before) +
                    " after=" + tdRGBA(after) + " | darkened=" + darkened + " kept=" + kept);
        }
    }
}
