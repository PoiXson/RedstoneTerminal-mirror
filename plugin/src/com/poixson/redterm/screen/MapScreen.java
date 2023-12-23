package com.poixson.redterm.screen;

import static com.poixson.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.LocationUtils.FaceToIxyz;
import static com.poixson.utils.NumberUtils.MinMax;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.poixson.tools.dao.Iabc;
import com.poixson.tools.dao.Iabcd;


public class MapScreen extends BukkitRunnable implements Closeable {
	public static final int MAP_SIZE = 128;

	protected final JavaPlugin plugin;

	protected final AtomicLong fps = new AtomicLong(1L);
	protected final PixelSource source;
	protected final MapScreenFrame[] screens;

	protected final Location  location;
	protected final BlockFace facing;

	protected final AtomicReference<Iabcd> screen_size = new AtomicReference<Iabcd>(null);

	protected final AtomicBoolean stopping = new AtomicBoolean(false);



	public MapScreen(final JavaPlugin plugin,
			final Location location, final BlockFace facing,
			final boolean perplayer, final PixelSource source) {
		this.plugin    = plugin;
		this.location  = location;
		this.facing    = facing;
		this.source    = source;
		// maps in frames
		{
			final Iabc xyz = FaceToIxyz(facing);
			final LinkedList<MapScreenFrame> screens = new LinkedList<MapScreenFrame>();
			final int width  = source.getScreensWidth();
			final int height = source.getScreensHeight();
			final int from_x = 0 - (int)Math.floor( ((double)width)/2.0 );
			for (int iy=0; iy<height; iy++) {
				for (int ix=from_x; ix<width; ix++) {
					final Location loc = location.clone()
						.add(xyz.a*ix, iy, xyz.c*ix);
					final MapScreenFrame screen = new MapScreenFrame(loc, facing, source, ix, iy, perplayer);
					screens.addLast(screen);
				}
			}
			this.screens = screens.toArray(new MapScreenFrame[0]);
		}
		// map render task
		final long ticks = MinMax( ((long)this.fps.get())/20L, 1L, 20L);
		this.runTaskTimer(this.plugin, ticks, ticks);
	}



	@Override
	public void run() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			for (MapScreenFrame screen : MapScreen.this.screens)
				screen.send(player);
		}
	}



	@Override
	public void close() {
		if (this.stopping.compareAndSet(false, true)) {
			this.cancel();
			for (final MapScreenFrame screen : this.screens)
				screen.close();
		}
	}



	public Location[] getScreenLocations() {
		final LinkedList<Location> locations = new LinkedList<Location>();
		for (final MapScreenFrame screen : this.screens)
			locations.add(screen.getLocation());
		return locations.toArray(new Location[0]);
	}

	public boolean isLocation(final Location loc) {
		if (loc == null) return false;
		for (final Location location : this.getScreenLocations()) {
			if (EqualsLocation(loc, location))
				return true;
		}
		return false;
	}



	public Iabcd getScreenSize() {
		// existing
		{
			final Iabcd size = this.screen_size.get();
			if (size != null)
				return size;
		}
		// find screen size
		{
			int min_x = Integer.MAX_VALUE;
			int min_y = Integer.MAX_VALUE;
			int max_x = Integer.MIN_VALUE;
			int max_y = Integer.MIN_VALUE;
//TODO
//			final BufferedImage mask = this.img_screen_mask.get();
//			// no screen mask
//			if (mask == null) {
				min_x = min_y = 0;
				max_x = max_y = MAP_SIZE - 1;
//			// find mask size in + shape
//			} else {
//				final int half = Math.floorDiv(MAP_SIZE, 2);
//				for (int ix=0; ix<half; ix++) {
//					if (Color.BLACK.equals(new Color(mask.getRGB(half-ix, half)))) {
//						min_x = (half - ix) + 1;
//						break;
//					}
//				}
//				for (int ix=0; ix<half; ix++) {
//					if (Color.BLACK.equals(new Color(mask.getRGB(half+ix, half)))) {
//						max_x = half + ix;
//						break;
//					}
//				}
//				for (int iy=0; iy<half; iy++) {
//					if (Color.BLACK.equals(new Color(mask.getRGB(half, half-iy)))) {
//						min_y = (half - iy) + 1;
//						break;
//					}
//				}
//				for (int iy=0; iy<half; iy++) {
//					if (Color.BLACK.equals(new Color(mask.getRGB(half, half+iy)))) {
//						max_y = half + iy;
//						break;
//					}
//				}
//			}
			final Iabcd size = new Iabcd(min_x, min_y, max_x-min_x, max_y-min_y);
			if (this.screen_size.compareAndSet(null, size))
				return size;
		}
		return this.getScreenSize();
	}



}
