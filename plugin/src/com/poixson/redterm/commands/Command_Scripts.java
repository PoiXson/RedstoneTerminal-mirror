package com.poixson.redterm.commands;

import org.bukkit.command.CommandSender;

import com.poixson.pluginlib.tools.commands.pxnCommand;
import com.poixson.redterm.RedTermPlugin;


public class Command_Scripts extends pxnCommand<RedTermPlugin> {



	public Command_Scripts(final RedTermPlugin plugin) {
		super(plugin, true);
	}



	@Override
	public boolean run(final CommandSender sender, final String label, final String[] args) {
//TODO
return false;
	}



	@Override
	public boolean isDefault() {
		return true;
	}



}
