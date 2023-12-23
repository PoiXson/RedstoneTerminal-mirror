package com.poixson.redterm.commands;

import org.bukkit.command.CommandSender;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommand;


public class Command_Reboot extends pxnCommand<RedTermPlugin> {



	public Command_Reboot(final RedTermPlugin plugin) {
		super(plugin, true);
	}



	@Override
	public boolean run(final CommandSender sender, final String label, final String[] args) {
//		final Player player = (sender instanceof Player ? (Player)sender : null);
//		final int num_args = args.length;
//TODO
return false;
	}



	@Override
	public boolean isDefault() {
		return true;
	}



}
