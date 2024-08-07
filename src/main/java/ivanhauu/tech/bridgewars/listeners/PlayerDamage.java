package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.PlayerWinner;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {

    private final BridgeWars plugin;
    private final PlayerWinner playerWinner;

    private String sectionsBaseFolder;
    private String subFolder2v2;
    private String subFolder4v4;
    private String battleWorldName2v2;
    private String battleWorldName4v4;

    public PlayerDamage(BridgeWars plugin, PlayerWinner playerWinner) {
        this.plugin = plugin;
        this.playerWinner = playerWinner;

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.battleWorldName2v2 = plugin.getBattleWorldName2v2();
        this.battleWorldName4v4 = plugin.getBattleWorldName4v4();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        String eventWorldName = event.getEntity().getWorld().getName();
        Player player = null;

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        } else {
            return;
        }

        if (eventWorldName.startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {
            boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".is4v4BattleStarted");
            if (!is4v4BattleStarted) {
                event.setCancelled(true);
            }

        } else if (eventWorldName.startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2)) {
            boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".is2v2BattleStarted");
            if (!is2v2BattleStarted) {
                event.setCancelled(true);
            }
        }

        Location iSpawn = new Location(player.getWorld(), -32, 50, 32);
        String deathMessage = "§cO jogador §6" + player.getName() + "§c foi de arrasta!";

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                for (Player p : player.getWorld().getPlayers()) {
                    p.sendMessage(deathMessage);
                }
            }
            player.teleport(iSpawn);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§4Você morreu!","§4Assista a partida, ou saia com /spawn");
            playerWinner.playerWinner(player.getWorld(), false);
        }
    }
}
