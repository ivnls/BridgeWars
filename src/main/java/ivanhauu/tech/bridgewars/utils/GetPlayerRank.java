package ivanhauu.tech.bridgewars.utils;

import ivanhauu.tech.bridgewars.BridgeWars;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetPlayerRank {

    private final BridgeWars plugin;

    public GetPlayerRank(BridgeWars plugin) {
        this.plugin = plugin;
    }

    public String getPlayerRank(Player player) {
        int wins4v4 = plugin.getPlayerConfig().getInt("players." + player.getName() + ".4v4wins");
        int wins2v2 = 2 * plugin.getPlayerConfig().getInt("players." + player.getName() + ".2v2wins"); //As partidas 8v8 tem o dobro de pontuação!

        List<String> prefixList = new ArrayList<String>();

        prefixList = plugin.getRanksPrefix();

        int ptsTotal = wins4v4 + wins2v2;

        if (ptsTotal >= 50) {
            return prefixList.get(0);
        } else if (ptsTotal >= 40) {
            return prefixList.get(1); //Rank MESTRE (☉) 40 a 50
        } else if (ptsTotal >= 30) {
            return prefixList.get(2); //Rank PROFISSIONAL (★) 30 a 40
        } else if (ptsTotal >= 20) {
            return prefixList.get(3); //Rank LUTADOR (♯) 10 a 20
        } else if (ptsTotal >= 10) {
            return prefixList.get(4); //Rank ENGAJADO (♖) 20 a 30
        } else if (ptsTotal >= 5) {
            return prefixList.get(5); //rank INICIANTE (♤) 5 a 10
        } else {
            return prefixList.get(6); //Rank Default (Provavelmente nenhuma insígnia)
        }
    }
}