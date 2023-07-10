package com.altoya.nationsrebuilt.commands;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TownCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("town")) return true;
    if (!sender.hasPermission("nationsrebuilt.town")) return true;
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;


    switch (args[0].toLowerCase()) {
      case "create":
        this.townCreateSubCommand(player, args);
        break;
      case "help":
        this.helpSubCommand(player, args);
        break;
      default:
        return false;
    }

    return true;
  }

  private void helpSubCommand(Player player, String[] args) {
    if (!player.hasPermission("nationsrebuilt.town.help")) return;

    player.sendMessage("Help");
  }

  private void townCreateSubCommand(Player player, String[] args) {
    if(args.length != 2) return;
    if (!player.hasPermission("nationsrebuilt.town.create")) return;

    //Load players.yml data file.
    File playersFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration playersData = YamlConfiguration.loadConfiguration(playersFile);

    UUID playerUUID = player.getUniqueId();

    Boolean hasTown =  playersData.getBoolean("players." + playerUUID.toString() + ".town.has");
    if(hasTown) return;

    //Town name must be 6-14 characters, no whitespaces.
    String townName = args[1].toString();
    Pattern pattern = Pattern.compile("(^\\w{5,15}\\S$)");
    if(!pattern.matcher(townName).matches())return;

    //Load towns.yml data file.
    File townsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("nationsrebuilt").getDataFolder(), "towns.yml");
    FileConfiguration townsData = YamlConfiguration.loadConfiguration(townsFile);

    //Set player/town data files with new values.
    if(townsData.contains("factions." + townName))return;
    townsData.set("factions." + townName + ".name", townName);
    townsData.set("factions." + townName + ".members", playerUUID.toString());
    playersData.set("players." + playerUUID.toString() + ".town.has", true);
    playersData.set("players."+playerUUID.toString()+".town.name", townName);

    try {
      townsData.save(townsFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      playersData.save(playersFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    player.sendMessage("Your town, \"" + townName + "\" has been created.");
  }


}
