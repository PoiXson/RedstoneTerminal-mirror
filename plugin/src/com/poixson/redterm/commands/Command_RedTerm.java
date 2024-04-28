package com.poixson.redterm.commands;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommandRoot;


public class Command_RedTerm extends pxnCommandRoot {

	protected final Command_Give     cmd_give;     // /redterm give
	protected final Command_Load     cmd_load;     // /redterm load
	protected final Command_Scripts  cmd_scripts;  // /redterm scripts
	protected final Command_Shutdown cmd_shutdown; // /redterm shutdown
	protected final Command_Reboot   cmd_reboot;   // /redterm reboot



	public Command_RedTerm(final RedTermPlugin plugin) {
		super(
			plugin,
			"Redstone Terminal commands", // desc
			null, // usage
			null, // perm
			"redterm", "redstoneterminal", "redstone-terminal",
			"computer"
		);
		this.addCommand(this.cmd_give     = new Command_Give(    plugin));
		this.addCommand(this.cmd_load     = new Command_Load(    plugin));
		this.addCommand(this.cmd_scripts  = new Command_Scripts( plugin));
		this.addCommand(this.cmd_shutdown = new Command_Shutdown(plugin));
		this.addCommand(this.cmd_reboot   = new Command_Reboot(  plugin));
	}



}