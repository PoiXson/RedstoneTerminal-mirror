package com.poixson.redterm.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommand;


// /redterm reboot
public class Command_Reboot extends pxnCommand {



	public Command_Reboot(final RedTermPlugin plugin) {
		super(
			"reboot",
			"restart"
		);
	}



	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
System.out.println("COMMAND:");
for (final String arg : args)
System.out.println("  "+arg);
		return false;
	}



	@Override
	public List<String> onTabComplete(final CommandSender sender, final String[] args) {
//TODO
System.out.println("TAB:"); for (final String arg : args) System.out.println("  "+arg);
return null;
	}



}
