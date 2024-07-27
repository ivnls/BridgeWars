package ivanhauu.tech.bridgewars.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ManageSchematics {

    public static Clipboard loadSchematic(File file) {
        Clipboard clipboard = null;

        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = ClipboardFormats.findByFile(file).getReader(fis)) {
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clipboard;
    }

    public static void pasteSchematic(Clipboard clipboard, Location location, boolean rotate180) {
        World world = BukkitAdapter.adapt(location.getWorld());
        EditSession editSession = WorldEdit.getInstance().newEditSession(world);

        ClipboardHolder holder = new ClipboardHolder(clipboard);

        if (rotate180) {
            AffineTransform transform = new AffineTransform().rotateY(180);
            holder.setTransform(holder.getTransform().combine(transform));
        }

        try {

            Operation operation = holder
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();

            Operations.complete(operation);
            editSession.close();

        } catch (WorldEditException e) {
            e.printStackTrace();
        }

    }
}
