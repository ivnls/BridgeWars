package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    private final BridgeWars plugin;

    private String sectionsBaseFolder;
    private String subFolder2v2;
    private String subFolder4v4;
    private String battleWorldName2v2;
    private String battleWorldName4v4;

    public PlayerMove(BridgeWars plugin) {
        this.plugin = plugin;

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.battleWorldName2v2 = plugin.getBattleWorldName2v2();
        this.battleWorldName4v4 = plugin.getBattleWorldName4v4();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String eventWorldName = player.getWorld().getName();

        if (eventWorldName.startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2) || eventWorldName.startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {
            boolean isBattleStarting = plugin.getBattleConfig().getBoolean("worlds." + eventWorldName + ".isBattleStarting");
            if (isBattleStarting) {
                player.sendMessage(plugin.getServerPrefix() + "§cA partida está iniciando, você não pode se mover!");
                event.setCancelled(true);
            }
        }
    }
}
