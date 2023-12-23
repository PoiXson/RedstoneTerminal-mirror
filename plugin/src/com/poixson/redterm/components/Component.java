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
	public final BlockFace direction;



	public Component(final RedTermPlugin plugin,
			final Location loc, final BlockFace dir) {
		this.plugin    = plugin;
		this.location  = loc;
		this.direction = dir;
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
			final RedTermPlugin plugin, final Location loc)
			throws FileNotFoundException {
		// existing component
		{
			final Component component = plugin.getComponent(loc);
			if (component != null)
				return null;
		}
		// activate component
		final World world = loc.getWorld();
		final Predicate<Entity> filter = GetEntityFilter();
		ENTITY_LOOP:
		for (final Entity entity : world.getNearbyEntities(loc, 0.5, 0.5, 0.5, filter)) {
			final ItemFrame frame = (ItemFrame) entity;
			final ItemStack item = frame.getItem();
			final ItemMeta meta = item.getItemMeta();
			if (meta.hasCustomModelData()) {
				final int model = meta.getCustomModelData();
				switch (model) {
				case 1897:   // monitor
				case 1972: { // arcade - pong
					final BlockFace facing = RotationToFace(frame.getRotation()).getOppositeFace();
					return new Component_Screen(plugin, loc, facing);
				}
				default: break ENTITY_LOOP;
				}
			}
		}
		return null;
	}



	public static Predicate<Entity> GetEntityFilter() {
		return new Predicate<Entity>() {
			@Override
			public boolean test(final Entity entity) {
				TYPE_SWITCH:
				switch (entity.getType()) {
				case ITEM_FRAME:
				case GLOW_ITEM_FRAME: {
					final ItemStack item = ((ItemFrame)entity).getItem();
					MATERIAL_SWITCH:
					switch (item.getType()) {
					case WHITE_STAINED_GLASS: {
						final ItemMeta meta = item.getItemMeta();
						if (meta.hasCustomModelData()) {
							final int model = meta.getCustomModelData();
							MODEL_SWITCH:
							switch (model) {
							case 1897: return true; // monitor
							case 880:  return true; // altair
							case 1972: return true; // arcade - pong
							default: break MODEL_SWITCH;
							}
							break MATERIAL_SWITCH;
						}
					}
					default: break MATERIAL_SWITCH;
					} // end MATERIAL_SWITCH
					break TYPE_SWITCH;
				}
				default: break TYPE_SWITCH;
				} // end TYPE_SWITCH
				return false;
			}
		};
	}
	public static Predicate<Entity> GetScreenFilter() {
		return new Predicate<Entity>() {
			@Override
			public boolean test(final Entity entity) {
				TYPE_SWITCH:
				switch (entity.getType()) {
				case ITEM_FRAME:
				case GLOW_ITEM_FRAME: {
					final ItemStack item = ((ItemFrame)entity).getItem();
					MATERIAL_SWITCH:
					switch (item.getType()) {
					case FILLED_MAP: {
						return true;
//TODO: use this?
//						final ItemMeta meta = item.getItemMeta();
//						if (meta.hasCustomModelData()) {
//							final int model = meta.getCustomModelData();
//System.out.println("G " + model);
//							MODEL_SWITCH:
//							switch (model) {
//TODO: more types?
//							case 1897: return true; // monitor
//							case 880:  return true; // altair
//							default: break MODEL_SWITCH;
//							}
//							break MATERIAL_SWITCH;
//						}
					}
					default: break MATERIAL_SWITCH;
					} // end MATERIAL_SWITCH
					break TYPE_SWITCH;
				}
				default: break TYPE_SWITCH;
				} // end TYPE_SWITCH
				return false;
			}
		};
	}



}
