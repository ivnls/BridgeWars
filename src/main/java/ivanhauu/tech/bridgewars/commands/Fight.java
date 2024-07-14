package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.BridgeWars;
import ivanhauu.tech.bridgewars.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Fight implements CommandExecutor {
    private final WorldManager worldManager;
    private final BridgeWars plugin;

    public Fight(WorldManager worldManager, BridgeWars plugin) {
        this.worldManager = worldManager;
        this.plugin = plugin;
    }

    //Este é o comando principal do plugin, ele pode te levar a uma partida ou criar uma nova

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um player!");
            return false;
        }

        if (!(args.length == 1)) {
            sender.sendMessage("Use '/fight list' para ver os modos de jogo disponíveis!");
            return false;
        }

        if (args[0].equals("list")) {
            sender.sendMessage("Modos de jogo disponíveis:");
            sender.sendMessage("2v2 --> 2 jogadores lutam entre si!");
            sender.sendMessage("4v4 --> 4 jogadores lutam entre si!");
            return true;
        }

        Player playerSender = (Player) sender;
        World spawn = Bukkit.getWorld("world");

        if (spawn == null || playerSender.getWorld() != spawn) {
            playerSender.sendMessage("Você só pode executar este comando no spawn!");
            return false;
        }

        if (args[0].equals("2v2")) {
            for (World mundo : Bukkit.getWorlds()) {
                boolean is2v2BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is2v2BattleStarted");
                if (mundo.getName().startsWith("battle_2v2_") && !is2v2BattleStarted) {
                    sender.sendMessage("Entrando em mundo já criado...");
                    Location playerSpawn = new Location(mundo, -32, 6, 32);
                    playerSender.teleport(playerSpawn);
                    return true;
                }
            }

            sender.sendMessage("Nenhum jogo encontrado, criando nova seção 2v2...");
            String templateWorldName = "ilhas";
            String newWorldName = "battle_2v2_" + System.currentTimeMillis();
            sender.sendMessage("Criando mundo para entrar...");

            if (worldManager.cloneWorld(templateWorldName, newWorldName)) {
                worldManager.loadWorld(newWorldName);
                World fightWorld = Bukkit.getWorld(newWorldName);
                if (fightWorld == null) {
                    sender.sendMessage("Erro ao carregar o novo mundo.");
                    return false;
                }

                Location playerSpawn = new Location(fightWorld, -32, 6, 32);
                playerSender.teleport(playerSpawn);
                return true;
            }

            sender.sendMessage("Erro ao clonar o mundo 2v2.");
            return false;

        } else if (args[0].equals("4v4")) {
            for (World mundo : Bukkit.getWorlds()) {
                boolean is4v4BattleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is4v4BattleStarted", false);
                if (mundo.getName().startsWith("battle_4v4_") && !is4v4BattleStarted) {
                    sender.sendMessage("Entrando em mundo já criado...");
                    Location playerSpawn = new Location(mundo, -32, 6, 32);
                    playerSender.teleport(playerSpawn);
                    return true;
                }
            }

            sender.sendMessage("Nenhum jogo encontrado, criando nova seção 4v4...");
            String templateWorldName = "ilhas";
            String newWorldName = "battle_4v4_" + System.currentTimeMillis();
            sender.sendMessage("Criando mundo para entrar...");

            if (worldManager.cloneWorld(templateWorldName, newWorldName)) {
                worldManager.loadWorld(newWorldName);
                World fightWorld = Bukkit.getWorld(newWorldName);
                if (fightWorld == null) {
                    sender.sendMessage("Erro ao carregar o novo mundo 4v4.");
                    return false;
                }

                Location playerSpawn = new Location(fightWorld, -32, 6, 32);
                playerSender.teleport(playerSpawn);
                plugin.is4v4BattleStart(newWorldName, false);
                plugin.isBattleStarting(newWorldName, false);
                return true;
            }

            sender.sendMessage("Erro ao clonar o mundo.");
            return false;

        } else {
            sender.sendMessage("Modo de jogo não encontrado, use '/fight list' para ver os modos de jogo!");
            return false;
        }

    }

}
