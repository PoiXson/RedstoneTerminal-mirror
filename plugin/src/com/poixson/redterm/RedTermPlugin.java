package com.poixson.redterm;

import static com.poixson.utils.LocationUtils.Distance3D;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.poixson.redterm.commands.Commands;
import com.poixson.redterm.devices.Device;
import com.poixson.redterm.devices.DeviceListeners;
import com.poixson.tools.xJavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


public class RedTermPlugin extends xJavaPlugin {
	@Override public int getBStatsID() { return 19096; }
	public static final Component CHAT_PREFIX = Component.text("[RedTerm] ").color(NamedTextColor.AQUA);

	protected final CopyOnWriteArraySet<Device> devices = new CopyOnWriteArraySet<Device>();

	protected final AtomicReference<Commands> commands = new AtomicReference<Commands>(null);

	protected final DeviceListeners listener_devices;



	public RedTermPlugin() {
		super(RedTermPlugin.class);
		this.listener_devices = new DeviceListeners(this);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		// redstone listener
		this.listener_devices.register();
		// commands
		{
			final Commands commands = new Commands(this);
			final Commands previous = this.commands.getAndSet(commands);
			if (previous != null)
				previous.close();
		}
		// save
		this.setConfigChanged();
		this.saveConfigs();
	}
	@Override
	public void onDisable() {
		super.onDisable();
		// commands
		{
			final Commands commands = this.commands.getAndSet(null);
			if (commands != null)
				commands.close();
		}
		// redstone listener
		this.listener_devices.unregister();
		final LinkedList<Device> removing = new LinkedList<Device>();
		for (final Device device : this.devices) {
			device.close();
			removing.add(device);
		}
		for (final Device device : removing)
			this.devices.remove(device);
	}



	// -------------------------------------------------------------------------------
	// devices



	public void register(final Device device) {
		if (device == null) throw new NullPointerException();
		this.devices.add(device);
		this.log().info("New device: "+device.getLocation().toString());
	}
	public boolean unregister(final Device device) {
		if (device == null) throw new NullPointerException();
		final boolean result = this.devices.remove(device);
		this.log().info("Removed device: "+device.getLocation().toString());
		return result;
	}



	public Device getDevice(final Location loc) {
		if (loc == null) throw new NullPointerException();
		for (final Device device : this.devices) {
			if (device.isLocation(loc))
				return device;
		}
		return null;
	}
	public Device getDevice(final Entity entity) {
		if (entity == null) throw new NullPointerException();
		final Location loc = entity.getLocation();
		for (final Device device : this.devices) {
			if (device.isLocation(loc))
				return device;
		}
		return null;
	}
	public Device getDevice(final Player player) {
		if (player == null) throw new NullPointerException();
		// looking at block
		{
			final RayTraceResult ray = player.rayTraceBlocks(10.0);
			final Block block = ray.getHitBlock();
			if (block != null) {
				final Device device = this.getDevice(block.getLocation());
				if (device != null)
					return device;
			}
		}
		// near player
		{
			final Device device = this.getDeviceNear(player.getLocation(), 10);
			if (device != null)
				return device;
		}
		return null;
	}
	public Device getDeviceNear(final Location loc, final int distance) {
		double nearest_dist = Double.MAX_VALUE;
		Device nearest = null;
		for (final Device device : this.devices) {
			final Location to = device.getLocation();
			if (to != null) {
				int dist = (int) Distance3D(loc, to);
				if (dist >= 0
				&&  dist <= distance
				&&  dist < nearest_dist) {
					nearest_dist = dist;
					nearest      = device;
				}
			}
		}
		return nearest;
	}



}
