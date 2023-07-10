package com.altoya.nationsrebuilt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandExample implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("commandNameInYML")) {
      if (sender.hasPermission("pluginname.permission")) {
        // if(args.length != 1 && args.length != 2) return true;
        //COMMAND CODE HERE
      
      }
    }
    return true;
  }
}
