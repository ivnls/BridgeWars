package ivanhauu.tech.bridgewars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateChest {

    private final JavaPlugin plugin;

    public GenerateChest(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //Basicamente este é o método que além de gerar o baú, também gera o inventario do mesmo, usando os itens que estão
    // na lista chestItems.
    public void generateChest(Location location) {
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BlockState state = block.getState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;

                ThreadLocalRandom rand = ThreadLocalRandom.current();

                //Lista de possíveis itens que podem vir nos baús
                List<ItemStack> chestItems = Arrays.asList(
                        new ItemStack(Material.STONE_AXE, 1),
                        new ItemStack(Material.SHIELD, 1),
                        new ItemStack(Material.LEATHER_CHESTPLATE, 1),
                        new ItemStack(Material.LEATHER_LEGGINGS, 1),
                        new ItemStack(Material.IRON_HELMET, 1),
                        new ItemStack(Material.IRON_BOOTS, 1),
                        new ItemStack(Material.CHAINMAIL_HELMET, 1),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
                        new ItemStack(Material.CHAINMAIL_BOOTS, 1),
                        new ItemStack(Material.CROSSBOW, 1),
                        new ItemStack(Material.ARROW, 5),
                        new ItemStack(Material.SNOWBALL, 1),
                        new ItemStack(Material.TRIDENT, 1),
                        new ItemStack(Material.STONE_SWORD, 1),
                        new ItemStack(Material.GOLDEN_AXE, 1),
                        new ItemStack(Material.GOLDEN_SWORD, 1),
                        new ItemStack(Material.GOLDEN_HELMET, 1),
                        new ItemStack(Material.GOLDEN_BOOTS, 1),
                        new ItemStack(Material.BREAD, rand.nextInt(1,8))
                );

                //Lógica de geração de inventário aleatório para baú
                int itemsPerChest = rand.nextInt(1, 8);

                for (int i = 0; i < itemsPerChest; i++) {
                    int slot = rand.nextInt(chest.getInventory().getSize());

                    if (chest.getInventory().getItem(slot) == null || chest.getInventory().getItem(slot).getType() == Material.AIR) {
                        int chestItemIndex = rand.nextInt(chestItems.size());
                        chest.getInventory().setItem(slot, chestItems.get(chestItemIndex));
                    } else {
                        i--;
                    }
                }

            }
        }, 20L);
    }
}