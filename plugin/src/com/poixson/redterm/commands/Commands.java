package com.poixson.redterm.commands;

import java.util.concurrent.atomic.AtomicReference;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommandsHandler;


public class Commands extends pxnCommandsHandler<RedTermPlugin> {

	protected final RedTermPlugin plugin;

	protected final AtomicReference<Commands_RedTerm> cmds_redterm = new AtomicReference<Commands_RedTerm>(null);
	protected final AtomicReference<Commands_Scripts> cmds_scripts = new AtomicReference<Commands_Scripts>(null);
	protected final AtomicReference<Commands_Load>    cmds_load    = new AtomicReference<Commands_Load>   (null);
	protected final AtomicReference<Commands_Off>     cmds_off     = new AtomicReference<Commands_Off>    (null);
	protected final AtomicReference<Commands_Reboot>  cmds_reboot  = new AtomicReference<Commands_Reboot> (null);



	public Commands(final RedTermPlugin plugin) {
		super();
		this.plugin = plugin;
	}



	public void register() {
		// /redterm
		{
			final Commands_RedTerm cmds = new Commands_RedTerm(this.plugin);
			final Commands_RedTerm previous = this.cmds_redterm.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register(this.plugin);
		}
		// /scripts
		{
			final Commands_Scripts cmds = new Commands_Scripts(this.plugin);
			final Commands_Scripts previous = this.cmds_scripts.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register(this.plugin);
		}
		// /load
		{
			final Commands_Load cmds = new Commands_Load(this.plugin);
			final Commands_Load previous = this.cmds_load.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register(this.plugin);
		}
		// /off
		{
			final Commands_Reboot cmds = new Commands_Reboot(this.plugin);
			final Commands_Reboot previous = this.cmds_reboot.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register(this.plugin);
		}
		// /reboot
		{
			final Commands_Reboot cmds = new Commands_Reboot(this.plugin);
			final Commands_Reboot previous = this.cmds_reboot.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register(this.plugin);
		}
	}

	@Override
	public void unregister() {
		// /redterm
		{
			final Commands_RedTerm cmds = this.cmds_redterm.getAndSet(null);
			if (cmds != null)
				cmds.unregister();
		}
		// /scripts
		{
			final Commands_Scripts cmds = this.cmds_scripts.getAndSet(null);
			if (cmds != null)
				cmds.unregister();
		}
		// /load
		{
			final Commands_Load cmds = this.cmds_load.getAndSet(null);
			if (cmds != null)
				cmds.unregister();
		}
		// /off
		{
			final Commands_Off cmds = this.cmds_off.getAndSet(null);
			if (cmds != null)
				cmds.unregister();
		}
		// /reboot
		{
			final Commands_Reboot cmds = this.cmds_reboot.getAndSet(null);
			if (cmds != null)
				cmds.unregister();
		}
	}



	// -------------------------------------------------------------------------------



	// redterm
	public class Commands_RedTerm extends pxnCommandsHandler<RedTermPlugin> {

		public Commands_RedTerm(final RedTermPlugin plugin) {
			super(
				"redterm"
			);
			this.addCommand(new Command_Give(plugin));
		}

	}



	// /scripts
	public class Commands_Scripts extends pxnCommandsHandler<RedTermPlugin> {

		public Commands_Scripts(final RedTermPlugin plugin) {
			super(
				"scripts"
			);
			this.addCommand(new Command_Scripts(plugin));
		}

	}



	// /load
	public class Commands_Load extends pxnCommandsHandler<RedTermPlugin> {

		public Commands_Load(final RedTermPlugin plugin) {
			super(
				"load"
			);
			this.addCommand(new Command_Load(plugin));
		}

	}



	// /off
	public class Commands_Off extends pxnCommandsHandler<RedTermPlugin> {

		public Commands_Off(final RedTermPlugin plugin) {
			super(
				"off"
			);
			this.addCommand(new Command_Off(plugin));
		}

	}



	// /reboot
	public class Commands_Reboot extends pxnCommandsHandler<RedTermPlugin> {

		public Commands_Reboot(final RedTermPlugin plugin) {
			super(
				"reboot"
			);
			this.addCommand(new Command_Reboot(plugin));
		}

	}



}
