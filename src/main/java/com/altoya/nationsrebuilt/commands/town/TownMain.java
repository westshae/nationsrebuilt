package com.altoya.nationsrebuilt.commands.town;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.altoya.nationsrebuilt.util.PlayerMessage;

public class TownMain implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("town")) return true;
    
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;
    if (!sender.hasPermission("nationsrebuilt.town")){
      PlayerMessage.error(player, "You do not have permisson to run this command");
      return true;
    }


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
      case "leave":
        TownLeave.townLeaveSubCommand(player, args);
        break;
      default:
        return false;
    }

    return true;
  }
}
