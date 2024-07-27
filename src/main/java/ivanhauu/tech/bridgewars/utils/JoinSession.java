package ivanhauu.tech.bridgewars.utils;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class JoinSession {

    private final BridgeWars plugin;

    public JoinSession(BridgeWars plugin) {
        this.plugin = plugin;
    }

    public void sessionType(String mode, Player player) {

        player.sendMessage("Entrou no método");

        player.sendMessage("Passou verificação");

        for (World mundo : Bukkit.getWorlds()) {
            plugin.getLogger().info(mundo.getName());
            if (mundo.getName().startsWith(plugin.getDataFolder() + "/sections/"+ mode +"/battle_"+ mode +"_")) {
                boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is"+ mode +"BattleStarted");
                if (!is2v2BattleStarted) {
                    player.sendMessage("Entrando na partida " + mode + "...");
                    Location playerSpawn = new Location(mundo, -32, 6, 32);
                    player.teleport(playerSpawn);
                    return;
                }
            }
        }

        player.sendMessage("Todas as partidas "+ mode +" estão lotadas!");
    }
}
