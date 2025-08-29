package net.naeryth.dtb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DTBConfigManager {
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("dtb.server.json");

    public static DTBConfig CURRENT = DTBConfig.DEFAULT; // (used if you later add S2C sync)

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (var r = Files.newBufferedReader(FILE)) {
                    var read = G.fromJson(r, DTBConfig.class);
                    if (read != null) CURRENT = read;
                }
            } else save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (var w = Files.newBufferedWriter(FILE)) { G.toJson(CURRENT, w); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
