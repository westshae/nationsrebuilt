package com.altoya.nationsrebuilt.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TownCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("town")) return true;
    if (!sender.hasPermission("nationsrebuilt.town")) return true;
    if (sender instanceof Player) return true;

    switch (args[0].toLowerCase()) {
      case "create":
        this.townCreateSubCommand(sender, args);
        break;
      case "help":
        this.helpSubCommand(sender, args);
        break;
      default:
        return false;
    }

    return true;
  }

  private void helpSubCommand(CommandSender sender, String[] args) {
    if (!sender.hasPermission("nationsrebuilt.town.help")) return;

    sender.sendMessage("Help");
  }

  private void townCreateSubCommand(CommandSender sender, String[] args) {
    if (!sender.hasPermission("nationsrebuilt.town.create")) return;
    // File fileFaction = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), File.separator + factionName + ".yml");
    // FileConfiguration factionData = YamlConfiguration.loadConfiguration(fileFaction);
  }


}
