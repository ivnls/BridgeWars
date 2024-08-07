package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Spawn implements CommandExecutor {

    private final BridgeWars plugin;

    private String sectionsBaseFolder;

    public Spawn(BridgeWars plugin) {
        this.plugin = plugin;

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
    }

    // Simples comando para voltar ao spawn quando o player estiver em uma partida.

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por um player!");
            return false;
        }

        Player playerSender = (Player) sender;
        World spawnWorld = Bukkit.getWorld("world");

        if (spawnWorld == null) {
            playerSender.sendMessage("§cO mundo 'world' não foi encontrado!");
            return false;
        }

        String worldName = playerSender.getWorld().getName();

        if (worldName.startsWith(plugin.getDataFolder() + sectionsBaseFolder)) {
            boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + worldName + ".is2v2BattleStarted");
            boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + worldName + ".is4v4BattleStarted");

            if ((is2v2BattleStarted || is4v4BattleStarted) && playerSender.getGameMode() == GameMode.SURVIVAL) {
                playerSender.sendMessage(plugin.getServerPrefix() + "§cVocê não pode sair de uma partida em andamento!");
                return false;
            }
        }

        Location spawnLocation = new Location(spawnWorld, 8.0, 0.0, 8.0);
        playerSender.teleport(spawnLocation);
        playerSender.getInventory().clear();
        playerSender.setHealth(20);
        playerSender.setFoodLevel(20);
        playerSender.setGameMode(GameMode.SURVIVAL);

        return true;
    }
}
