package ivanhauu.tech.bridgewars.commands;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Battles implements CommandExecutor {

    private final BridgeWars plugin;

    private String sectionsBaseFolder;
    private String subFolder4v4;
    private String subFolder2v2;
    private String battleWorldName2v2;
    private String battleWorldName4v4;

    public Battles(BridgeWars plugin) {
        this.plugin = plugin;

        this.sectionsBaseFolder = plugin.getSectionsBaseFolder();
        this.subFolder4v4 = plugin.getSubfolder4v4();
        this.subFolder2v2 = plugin.getSubfolder2v2();
        this.battleWorldName2v2 = plugin.getBattleWorldName2v2();
        this.battleWorldName4v4 = plugin.getBattleWorldName4v4();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        List<String> battle2v2 = new ArrayList<>();
        List<String> battle4v4 = new ArrayList<>();

        for (World mundo : Bukkit.getWorlds()) {
            if (mundo.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder2v2 + battleWorldName2v2)) {
                boolean battleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is2v2BattleStarted");

                if (battleStarted) {
                    battle2v2.add("§2[" + mundo.getName().charAt(mundo.getName().length() - 1) + " | 2v2]");
                } else {
                    battle2v2.add("§7[" + mundo.getName().charAt(mundo.getName().length() - 1) + " | 2v2]");
                }

            } else if (mundo.getName().startsWith(plugin.getDataFolder() + sectionsBaseFolder + subFolder4v4 + battleWorldName4v4)) {
                boolean battleStarted = plugin.getBattleConfig().getBoolean("worlds." + mundo.getName() + ".is4v4BattleStarted");

                if (battleStarted) {
                    battle4v4.add("§2[" + mundo.getName().charAt(mundo.getName().length() - 1) + " | 4v4]");
                } else {
                    battle4v4.add("§7[" + mundo.getName().charAt(mundo.getName().length() - 1) + " | 4v4]");
                }
            }
        }

        int maxBattles = Math.max(battle2v2.size(), battle4v4.size());
        sender.sendMessage("§6Batalhas em andamento:");
        sender.sendMessage("§2Verde: Em andamento");
        sender.sendMessage("§7Cinza: Sem andamento");
        for (int i = 0; i < maxBattles; i++) {
            sender.sendMessage(battle2v2.get(i) + " " + battle4v4.get(i));
        }

        return true;
    }
}
