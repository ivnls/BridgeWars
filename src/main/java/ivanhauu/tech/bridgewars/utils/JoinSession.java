package ivanhauu.tech.bridgewars.utils;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class JoinSession {

    private final BridgeWars plugin;

    public JoinSession(BridgeWars plugin) {
        this.plugin = plugin;
    }

    public void sessionType(String mode, Player player) {

        for (World mundo : Bukkit.getWorlds()) {
            plugin.getLogger().info(mundo.getName());
            if (mundo.getName().startsWith(plugin.getDataFolder() + "/sections/"+ mode +"/battle_"+ mode +"_")) {
                boolean isBattleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is"+ mode +"BattleStarted");
                if (!isBattleStarted) {
                    player.sendMessage("§6[BW-INFO] §6Entrando na partida " + mode + "...");
                    Location playerSpawn = new Location(mundo, -32, 6, 32);
                    player.teleport(playerSpawn);
                    return;
                }
            }
        }

        player.sendMessage("§6[BW-INFO] §cTodas as partidas "+ mode +" estão lotadas!");
    }
}
