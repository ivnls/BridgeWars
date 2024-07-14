package ivanhauu.tech.bridgewars.listeners;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.utils.GetPlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class playerChat implements Listener {

    private final BridgeWars plugin;
    private final GetPlayerRank getPlayerRank;

    public playerChat(BridgeWars plugin, GetPlayerRank getPlayerRank) {
        this.plugin = plugin;
        this.getPlayerRank = getPlayerRank;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        World mundo = player.getWorld();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() != mundo) {
                event.getRecipients().remove(p);
            }
        }

        String formattedMessage = String.format("%s %s: %s", getPlayerRank.getPlayerRank(player), player.getName(), message);

        event.setFormat(formattedMessage);
    }

}
