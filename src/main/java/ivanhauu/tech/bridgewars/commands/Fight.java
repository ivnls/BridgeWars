package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.BridgeWars;
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
    private final BridgeWars plugin;

    private boolean allow2v2;
    private boolean allow4v4;

    public Fight(JoinSession joinSession, BridgeWars plugin) {
        this.joinSession = joinSession;
        this.plugin = plugin;
        this.allow2v2 = plugin.isAllow2v2();
        this.allow4v4 = plugin.isAllow4v4();
    }

    //Este é o comando principal do plugin, ele pode te levar a uma partida ou criar uma nova

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getServerPrefix() + "§cEste comando só pode ser executado por um player!");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getServerPrefix() + "§bUse '/fight list' para ver os modos de jogo disponíveis!");
            return false;
        }

        if (args[0].equals("2v2") && !allow2v2) {
            sender.sendMessage("§cO modo de jogo 2v2 está desativado!");
            return false;
        } else if (args[0].equals("4v4") && !allow4v4) {
            sender.sendMessage("§cO modo de jogo 4v4 está desativado!");
            return false;
        }

        Player playerSender = (Player) sender;
        World spawn = Bukkit.getWorld("world");

        if (spawn == null || playerSender.getWorld() != spawn) {
            playerSender.sendMessage(plugin.getServerPrefix() + "§cVocê só pode executar este comando no spawn!");
            return false;
        }

        switch (args[0]) {
            case "2v2":
            case "4v4":
                joinSession.sessionType(args[0], playerSender);
                return true;
            case "list":
                sender.sendMessage("§2Modos de jogo disponíveis:");
                sender.sendMessage("§b2v2 §7--> §42 jogadores lutam entre si!");
                sender.sendMessage("§b4v4 §7--> §44 jogadores lutam entre si!");
                return true;
            default:
                sender.sendMessage(plugin.getServerPrefix() + "§cModo de jogo "+ args[0] +" não encontrado, use '/fight list' para ver os modos de jogo!");
                return false;
        }

    }
}
