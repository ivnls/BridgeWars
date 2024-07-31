package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.utils.JoinSession;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Fight implements CommandExecutor {
    private final JoinSession joinSession;

    public Fight(JoinSession joinSession) {
        this.joinSession = joinSession;
    }

    //Este é o comando principal do plugin, ele pode te levar a uma partida ou criar uma nova

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§6[BW-INFO] §cEste comando só pode ser executado por um player!");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage("§6[BW-INFO] §bUse '/fight list' para ver os modos de jogo disponíveis!");
            return false;
        }

        Player playerSender = (Player) sender;
        World spawn = Bukkit.getWorld("world");

        if (spawn == null || playerSender.getWorld() != spawn) {
            playerSender.sendMessage("§6[BW-INFO] §cVocê só pode executar este comando no spawn!");
            return false;
        }

        switch (args[0]) {
            case "2v2":
            case "4v4":
                joinSession.sessionType(args[0], playerSender);
                return true;
            case "list":
                sender.sendMessage("Modos de jogo disponíveis:");
                sender.sendMessage("2v2 --> 2 jogadores lutam entre si!");
                sender.sendMessage("4v4 --> 4 jogadores lutam entre si!");
                return true;
            default:
                sender.sendMessage("§6[BW-INFO] §cModo de jogo "+ args[0] +" não encontrado, use '/fight list' para ver os modos de jogo!");
                return false;
        }

    }
}
