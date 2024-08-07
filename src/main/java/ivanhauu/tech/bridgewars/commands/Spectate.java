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

public class Spectate implements CommandExecutor {

    private final BridgeWars plugin;

    private String sectionsBaseFolder;
    private String subFolder2v2;
    private String subFolder4v4;
    private String serverPrefix;

    public Spectate(BridgeWars plugin) {
        this.plugin = plugin;
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.serverPrefix = plugin.getServerPrefix();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um player!");
            return false;
        }

        Player player = (Player) sender;

        if (player.getWorld().getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder)) {
            player.sendMessage(serverPrefix + "§cVocê não pode espectar um mundo enquanto estiver fora do spawn!");
        }

        if (args.length != 2) {
            sender.sendMessage("Uso do comando: '/spectate <número da partida> <modo da batalha, ex: 4v4>'");
            sender.sendMessage("Use '/battles' para ver as partidas em andamento!");
            return false;
        }

        final int BattleNum;
        final String BattleMode;

        try {
            BattleNum = Integer.parseInt(args[0]);
            BattleMode = args[1];
        } catch (NumberFormatException e) {
            sender.sendMessage("§cO argumento deve ser o número da partida!");
            return false;
        }
        if (BattleMode.equals(subFolder2v2) || BattleMode.equals(subFolder4v4)) {
            String battleWorldName = plugin.getDataFolder() + sectionsBaseFolder + "/" + BattleMode + "/battle_" + BattleMode + "_" + BattleNum;
            boolean battleStarted = plugin.getBattleConfig().getBoolean("worlds." + battleWorldName + ".is" + BattleMode + "BattleStarted");

            if (battleStarted) {
                World battleWorld = Bukkit.getWorld(battleWorldName);
                Location teleportLoc = new Location(battleWorld, -32, 50, 32);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(teleportLoc);
                return true;
            } else {
                player.sendMessage("§cA partida §6" + BattleNum + "§c não está em andamento!");
            }
        }

        return false;
    }
}
