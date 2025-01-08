package com.poixson.redterm.commands;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommandRoot;


// /redterm
public class Command_RedTerm extends pxnCommandRoot {

	protected final Command_Give     cmd_give;     // /redterm give
	protected final Command_Load     cmd_load;     // /redterm load
	protected final Command_Scripts  cmd_scripts;  // /redterm scripts
	protected final Command_Shutdown cmd_shutdown; // /redterm shutdown
	protected final Command_Reboot   cmd_reboot;   // /redterm reboot



	public Command_RedTerm(final RedTermPlugin plugin) {
		super(
			plugin,
			"redterm", // namespace
			"Redstone Terminal commands", // desc
			null, // usage
			null, // perm
			new String[] { // labels
				"redterm", "redstoneterminal", "redstone-terminal",
				"computer"
			}
		);
		this.addCommand(this.cmd_give     = new Command_Give(    plugin)); // /redterm give
		this.addCommand(this.cmd_load     = new Command_Load(    plugin)); // /redterm load
		this.addCommand(this.cmd_scripts  = new Command_Scripts( plugin)); // /redterm scripts
		this.addCommand(this.cmd_shutdown = new Command_Shutdown(plugin)); // /redterm shutdown
		this.addCommand(this.cmd_reboot   = new Command_Reboot(  plugin)); // /redterm reboot
	}



}
