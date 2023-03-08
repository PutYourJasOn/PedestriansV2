package me.json.pedestrians.commands;

import me.json.pedestrians.objects.framework.pedestrian.PedestrianGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class DebugCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))) {
            sender.sendMessage("[{Help}]");
            sender.sendMessage("/pedestrians debug uuid <name>");
            sender.sendMessage("    gets the uuid of an online playername");
            sender.sendMessage("/pedestrians debug groupInfo");
            sender.sendMessage("    gets group info");
            sender.sendMessage("[{----}]");

            return true;
        }

        //0 arg commands
        if(args.length < 2) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("groupInfo")) {

            sender.sendMessage("[{Groups}]");
            for (PedestrianGroup pedestrianGroup : PedestrianGroup.Registry.pedestrianGroups()) {

                sender.sendMessage("Group: "+pedestrianGroup.id());
                sender.sendMessage("    "+pedestrianGroup.pathNetwork().name());
                sender.sendMessage("    "+pedestrianGroup.pedestrians().size());
            }
            sender.sendMessage("[{------}]");

            return true;
        }

        //1 arg commands
        if(args.length < 3) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("uuid")) {
            Player player = (Player) sender;
            UUID uuid = Bukkit.getPlayer(args[2]).getUniqueId();

            Inventory inv = Bukkit.createInventory(player, InventoryType.ANVIL);
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(uuid.toString());
            item.setItemMeta(meta);
            inv.setItem(0, item);
            player.openInventory(inv);

            return true;
        }

        sender.sendMessage("[{Error}] Wrong command or arguments.");
        return true;
    }

}
