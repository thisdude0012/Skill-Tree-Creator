/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonIOException
 *  com.google.gson.stream.JsonReader
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package daripher.skilltree.client.data;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

public class SkillTreeClientData {
    private static final Map<ResourceLocation, PassiveSkill> EDITOR_PASSIVE_SKILLS = new HashMap<ResourceLocation, PassiveSkill>();
    private static final Map<ResourceLocation, PassiveSkillTree> EDITOR_TREES = new HashMap<ResourceLocation, PassiveSkillTree>();
    private static final Set<ResourceLocation> EDITOR_TREES_IDS = new HashSet<ResourceLocation>();
    private static boolean loadedIDs = false;

    public static PassiveSkill getEditorSkill(ResourceLocation id) {
        return EDITOR_PASSIVE_SKILLS.get(id);
    }

    @Nullable
    public static PassiveSkillTree getOrCreateEditorTree(ResourceLocation treeId) {
        try {
            PassiveSkillTree skillTree;
            File mcmetaFile;
            File folder = SkillTreeClientData.getSkillTreeSavesFolder(treeId);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!(mcmetaFile = new File(SkillTreeClientData.getEditorFolder(), "pack.mcmeta")).exists()) {
                SkillTreeClientData.generatePackMcmetaFile(mcmetaFile);
            }
            if (!SkillTreeClientData.getSkillTreeSaveFile(treeId).exists()) {
                skillTree = SkillTreesReloader.getSkillTreeById(treeId);
                SkillTreeClientData.saveEditorSkillTree(skillTree);
            }
            if (!EDITOR_TREES.containsKey(treeId)) {
                SkillTreeClientData.loadEditorSkillTree(treeId);
            }
            if (!EDITOR_TREES.containsKey(treeId)) {
                EDITOR_TREES_IDS.add(treeId);
            }
            skillTree = EDITOR_TREES.getOrDefault(treeId, new PassiveSkillTree(treeId));
            for (ResourceLocation skillId : skillTree.getSkillIds()) {
                try {
                    SkillTreeClientData.loadOrCreateEditorSkill(skillId);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    SkillTreeClientData.printMessage("Couldn't read passive skill " + skillId, ChatFormatting.DARK_RED);
                    SkillTreeClientData.printMessage("", new ChatFormatting[0]);
                    String errorMessage = exception.getMessage() == null ? "No error message" : exception.getMessage();
                    SkillTreeClientData.printMessage(errorMessage, ChatFormatting.RED);
                    return null;
                }
            }
            return skillTree;
        }
        catch (Exception exception) {
            EDITOR_TREES.clear();
            EDITOR_PASSIVE_SKILLS.clear();
            SkillTreeClientData.printMessage("Couldn't read skill tree " + treeId, ChatFormatting.DARK_RED);
            SkillTreeClientData.printMessage("", new ChatFormatting[0]);
            String errorMessage = exception.getMessage() == null ? "No error message" : exception.getMessage();
            SkillTreeClientData.printMessage(errorMessage, ChatFormatting.RED);
            SkillTreeClientData.printMessage("", new ChatFormatting[0]);
            SkillTreeClientData.printMessage("Try removing files from folder", ChatFormatting.DARK_RED);
            SkillTreeClientData.printMessage("", new ChatFormatting[0]);
            SkillTreeClientData.printMessage(SkillTreeClientData.getEditorDataFolder().getPath(), ChatFormatting.RED);
            exception.printStackTrace();
            return null;
        }
    }

    private static void generatePackMcmetaFile(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String contents = "{\n  \"pack\": {\n    \"description\": {\n      \"text\": \"PST editor data\"\n    },\n    \"pack_format\": 15\n  }\n}\n";
            writer.write(contents);
            writer.close();
        }
        catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    private static void loadOrCreateEditorSkill(ResourceLocation skillId) {
        PassiveSkill skill;
        File skillSavesFolder = SkillTreeClientData.getSkillSavesFolder(skillId);
        if (!skillSavesFolder.exists()) {
            skillSavesFolder.mkdirs();
        }
        if (!SkillTreeClientData.getSkillSaveFile(skillId).exists() && (skill = SkillsReloader.getSkillById(skillId)) != null) {
            SkillTreeClientData.saveEditorSkill(skill);
        }
        if (!EDITOR_PASSIVE_SKILLS.containsKey(skillId)) {
            SkillTreeClientData.loadEditorSkill(skillId);
        }
    }

    public static void saveEditorSkillTree(PassiveSkillTree skillTree) {
        File file = SkillTreeClientData.getSkillTreeSaveFile(skillTree.getId());
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);){
            SkillTreesReloader.GSON.toJson((Object)skillTree, (Appendable)writer);
        }
        catch (JsonIOException | IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't save editor skill tree " + skillTree.getId());
        }
    }

    public static void loadEditorSkillTree(ResourceLocation treeId) throws IOException {
        PassiveSkillTree skillTree;
        File file = SkillTreeClientData.getSkillTreeSaveFile(treeId);
        try {
            skillTree = SkillTreeClientData.readFromFile(PassiveSkillTree.class, file);
        }
        catch (Exception exception) {
            PassiveSkillTree skillTree2 = new PassiveSkillTree(treeId);
            SkillTreeClientData.saveEditorSkillTree(skillTree2);
            EDITOR_TREES.put(treeId, skillTree2);
            throw exception;
        }
        EDITOR_TREES.put(treeId, skillTree);
    }

    public static void saveEditorSkill(PassiveSkill skill) {
        File file = SkillTreeClientData.getSkillSaveFile(skill.getId());
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);){
            SkillsReloader.GSON.toJson((Object)skill, (Appendable)writer);
        }
        catch (JsonIOException | IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Can't save editor skill " + skill.getId());
        }
    }

    public static void loadEditorSkill(ResourceLocation skillId) {
        PassiveSkill skill;
        try {
            skill = SkillTreeClientData.readFromFile(PassiveSkill.class, SkillTreeClientData.getSkillSaveFile(skillId));
        }
        catch (IOException exception) {
            exception.printStackTrace();
            SkillTreeClientData.printMessage("Can't load editor skill " + skillId, ChatFormatting.DARK_RED);
            throw new RuntimeException("Can't load editor skill " + skillId);
        }
        EDITOR_PASSIVE_SKILLS.put(skillId, skill);
    }

    public static void deleteEditorSkill(PassiveSkill skill) {
        SkillTreeClientData.getSkillSaveFile(skill.getId()).delete();
        EDITOR_PASSIVE_SKILLS.remove(skill.getId());
    }

    private static File getEditorDataFolder() {
        return new File(SkillTreeClientData.getEditorFolder(), "data");
    }

    private static File getEditorFolder() {
        return new File(FMLPaths.GAMEDIR.get().toFile(), "skilltree/editor");
    }

    private static File getSkillSavesFolder(ResourceLocation skillId) {
        return new File(SkillTreeClientData.getEditorDataFolder(), skillId.m_135827_() + "/skills");
    }

    private static File getSkillTreeSavesFolder(ResourceLocation skillTreeId) {
        return new File(SkillTreeClientData.getEditorDataFolder(), skillTreeId.m_135827_() + "/skill_trees");
    }

    private static File getSkillSaveFile(ResourceLocation skillId) {
        return new File(SkillTreeClientData.getSkillSavesFolder(skillId), skillId.m_135815_() + ".json");
    }

    private static File getSkillTreeSaveFile(ResourceLocation skillTreeId) {
        return new File(SkillTreeClientData.getSkillTreeSavesFolder(skillTreeId), skillTreeId.m_135815_() + ".json");
    }

    private static <T> T readFromFile(Class<T> objectType, File file) throws IOException {
        try (JsonReader reader = new JsonReader((Reader)new FileReader(file, StandardCharsets.UTF_8));){
            Object object = SkillsReloader.GSON.fromJson(reader, objectType);
            return (T)object;
        }
    }

    public static void printMessage(String text, ChatFormatting ... styles) {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null) {
            MutableComponent component = Component.m_237113_((String)text);
            for (ChatFormatting style : styles) {
                component.m_130940_(style);
            }
            player.m_213846_((Component)component);
        }
    }

    public static Set<ResourceLocation> getEditorTreesIDs() {
        if (loadedIDs) {
            return EDITOR_TREES_IDS;
        }
        File dataFolder = SkillTreeClientData.getEditorDataFolder();
        File[] dataFiles = dataFolder.listFiles();
        if (!dataFolder.exists() || dataFiles == null) {
            return EDITOR_TREES_IDS;
        }
        for (File namespaceDirectory : dataFiles) {
            File[] skillTreeFiles;
            File skillTreesDirectory;
            if (!namespaceDirectory.isDirectory() || !(skillTreesDirectory = new File(namespaceDirectory, "skill_trees")).exists() || (skillTreeFiles = skillTreesDirectory.listFiles()) == null) continue;
            String namespace = namespaceDirectory.getName();
            for (File skillTreeFile : skillTreeFiles) {
                String skillTreeFileName = skillTreeFile.getName();
                if (!skillTreeFileName.endsWith(".json")) continue;
                String skillTreeName = skillTreeFileName.substring(0, skillTreeFileName.lastIndexOf(46));
                EDITOR_TREES_IDS.add(new ResourceLocation(namespace, skillTreeName));
            }
        }
        loadedIDs = true;
        return EDITOR_TREES_IDS;
    }
}

