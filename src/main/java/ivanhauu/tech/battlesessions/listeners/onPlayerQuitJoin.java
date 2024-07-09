package ivanhauu.tech.battlesessions.listeners;

import ivanhauu.tech.battlesessions.BattleSessions;
import ivanhauu.tech.battlesessions.WorldManager;
import ivanhauu.tech.battlesessions.utils.GetPlayerRank;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Bukkit.getServer;

public class onPlayerQuitJoin implements Listener {

    private final BattleSessions plugin;
    private final WorldManager worldManager;
    private final GetPlayerRank getPlayerRank;

    public onPlayerQuitJoin(BattleSessions plugin, WorldManager worldManager, GetPlayerRank getPlayerRank) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.getPlayerRank = getPlayerRank;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World mundo = player.getWorld();
        World spawn_world = Bukkit.getWorld("spawn");

        if (mundo.getName().startsWith("battle_8v8_") || mundo.getName().startsWith("battle_4v4_")) {
            Location spawn = new Location(spawn_world, 8, 0, 8);
            player.teleport(spawn);
        }

        if (player.getWorld().getName().equals("world")) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
        }

        plugin.updatePlayersVisualization(player);

        event.setJoinMessage(null);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (mundo == p.getWorld()) {
                p.sendMessage(ChatColor.GREEN + player.getName() + " entrou no jogo!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.updatePlayersVisualization(event.getPlayer());
    }

    @EventHandler
    public void delEmptyWorlds(PlayerQuitEvent event) {
        getServer().getScheduler().runTaskLater(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                worldManager.deleteWorldIfEmpty(world.getName());
            }
        }, 20L);
    }
}
