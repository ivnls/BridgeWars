package ivanhauu.tech.bridgewars.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadSchematic {

    public static Clipboard loadSchematic(File file, World world) {
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        Clipboard clipboard;

        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = ClipboardFormats.findByFile(file).getReader(fis)) {
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clipboard;
    }
}
