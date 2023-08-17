package com.poixson.redterm.components;

import static com.poixson.commonmc.utils.ItemUtils.GetCustomModel;
import static com.poixson.commonmc.utils.LocationUtils.YawToFace;
import static com.poixson.redterm.RedTermPlugin.CHAT_PREFIX;
import static com.poixson.redterm.components.Component.ActivateComponent;
import static com.poixson.redterm.components.Component.GetEntityFilter;
import static com.poixson.redterm.components.Component.GetScreenFilter;
import static com.poixson.redterm.components.Component.PlaceComputerEntity;

import java.io.FileNotFoundException;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.poixson.commonmc.tools.plugin.xListener;
import com.poixson.redterm.RedTermPlugin;


public class ComponentListeners extends xListener<RedTermPlugin> {



	public ComponentListeners(final RedTermPlugin plugin) {
		super(plugin);
	}



	// place component
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();
		MATERIAL_SWITCH:
		switch (item.getType()) {
		case WHITE_STAINED_GLASS: {
			boolean has_perm = false;
			final Player player = event.getPlayer();
			final int model = GetCustomModel(item);
			PERMISSION_SWITCH:
			// check permissions
			switch (model) {
			// altair
			case 880:  if (player.hasPermission("redterm.place.altair"  )) has_perm = true; break PERMISSION_SWITCH;
			// crt monitor
			case 1897: if (player.hasPermission("redterm.place.monitor" )) has_perm = true; break PERMISSION_SWITCH;
			// keyboard
			case 104:  if (player.hasPermission("redterm.place.keyboard")) has_perm = true; break PERMISSION_SWITCH;
			// keypad
			case 1988: if (player.hasPermission("redterm.place.keypad"  )) has_perm = true; break PERMISSION_SWITCH;
			// outlet
			case 11:   if (player.hasPermission("redterm.place.outlet"  )) has_perm = true; break PERMISSION_SWITCH;
			// arcade - pong
			case 1972: if (player.hasPermission("redterm.place.arcade"  )) has_perm = true; break PERMISSION_SWITCH;
			default: return;
			}
			if (!has_perm) {
				event.setCancelled(true);
				player.sendMessage(CHAT_PREFIX + "You don't have permission to place a computer component");
				return;
			}
			if (!GameMode.CREATIVE.equals(player.getGameMode())) {
				event.setCancelled(true);
				player.sendMessage(CHAT_PREFIX + "You must be in creative mode to place this block.");
				return;
			}
			// create computer component
			final Location loc_player = player.getLocation();
			final Location loc_block  = event.getBlockPlaced().getLocation();
			final BlockFace facing = YawToFace(loc_player.getYaw());
			MODEL_SWITCH:
			switch (model) {
			// altair
			case 880: {
				PlaceComputerEntity(loc_block, item, facing, false, false);
//TODO
//				player.playSound(loc_block, Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
				break MODEL_SWITCH;
			}
			// crt monitor
			case 1897: {
				PlaceComputerEntity(loc_block, item, facing, true, false);
//TODO
//				final Component component = ActivateComponent(this.plugin, loc_block);
//				if (component != null)
//					player.playSound(loc_block, Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
				break MODEL_SWITCH;
			}
			// keyboard
			case 104: {
				PlaceComputerEntity(loc_block, item, facing, false, true);
				break MODEL_SWITCH;
			}
			// keypad
			case 1988: {
				PlaceComputerEntity(loc_block, item, facing, false, true);
				break MODEL_SWITCH;
			}
			// outlet
			case 11: {
				PlaceComputerEntity(loc_block, item, facing, false, true);
				break MODEL_SWITCH;
			}
			// arcade - pong
			case 1972: {
				PlaceComputerEntity(loc_block, item, facing, true, false);
				break MODEL_SWITCH;
			}
			default: break MODEL_SWITCH;
			}
		}
		default: break MATERIAL_SWITCH;
		}
	}

	// break component
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		TYPE_SWITCH:
		switch (block.getType()) {
		case BARRIER: {
			final Location loc = block.getLocation();
			final Component component = this.plugin.getComponent(loc);
			if (component != null) {
				final Player player = event.getPlayer();
				// crt monitor
				if (component instanceof Component_Screen) {
					if (!player.hasPermission("redterm.destroy.monitor")) {
						player.sendMessage(CHAT_PREFIX + "You don't have permission to break this.");
						event.setCancelled(true);
						return;
					}
					component.close();
					for (Entity entity : loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5, GetEntityFilter()))
						entity.remove();
				}
//TODO
//redterm.interact.altair
//redterm.interact.keyboard
//redterm.interact.keypad
//redterm.interact.outlet
			}
			break TYPE_SWITCH;
		}
		default: break TYPE_SWITCH;
		}
	}



	// turn off monitor
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
		final Entity entity = event.getEntity();
		ENTITY_SWITCH:
		switch (entity.getType()) {
		case ITEM_FRAME:
		case GLOW_ITEM_FRAME: {
			final Entity remover = event.getRemover();
			if (remover instanceof Player) {
				final Player player = (Player) remover;
				if (GetScreenFilter().test(entity)) {
					final Component component = this.plugin.getComponent(entity);
					if (component != null) {
						// crt monitor
						if (component instanceof Component_Screen) {
							if (!player.hasPermission("redterm.interact.monitor")) {
								player.sendMessage(CHAT_PREFIX + "You don't have permission to use this.");
								event.setCancelled(true);
								return;
							}
							component.close();
						}
//TODO
//redterm.interact.altair
//redterm.interact.keyboard
//redterm.interact.keypad
//redterm.interact.outlet
					}
				}
			}
			break ENTITY_SWITCH;
		}
		default: break ENTITY_SWITCH;
		}
	}



	// activate component
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockInteract(final PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		TYPE_SWITCH:
		switch (block.getType()) {
		case BARRIER: {
			final Location loc = block.getLocation();
			final Component existing = this.plugin.getComponent(loc);
			if (existing != null) break TYPE_SWITCH;
//TODO: permissions
//TODO: how to select the filename?
final String filename = "default.js";
			// activate component
			try {
				ActivateComponent(this.plugin, loc, filename);
			} catch (FileNotFoundException e) {
				event.setCancelled(true);
				e.printStackTrace();
			}
//TODO: finish or remove this
//			final Component component = ActivateComponent(this.plugin, loc, filename);
//			if (component != null)
//				player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
			break TYPE_SWITCH;
		} // end barrier
		default: break TYPE_SWITCH;
		}
	}



	// click monitor screen
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
		HAND_SWITCH:
		switch (event.getHand()) {
		case HAND:
		case OFF_HAND: {
			final Entity entity = event.getRightClicked();
			if (GetScreenFilter().test(entity)) {
				final Component component = this.plugin.getComponent(entity);
				if (component != null) {
//TODO: permissions
					component.click(event.getPlayer(), event.getClickedPosition());
				}
			}
			break HAND_SWITCH;
		}
		default: break HAND_SWITCH;
		}
	}



}
