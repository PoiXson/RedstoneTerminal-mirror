package com.poixson.redterm;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.poixson.redterm.commands.Commands;
import com.poixson.redterm.components.Component;
import com.poixson.redterm.components.ComponentListeners;
import com.poixson.tools.xJavaPlugin;


public class RedTermPlugin extends xJavaPlugin {
	@Override public int getSpigotPluginID() { return 111236; }
	@Override public int getBStatsID() {       return 19096;  }

	public static final String LOG_PREFIX  = "[RedTerm] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + LOG_PREFIX + ChatColor.WHITE;

	protected final CopyOnWriteArraySet<Component> components = new CopyOnWriteArraySet<Component>();
	protected final ComponentListeners listener_component;

	protected final Commands commands;



	public RedTermPlugin() {
		super(RedTermPlugin.class);
		this.listener_component = new ComponentListeners(this);
		this.commands = new Commands(this);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		this.listener_component.register();
		this.commands.register();
		this.saveConfigs();
	}
	@Override
	public void onDisable() {
		super.onDisable();
		this.listener_component.unregister();
		this.commands.unregister();
		final LinkedList<Component> removing = new LinkedList<Component>();
		for (final Component component : this.components) {
			component.close();
			removing.add(component);
		}
		for (final Component component : removing)
			this.components.remove(component);
	}



	// -------------------------------------------------------------------------------
	// components



	public void register(final Component component) {
		if (component == null) throw new NullPointerException();
		this.components.add(component);
		this.log().info("New component: "+component.getLocation().toString());
	}
	public boolean unregister(final Component component) {
		if (component == null) throw new NullPointerException();
		final boolean result = this.components.remove(component);
		this.log().info("Removed component: "+component.getLocation().toString());
		return result;
	}



	public Component getComponent(final Location loc) {
		if (loc == null) throw new NullPointerException();
		for (final Component component : this.components) {
			if (component.isLocation(loc))
				return component;
		}
		return null;
	}
	public Component getComponent(final Entity entity) {
		if (entity == null) throw new NullPointerException();
		final Location loc = entity.getLocation();
		for (final Component component : this.components) {
			if (component.isLocation(loc))
				return component;
		}
		return null;
	}
	public Component getComponent(final Player player) {
		if (player == null) throw new NullPointerException();
		// looking at block
		{
			final RayTraceResult ray = player.rayTraceBlocks(10.0);
			final Block block = ray.getHitBlock();
			if (block != null) {
				final Component component = this.getComponent(block.getLocation());
				if (component != null)
					return component;
			}
		}
		// near player
		{
			final Component component = this.getComponentNear(player.getLocation(), 10);
			if (component != null)
				return component;
		}
		return null;
	}
//TODO: check world?
	public Component getComponentNear(final Location loc, final int distance) {
		Location to;
		double dist;
		double nearest_dist = Double.MAX_VALUE;
		Component nearest = null;
		for (final Component component : this.components) {
			to = component.getLocation();
			if (to != null) {
				dist = to.distance(loc);
				if (dist <= distance
				&&  dist < nearest_dist) {
					nearest_dist = dist;
					nearest      = component;
				}
			}
		}
		return nearest;
	}



}
