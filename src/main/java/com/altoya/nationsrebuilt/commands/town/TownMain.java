package com.altoya.nationsrebuilt.commands.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TownMain implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("town")) return true;
    if (!sender.hasPermission("nationsrebuilt.town")) return true;
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;


    switch (args[0].toLowerCase()) {
      case "create":
        TownCreate.townCreateSubCommand(player, args);
        break;
      case "invite":
        TownInvite.townInviteSubCommand(player, args);
        break;
      case "help":
        TownHelp.helpSubCommand(player, args);
        break;
      case "kick":
        TownKick.townKickSubCommand(player,args);
        break;
      case "votelist":
        TownVotelist.townVoteListSubCommand(player);
        break;
      case "vote":
        TownVote.townVoteSubCommand(player, args);
        break;
      case "join":
        TownJoin.townJoinSubCommand(player, args);
        break;
      default:
        return false;
    }

    return true;
  }
}
