package ivanhauu.tech.bridgewars.commands;

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

    // Simples comando para voltar ao spawn quando o player estiver em uma partida.

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um player!");
            return false;
        }

        Player playerSender = (Player) sender;
        World spawnWorld = Bukkit.getWorld("world");

        if (spawnWorld == null) {
            playerSender.sendMessage("O mundo 'world' não foi encontrado!");
            return false;
        }

        Location spawnLocation = new Location(spawnWorld, 8.0, 0.0, 8.0);
        playerSender.teleport(spawnLocation);
        playerSender.getInventory().clear();
        playerSender.setHealth(20);
        playerSender.setFoodLevel(20);
        playerSender.setGameMode(GameMode.SURVIVAL);
        playerSender.sendMessage("Você voltou ao spawn!");

        return true;
    }
}
