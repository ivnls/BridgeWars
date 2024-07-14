package ivanhauu.tech.bridgewars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class WorldManager {
    private final JavaPlugin plugin;

    public WorldManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //Aqui ficam os métodos usados para manuzear o mundo das partidas

    public boolean cloneWorld(String originalWorldName, String newWorldName) {

        File originalWorldFolder = new File(Bukkit.getWorldContainer(), originalWorldName);
        File newWorldFolder = new File(Bukkit.getWorldContainer(), newWorldName);

        if (!originalWorldFolder.exists()) {
            plugin.getLogger().log(Level.SEVERE, "O mundo original não existe: " + originalWorldName);
            return false;
        }

        if (newWorldFolder.exists()) {
            plugin.getLogger().log(Level.SEVERE, "O mundo de destino já existe: " + newWorldName);
            return false;
        }

        try {
            copyFolder(originalWorldFolder, newWorldFolder);

            // Remove the unique id file to prevent conflicts
            File uidFile = new File(newWorldFolder, "uid.dat");
            if (uidFile.exists() && !uidFile.isDirectory()) {
                uidFile.delete();
            }
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao clonar o mundo", e);
            return false;
        }
    }

    public void loadWorld(String worldName) {
        WorldCreator wc = new WorldCreator(worldName);
        Bukkit.createWorld(wc);
    }

    private void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(source, file);
                    File destFile = new File(destination, file);
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void deleteWorldIfEmpty(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (!worldName.startsWith("battle_2v2_") && !worldName.startsWith("battle_4v4_")) {
            plugin.getLogger().info("O mundo " + worldName + " não corresponde ao padrão 'battle_2v2_' ou 'battle_4v4_'.");
            return;
        }

        if (world == null) {
            plugin.getLogger().warning("O mundo " + worldName + " não foi encontrado.");
            return;
        }

        if (world.getPlayers().isEmpty()) {
            Bukkit.unloadWorld(world, false);
            File worldFolder = world.getWorldFolder();
            deleteWorldFolder(worldFolder);
            Bukkit.getLogger().info("O mundo " + worldName + " foi excluído com sucesso.");
        } else {
            plugin.getLogger().info("O mundo " + worldName + " não pode ser excluído porque ainda há jogadores nele.");
        }
    }

    private void deleteWorldFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteWorldFolder(file);
                }
            }
        }
        folder.delete();
    }
}
