package com.poixson.redterm.components;

import static com.poixson.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.LocationUtils.FaceToRotation;
import static com.poixson.utils.LocationUtils.RotationToFace;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.poixson.redterm.RedTermPlugin;


public abstract class Component implements Closeable {

	protected final RedTermPlugin plugin;

	public final Location location;
	public final BlockFace facing;



	public Component(final RedTermPlugin plugin,
			final Location location, final BlockFace facing) {
		this.plugin    = plugin;
		this.location  = location;
		this.facing    = facing;
	}



	@Override
	public abstract void close();



	public abstract void click(final Player player, final Vector vec);



	public Location getLocation() {
		return this.location;
	}
	public BlockFace getFacing() {
		return this.facing;
	}
	public boolean isLocation(final Location loc) {
		if (loc == null) return false;
		return EqualsLocation(loc, this.location);
	}



	public static void PlaceComputerEntity(final ItemStack item,
			final Location loc, final BlockFace facing,
			final boolean isSolid, final boolean isWall) {
		final Block block = loc.getBlock();
		if (isSolid) block.setType(Material.BARRIER);
		else         block.setType(Material.AIR);
		final ItemFrame frame = (ItemFrame) loc.getWorld().spawnEntity(loc, EntityType.ITEM_FRAME);
		frame.setItem(item);
		if (isWall) {
			frame.setRotation(Rotation.NONE);
			frame.setFacingDirection(facing.getOppositeFace(), true);
		} else {
			frame.setRotation(FaceToRotation(facing));
			frame.setFacingDirection(BlockFace.UP, true);
		}
		frame.setFixed(true);
		frame.setPersistent(true);
		frame.setInvulnerable(true);
		frame.setVisible(false);
	}



	public static Component ActivateComponent(
			final RedTermPlugin plugin, final Location location)
			throws FileNotFoundException {
		// existing component
		{
			final Component component = plugin.getComponent(location);
			if (component != null)
				return null;
		}
		// activate component
		{
			final World world = location.getWorld();
			final Predicate<Entity> filter = GetEntityFilter();
			LOOP_ENTITY:
			for (final Entity entity : world.getNearbyEntities(location, 0.5, 0.5, 0.5, filter)) {
				final ItemFrame frame = (ItemFrame) entity;
				final ItemStack item = frame.getItem();
				final ItemMeta meta = item.getItemMeta();
				if (meta.hasCustomModelData()) {
					final int model = meta.getCustomModelData();
					switch (model) {
					case 1897:   // monitor
					case 1972: { // arcade - pong
						final BlockFace facing = RotationToFace(frame.getRotation()).getOppositeFace();
						return new Component_Screen(plugin, location, facing, 1, 1);
					}
					default: break LOOP_ENTITY;
					}
				}
			}
		}
		return null;
	}
	public static Predicate<Entity> GetEntityFilter() {
		return new Predicate<Entity>() {
			@Override
			public boolean test(final Entity entity) {
				SWITCH_TYPE:
				switch (entity.getType()) {
				case ITEM_FRAME:
				case GLOW_ITEM_FRAME: {
					final ItemStack item = ((ItemFrame)entity).getItem();
					SWITCH_MATERIAL:
					switch (item.getType()) {
					case WHITE_STAINED_GLASS: {
						final ItemMeta meta = item.getItemMeta();
						if (meta.hasCustomModelData()) {
							final int model = meta.getCustomModelData();
							SWITCH_MODEL:
							switch (model) {
							case 1897: return true; // monitor
							case 880:  return true; // altair
							case 1972: return true; // arcade - pong
							default: break SWITCH_MODEL;
							}
							break SWITCH_MATERIAL;
						}
					}
					default: break SWITCH_MATERIAL;
					} // end SWITCH_MATERIAL
					break SWITCH_TYPE;
				}
				default: break SWITCH_TYPE;
				} // end SWITCH_TYPE
				return false;
			}
		};
	}
	public static Predicate<Entity> GetScreenFilter() {
		return new Predicate<Entity>() {
			@Override
			public boolean test(final Entity entity) {
				SWITCH_TYPE:
				switch (entity.getType()) {
				case ITEM_FRAME:
				case GLOW_ITEM_FRAME: {
					final ItemStack item = ((ItemFrame)entity).getItem();
					if (Material.FILLED_MAP.equals(item.getType()))
						return true;
					break SWITCH_TYPE;
				}
				default: break SWITCH_TYPE;
				} // end SWITCH_TYPE
				return false;
			}
		};
	}



}
