package com.poixson.redterm.commands;

import java.io.Closeable;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.commands.pxnCommandRoot;


public class Commands implements Closeable {

	// /redterm
	protected final pxnCommandRoot cmd_redterm;



	public Commands(final RedTermPlugin plugin) {
		this.cmd_redterm = new Command_RedTerm(plugin);
	}



	@Override
	public void close() {
		this.cmd_redterm.close();
	}



}
