package com.poixson.redterm;

import static com.poixson.utils.BukkitUtils.Log;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;


public class RedTermAPI {

	protected static final String NAME  = "RedTermPlugin";
	protected static final String CLASS = "com.poixson.redterm.RedTermPlugin";

	protected final RedTermPlugin plugin;

	protected static final AtomicInteger errcount_PluginNotFound = new AtomicInteger(0);



	public static RedTermAPI GetAPI() {
		// existing instance
		{
			final ServicesManager services = Bukkit.getServicesManager();
			final RedTermAPI api = services.load(RedTermAPI.class);
			if (api != null)
				return api;
		}
		// load api
		try {
			if (Class.forName(CLASS) == null)
				throw new ClassNotFoundException(CLASS);
			final PluginManager manager = Bukkit.getPluginManager();
			final Plugin plugin = manager.getPlugin(NAME);
			if (plugin == null) throw new RuntimeException(NAME+" plugin not found");
			return new RedTermAPI(plugin);
		} catch (ClassNotFoundException e) {
			if (errcount_PluginNotFound.getAndIncrement() < 10)
				Log().severe("Plugin not found: "+NAME);
			return null;
		}
	}

	protected RedTermAPI(final Plugin p) {
		if (p == null) throw new NullPointerException();
		this.plugin = (RedTermPlugin) p;
	}



}
