package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Ptop implements CommandExecutor {

    private final BridgeWars plugin;

    public Ptop(BridgeWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length != 2) {
            sender.sendMessage("Uso do comando: /ptop <modo de jogo> <quantidade de players>");
            return false;
        }

        String mode = args[0];
        int nickShow;

        try {
            nickShow = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Número inválido fornecido.");
            return false;
        }

        if (nickShow <= 0) {
            sender.sendMessage("Número de jogadores deve ser maior que zero.");
            return false;
        }

        if (!(mode.equalsIgnoreCase("2v2") || mode.equalsIgnoreCase("4v4"))) {
            sender.sendMessage("Modo de jogo inválido. Use '2v2' ou '4v4'.");
            return false;
        }

        Set<String> players = plugin.getPlayerConfig().getConfigurationSection("players").getKeys(false);
        nickShow = Math.min(nickShow, players.size());

        List<String> topPlayers = new ArrayList<>();

        for (String player : players) {
            int wins = 0;

            if (mode.equalsIgnoreCase("2v2")) {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".2v2wins");
            } else if (mode.equalsIgnoreCase("4v4")) {
                wins = plugin.getPlayerConfig().getInt("players." + player + ".4v4wins");
            }

            topPlayers.add(player + ":" + wins);
        }

        topPlayers.sort((player1, player2) -> {
            int wins1 = Integer.parseInt(player1.split(":")[1]);
            int wins2 = Integer.parseInt(player2.split(":")[1]);
            return Integer.compare(wins2, wins1);
        });

        sender.sendMessage("Top " + nickShow + " jogadores no modo " + mode + ":");
        for (int i = 0; i < nickShow && i < topPlayers.size(); i++) {
            String[] playerData = topPlayers.get(i).split(":");
            String playerName = playerData[0];
            int wins = Integer.parseInt(playerData[1]);
            sender.sendMessage(playerName + "\n" + "Vitórias: " + wins + "\n");
        }

        return true;
    }
}
