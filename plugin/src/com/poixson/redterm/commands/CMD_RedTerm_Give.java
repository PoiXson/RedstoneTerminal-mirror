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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


// /redterm give
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
			player.sendMessage(CHAT_PREFIX.append(Component.text("Give what?").color(NamedTextColor.AQUA)));
			return true;
		}
		boolean has_perm = false;
		SWITCH_PERMISSION:
		switch (args[1]) {
		// altair
		case "altair":
		case "cpu":
		case "processor": if (player.hasPermission("redterm.cmd.give.altair")) has_perm = true; break SWITCH_PERMISSION;
		// crt monitor
		case "crt":
		case "mon":
		case "monitor": if (player.hasPermission("redterm.cmd.give.monitor")) has_perm = true; break SWITCH_PERMISSION;
		// keyboard
		case "key":
		case "keyboard": if (player.hasPermission("redterm.cmd.give.keyboard")) has_perm = true; break SWITCH_PERMISSION;
		// keypad
		case "pad":
		case "keypad": if (player.hasPermission("redterm.cmd.give.keypad")) has_perm = true; break SWITCH_PERMISSION;
		// outlet
		case "outlet":
		case "plug":
		case "socket": if (player.hasPermission("redterm.cmd.give.outlet")) has_perm = true; break SWITCH_PERMISSION;
		// arcade - pong
		case "arcade":
		case "pong": if (player.hasPermission("redterm.cmd.give.arcade")) has_perm = true; break SWITCH_PERMISSION;
		default:
			player.sendMessage(CHAT_PREFIX.append(Component.text(
				"Unknown computer device: "+args[1]).color(NamedTextColor.AQUA)));
			return true;
		}
		if (!has_perm) {
			player.sendMessage(CHAT_PREFIX.append(Component.text(
				"You don't have permission to use this.").color(NamedTextColor.AQUA)));
			return true;
		}
		SWITCH_GIVE:
		switch (args[1]) {
		// altair
		case "altair":
		case "cpu":
		case "processor": GiveToPlayer(player, 880); break SWITCH_GIVE;
		// crt monitor
		case "crt":
		case "mon":
		case "monitor": GiveToPlayer(player, 1897); break SWITCH_GIVE;
		// keyboard
		case "key":
		case "keyboard": GiveToPlayer(player, 104); break SWITCH_GIVE;
		// keypad
		case "pad":
		case "keypad": GiveToPlayer(player, 1988); break SWITCH_GIVE;
		// outlet
		case "outlet":
		case "plug":
		case "socket": GiveToPlayer(player, 11); break SWITCH_GIVE;
		// arcade - pong
		case "arcade":
		case "pong": GiveToPlayer(player, 1972); break SWITCH_GIVE;
		default:
			player.sendMessage(CHAT_PREFIX.append(Component.text(
				"Unknown computer device: "+args[1]).color(NamedTextColor.AQUA)));
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
