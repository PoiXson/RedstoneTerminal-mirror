package com.poixson.redterm.commands;

import org.bukkit.command.CommandSender;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommand;


public class Command_Off extends pxnCommand<RedTermPlugin> {



	public Command_Off(final RedTermPlugin plugin) {
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
