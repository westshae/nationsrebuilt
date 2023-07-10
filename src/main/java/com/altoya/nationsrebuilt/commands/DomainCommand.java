package com.altoya.nationsrebuilt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DomainCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("help")) {
      if (sender.hasPermission("nationsrebuilt.help")) {
        sender.sendMessage("Help");
      }
    }
    return true;
  }
}
