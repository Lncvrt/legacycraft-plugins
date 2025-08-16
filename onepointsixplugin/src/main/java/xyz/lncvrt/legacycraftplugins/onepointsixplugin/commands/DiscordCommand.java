package xyz.lncvrt.legacycraftplugins.onepointsixplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.lncvrt.legacycraftplugins.utils.Messages;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(Messages.discord);
        return true;
    }
}
