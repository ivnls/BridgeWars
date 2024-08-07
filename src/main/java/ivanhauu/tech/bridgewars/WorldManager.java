package ivanhauu.tech.bridgewars;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldManager {
    private final BridgeWars plugin;

    public WorldManager(BridgeWars plugin) {
        this.plugin = plugin;
    }

    //Aqui ficam os métodos usados para manuzear o mundo das partidas

    public void loadWorld(String mode, int sectionNumber) {

        String worldPath = plugin.getDataFolder() + "/sections/" + mode + "/battle_" + mode + "_" + sectionNumber;

        WorldCreator wc = new WorldCreator(worldPath);
        Bukkit.createWorld(wc);
    }

    public void unloadWorld(String worldName) {
        Bukkit.unloadWorld(worldName, false);
    }

}