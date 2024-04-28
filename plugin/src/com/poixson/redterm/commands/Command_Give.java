package com.poixson.redterm.commands;

import static com.poixson.redterm.RedTermPlugin.CHAT_PREFIX;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommand;


public class Command_Give extends pxnCommand {



	public Command_Give(final RedTermPlugin plugin) {
		super(
			"give"
		);
	}



	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		final Player player = (sender instanceof Player ? (Player)sender : null);
		if (player == null) {
			sender.sendMessage("Cannot give to console");
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(CHAT_PREFIX + "Give what?");
			return true;
		}
		boolean has_perm = false;
		PERMISSION_SWITCH:
		switch (args[1]) {
		// altair
		case "altair":
		case "cpu":
		case "processor": if (player.hasPermission("redterm.give.altair")) has_perm = true; break PERMISSION_SWITCH;
		// crt monitor
		case "crt":
		case "mon":
		case "monitor": if (player.hasPermission("redterm.give.monitor")) has_perm = true; break PERMISSION_SWITCH;
		// keyboard
		case "key":
		case "keyboard": if (player.hasPermission("redterm.give.keyboard")) has_perm = true; break PERMISSION_SWITCH;
		// keypad
		case "pad":
		case "keypad": if (player.hasPermission("redterm.give.keypad")) has_perm = true; break PERMISSION_SWITCH;
		// outlet
		case "outlet":
		case "plug":
		case "socket": if (player.hasPermission("redterm.give.outlet")) has_perm = true; break PERMISSION_SWITCH;
		// arcade - pong
		case "arcade":
		case "pong": if (player.hasPermission("redterm.give.arcade")) has_perm = true; break PERMISSION_SWITCH;
		default:
			player.sendMessage(CHAT_PREFIX + "Unknown computer component: " + args[1]);
			return true;
		}
		if (!has_perm) {
			player.sendMessage(CHAT_PREFIX + "You don't have permission to use this.");
			return true;
		}
		GIVE_SWITCH:
		switch (args[1]) {
		// altair
		case "altair":
		case "cpu":
		case "processor": GiveToPlayer(player, 880); break GIVE_SWITCH;
		// crt monitor
		case "crt":
		case "mon":
		case "monitor": GiveToPlayer(player, 1897); break GIVE_SWITCH;
		// keyboard
		case "key":
		case "keyboard": GiveToPlayer(player, 104); break GIVE_SWITCH;
		// keypad
		case "pad":
		case "keypad": GiveToPlayer(player, 1988); break GIVE_SWITCH;
		// outlet
		case "outlet":
		case "plug":
		case "socket": GiveToPlayer(player, 11); break GIVE_SWITCH;
		// arcade - pong
		case "arcade":
		case "pong": GiveToPlayer(player, 1972); break GIVE_SWITCH;
		default:
			player.sendMessage(CHAT_PREFIX + "Unknown computer component: " + args[1]);
			return true;
		}
		return false;
	}



	public static void GiveToPlayer(final Player player, final int item_id) {
		final ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS);
		final ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(Integer.valueOf(item_id));
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
	}



	@Override
	public List<String> onTabComplete(final CommandSender sender, final String[] args) {
//TODO
System.out.println("TAB:"); for (final String arg : args) System.out.println("  "+arg);
return null;
	}



}
