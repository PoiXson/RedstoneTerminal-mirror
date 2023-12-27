package com.poixson.redterm.components;

import static com.poixson.redterm.RedTermPlugin.CHAT_PREFIX;
import static com.poixson.redterm.components.Component.ActivateComponent;
import static com.poixson.redterm.components.Component.GetEntityFilter;
import static com.poixson.redterm.components.Component.GetScreenFilter;
import static com.poixson.redterm.components.Component.PlaceComputerEntity;
import static com.poixson.utils.BlockUtils.GetCustomModel;
import static com.poixson.utils.LocationUtils.YawToFace;

import java.io.FileNotFoundException;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.events.xListener;


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
			case 880:  PlaceComputerEntity(item, loc_block, facing, false, false); break MODEL_SWITCH;
			// crt monitor
			case 1897: PlaceComputerEntity(item, loc_block, facing, true,  false); break MODEL_SWITCH;
			// keyboard
			case 104:  PlaceComputerEntity(item, loc_block, facing, false, true ); break MODEL_SWITCH;
			// keypad
			case 1988: PlaceComputerEntity(item, loc_block, facing, false, true ); break MODEL_SWITCH;
			// outlet
			case 11:   PlaceComputerEntity(item, loc_block, facing, false, true ); break MODEL_SWITCH;
			// arcade - pong
			case 1972: PlaceComputerEntity(item, loc_block, facing, true,  false); break MODEL_SWITCH;
			default: break MODEL_SWITCH;
			}
		}
		default: break MATERIAL_SWITCH;
		}
	}



	// break component
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		TYPE_SWITCH:
		switch (block.getType()) {
		case BARRIER: {
			final Location loc = block.getLocation();
			final Component component = this.plugin.getComponent(loc);
			if (component == null) {
				//ENTITY_LOOP:
				for (final Entity entity : loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
					final EntityType type = entity.getType();
					ENTITY_TYPE_SWITCH:
					switch (type) {
					case ITEM_FRAME:
					case GLOW_ITEM_FRAME: {
						if (!player.hasPermission("redterm.destroy.monitor")) {
							player.sendMessage(CHAT_PREFIX + "You don't have permission to break this.");
							event.setCancelled(true);
							break ENTITY_TYPE_SWITCH;
						}
						((ItemFrame)entity).setItem(null);
						entity.remove();
						break ENTITY_TYPE_SWITCH;
					}
					default: break ENTITY_TYPE_SWITCH;
					}
				} // end ENTITY_LOOP
			} else {
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



	// activate component
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockInteract(final PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))         return;
		if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) return;
		{
			final Block block = event.getClickedBlock();
			TYPE_SWITCH:
			switch (block.getType()) {
			case BARRIER: {
				final Location loc = block.getLocation();
				final Component existing = this.plugin.getComponent(loc);
				if (existing == null) {
//TODO: permissions
					// activate component
					try {
						ActivateComponent(this.plugin, loc);
					} catch (FileNotFoundException e) {
						event.setCancelled(true);
						e.printStackTrace();
					}
				}
				break TYPE_SWITCH;
			} // end barrier
			default: break TYPE_SWITCH;
			}
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
