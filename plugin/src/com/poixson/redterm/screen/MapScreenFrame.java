package com.poixson.redterm.screen;

import static com.poixson.pluginlib.pxnPluginLib.GetFreedMapStore;
import static com.poixson.redterm.components.Component_Screen.DEBUG;
import static com.poixson.redterm.screen.MapScreen.MAP_SIZE;
import static com.poixson.utils.BukkitUtils.SetMapID;

import java.awt.Color;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.poixson.tools.FreedMapStore;
import com.poixson.utils.BukkitUtils;


// map in frame
public class MapScreenFrame extends MapRenderer implements Closeable {

	protected final ItemStack map;
	protected final MapView   view;
	protected final ItemFrame frame;

	protected final int map_id;
	protected final Location location;

	protected final PixelSource source;
	protected final int x, y;

	protected final AtomicBoolean stopped = new AtomicBoolean(false);



	public MapScreenFrame(final Location location, final BlockFace facing,
			final PixelSource source, final int x, final int y, final boolean perplayer) {
		super(perplayer);
		this.location = location;
		this.source   = source;
		this.x = x;
		this.y = y;
		this.map = new ItemStack(Material.FILLED_MAP, 1);
		final FreedMapStore store = GetFreedMapStore();
		this.map_id = store.get();
		SetMapID(this.map, this.map_id);
		this.frame = (GlowItemFrame) location.getWorld().spawnEntity(location, EntityType.GLOW_ITEM_FRAME);
		this.frame.setRotation(Rotation.NONE);
		this.frame.setFacingDirection(facing);
		this.frame.setFixed(true);
		this.frame.setPersistent(true);
		this.frame.setInvulnerable(true);
		if (!DEBUG)
			this.frame.setVisible(false);
		this.frame.setItem(this.map);
		this.view = BukkitUtils.GetMapView(this.map_id);
		if (this.view == null) throw new RuntimeException(String.format("Failed to get map view: %d", this.map_id));
		this.view.setTrackingPosition(false);
		this.view.setCenterX(0);
		this.view.setCenterZ(0);
		for (final MapRenderer render : this.view.getRenderers())
			this.view.removeRenderer(render);
		this.view.addRenderer(this);
		this.view.setLocked(true);
	}



	@Override
	public void render(final MapView map, final MapCanvas canvas, final Player player) {
		if (this.stopped.get()) return;
		final int offset_x = MAP_SIZE * this.x;
		final int offset_y = MAP_SIZE * this.y;
		final Color[][] pixels = this.source.getPixels(player);
		if (pixels != null) {
//TODO: mask
//			final BufferedImage img_mask = this.img_screen_mask.get();
//			final Iabcd size = this.findMaskArea();
//			final int color_white = Color.WHITE.asRGB();
			for (int iy=0; iy<MAP_SIZE; iy++) {
				for (int ix=0; ix<MAP_SIZE; ix++) {
//					if (img_mask == null || img_mask.getRGB(xx, yy) == color_white)
					final Color pixel = pixels[iy+offset_y][ix+offset_x];
					canvas.setPixelColor(ix, iy, pixel==null ? Color.BLACK : pixel);
				}
			}
		}
	}



	public void send(final Player player) {
		player.sendMap(this.view);
	}



	@Override
	public void close() {
		if (this.stopped.compareAndSet(false, true)) {
			// remove the item frame
			this.frame.setItem(null);
			this.frame.remove();
			// free the map id
			final FreedMapStore mapstore = GetFreedMapStore();
			mapstore.release(this.map_id);
		}
	}



	public Location getLocation() {
		return this.location;
	}



}
