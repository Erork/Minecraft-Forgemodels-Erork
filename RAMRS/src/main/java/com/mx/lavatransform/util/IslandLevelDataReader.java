package com.mx.lavatransform.util;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class IslandLevelDataReader {
    private static final Path LEVEL_FILE = Paths.get("world", "island_data.json");

    public static Integer getGlobalLevel() {
        try {
            if (!Files.exists(LEVEL_FILE)) return null;

            try (Reader reader = Files.newBufferedReader(LEVEL_FILE)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (root.has("global_level")) {
                    return root.get("global_level").getAsInt();
                }
            }
        } catch (Exception e) {
            System.err.println("[LavaTransform] 读取 global_level 失败：" + e.getMessage());
        }
        return null;
    }
}

