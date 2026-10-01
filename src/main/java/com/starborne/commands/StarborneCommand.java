package com.starborne.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StarborneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        sender.sendMessage("§5§l★ STARBORNE SMP ★");
        sender.sendMessage("§dThe plugin is working!");
        return true;
    }
}
