package net.naeryth.dtb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DTBConfigManager {
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("true_darkness.server.json");
    public static DTBConfig CURRENT = DTBConfig.DEFAULT;

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (var r = Files.newBufferedReader(FILE)) {CURRENT = G.fromJson(r, DTBConfig.class);};
            } else {
                save(); // write defaults on first run
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void save() {
        try (var w = Files.newBufferedWriter(FILE)) { G.toJson(CURRENT, w); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
