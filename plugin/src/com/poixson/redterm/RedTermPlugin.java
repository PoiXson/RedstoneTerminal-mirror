package com.poixson.redterm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

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

	// commands
	protected final AtomicReference<Commands> commands = new AtomicReference<Commands>(null);

	// listeners
	protected final AtomicReference<ComponentListeners> listeners = new AtomicReference<ComponentListeners>(null);



	public RedTermPlugin() {
		super(RedTermPlugin.class);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		// component listeners
		{
			final ComponentListeners listener = new ComponentListeners(this);
			final ComponentListeners previous = this.listeners.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// commands
		{
			final Commands cmds = new Commands(this);
			final Commands previous = this.commands.getAndSet(cmds);
			if (previous != null)
				previous.unregister();
			cmds.register();
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// commands
		{
			final Commands commands = this.commands.getAndSet(null);
			if (commands != null)
				commands.unregister();
		}
		// component listeners
		{
			final ComponentListeners listener = this.listeners.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
		// components
		{
			final LinkedList<Component> removing = new LinkedList<Component>();
			final Iterator<Component> it = this.components.iterator();
			while (it.hasNext()) {
				final Component comp = it.next();
				comp.close();
				removing.add(comp);
			}
			for (final Component comp : removing)
				this.components.remove(comp);
		}
	}



	// -------------------------------------------------------------------------------



	public void register(final Component component) {
		if (component == null) throw new NullPointerException();
		this.components.add(component);
	}
	public boolean unregister(final Component component) {
		if (component == null) throw new NullPointerException();
		return this.components.remove(component);
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
//	public Component_Monitor getMonitorByScreen(final Location loc) {
//		for (final Component_Monitor monitor : this.monitors) {
//			if (EqualsLocation(monitor.getScreenLocation(), loc))
//				return monitor;
//		}
//		return null;
//	}



}
