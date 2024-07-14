package ivanhauu.tech.bridgewars.utils;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;

public class GetPlayerRank {

    private final BridgeWars plugin;

    public GetPlayerRank(BridgeWars plugin) {
        this.plugin = plugin;
    }

    public String getPlayerRank(Player player) {
        int wins4v4 = plugin.getPlayerConfig().getInt("players." + player.getName() + ".4v4wins");
        int wins2v2 = 2 * plugin.getPlayerConfig().getInt("players." + player.getName() + ".2v2wins"); //As partidas 8v8 tem o dobro de pontuação!

        int ptsTotal = wins4v4 + wins2v2;

        String rankPrefix = null;

        if (ptsTotal >= 50) {
            return "§6[♅]";
        } else if (ptsTotal >= 40) {
            return "§9[☉]"; //Rank MESTRE (☉) 40 a 50
        } else if (ptsTotal >= 30) {
            return "§b[★]"; //Rank PROFISSIONAL (★) 30 a 40
        } else if (ptsTotal >= 20) {
            return "§e[♖]"; //Rank ENGAJADO (♖) 20 a 30
        } else if (ptsTotal >= 10) {
            return "§4[♯]"; //Rank LUTADOR (♯) 10 a 20
        } else if (ptsTotal >= 5) {
            return "§2[♤]"; //rank INICIANTE (♤) 5 a 10
        } else {
            return "§7[D]"; //Rank Default (Provavelmente nenhuma insígnia)
        }
    }
}