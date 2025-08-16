package xyz.lncvrt.legacycraftplugins.onepointsixplugin.commands;

import net.minecraft.server.v1_6_R3.ChatMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.lncvrt.legacycraftplugins.onepointsixplugin.LegacyCraftPlugin;

public class SpawnCommand implements CommandExecutor {
    private final LegacyCraftPlugin plugin;

    public SpawnCommand(LegacyCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            World world = plugin.getServer().getWorld("world");
            player.teleport(new Location(world, -78, 63, 262));
            player.sendMessage(ChatColor.GOLD + "Teleporting to spawn...");
        }
        return true;
    }
}
