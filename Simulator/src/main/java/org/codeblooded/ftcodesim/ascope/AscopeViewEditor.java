package org.codeblooded.ftcodesim.ascope;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import org.codeblooded.ftcodesim.physics.SeasonField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AscopeViewEditor extends JsonEditor {
    ArrayNode sources;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AscopeViewEditor(SeasonField seasonField) {
        super(getStateFile());
        sources = fieldView3D(seasonField);

        copyFolder(
                new File("../Simulator/src/main/resources/assets/Robot_CodeBloodedDecode").toPath(),
                new File(getAdvantageScopeFolder(), "userAssets").toPath());
    }

    public ArrayNode fieldView3D(SeasonField seasonField) {
        ObjectNode tabs = (ObjectNode) get("hubs/0/state/tabs");

        tabs.put("selected", 3);

        ArrayNode tabArray =
                (ArrayNode) tabs.get("tabs");

        for(JsonNode tab : tabArray) {
            if(
                    isEqualTo(tab, "controller/game", seasonField.ascopeName) &&
                    isEqualTo(tab, "title", "3D Field")
            ) {
                return (ArrayNode) get(tab, "controller/sources");
            }
        }

        System.out.println("adding 3d field");

        ObjectNode tab = mapper.createObjectNode();

        tab.put(
                "type",
                3
        );
        tab
                .put("title", "3D Field")
                .put("controllerUUID", "abcdefghijklmnopqrstuv1234567890");
        tab.set("controller", mapper.createObjectNode().put("game", seasonField.ascopeName).set("sources", mapper.createArrayNode()));
        //tab.set("renderer", mapper.createObjectNode());
        tab.put("controlsHeight", 100);
        tabArray.add(tab);

        return (ArrayNode) get(tab, "controller/sources");
    }

    public void addSource(
            String key,
            SourceType sourceType
    ) {
        for (JsonNode source : sources) {
            if(
                    isEqualTo(source, "logKey", key)
            ) {
                return;
            }
        }

        System.out.println("Adding source: " + key);

        ObjectNode source =
                mapper.createObjectNode();

        source
                .put("type", sourceType.type)
                .put("logKey", key)
                .put("logType", sourceType.logType)
                .put("visible", true)
                .set("options", sourceType.options);

        sources.add(source);
    }

    private static File getStateFile() {
        File folder = getAdvantageScopeFolder();

        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        File[] files = folder.listFiles((dir, name) ->
                name.startsWith("state-") && name.endsWith(".json"));

        if (files == null || files.length == 0) {
            return null;
        }

        return files[0];
    }

    private static File getAdvantageScopeFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return new File(appData, "AdvantageScope");
            }
            return new File(new File(new File(home, "AppData"), "Roaming"), "AdvantageScope");
        } else if (os.contains("mac")) {
            return new File(new File(new File(home, "Library"), "Application Support"), "AdvantageScope");
        } else {
            String xdg = System.getenv("XDG_CONFIG_HOME");
            if (xdg != null) {
                return new File(xdg, "AdvantageScope");
            }
            return new File(new File(home, ".config"), "AdvantageScope");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copyFolder(Path source, Path destination) {
        Path targetRoot = destination.resolve(source.getFileName());

        // Folder already exists, do nothing
        if (Files.exists(targetRoot)) {
            return;
        }

        System.out.println("Copying " + source + " to " + targetRoot);

        try {
            Files.walk(source).forEach(path -> {
                try {
                    Path target = targetRoot.resolve(source.relativize(path));

                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(path, target);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.out.println("Working directory: " + System.getProperty("user.dir"));
            throw new RuntimeException(e);
        }
    }
}