package com.poixson.redterm.components;

import static com.poixson.redterm.RedTermPlugin.LOG_PREFIX;
import static com.poixson.redterm.screen.MapScreen.MAP_SIZE;
import static com.poixson.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.CraftScriptUtils.FixClickPosition;
import static com.poixson.utils.CraftScriptUtils.FixCursorPosition;
import static com.poixson.utils.CraftScriptUtils.PlayerToHashMap;
import static com.poixson.utils.Utils.SafeClose;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.redterm.screen.MapScreen;
import com.poixson.redterm.screen.PixelSource;
import com.poixson.scripting.xScriptThreadSafe;
import com.poixson.scripting.loader.xScriptLoader_File;
import com.poixson.tools.LocalPlayerOut;
import com.poixson.tools.dao.Iab;
import com.poixson.tools.dao.Iabcd;


public class Component_Screen extends Component implements Runnable, PixelSource {
	public static boolean DEBUG = true;

	public static final int    DEFAULT_FPS    = 1;
	public static final String DEFAULT_SCRIPT = "boot.js";
	public static final int    DEFAULT_RADIUS = 10;

	protected final MapScreen         screen;
	protected final xScriptThreadSafe script;

	protected final int screens_width, screens_height;



	public Component_Screen(final RedTermPlugin plugin,
			final Location loc, final BlockFace facing)
			final int screens_width, final int screens_height)
			throws FileNotFoundException {
		super(plugin, loc, facing);
		this.screens_width  = screens_width;
		this.screens_height = screens_height;
		// load script
		{
			final xScriptLoader_File loader;
			final String path_plugin    = plugin.getDataFolder().getPath();
			final String path_local     = path_plugin + "/scripts";
			final String path_res       = "scripts";
//TODO: move this to getLocationScriptFile()
			final String file_script =
					String.format(
						"%s/%s_%dx_%dy_%dz.js",
						path_locations,
						loc.getWorld().getName(),
						loc.getBlockX(),
						loc.getBlockY(),
						loc.getBlockZ()
					);
			final String path_locations = "locations";
				);
//TODO: move this to setBootScript()
			// create location specific script
			final File file = new File(path_local, filename);
			if (!file.isFile()) {
				final File dir = file.getParentFile();
				try {
					if (!dir.isDirectory()) {
						if (dir.mkdirs())
							this.log().info(String.format("%sCreated directory: %s", LOG_PREFIX, dir.toString()));
					}
					if (file.createNewFile()) {
						FileWriter handle = null;
						try {
							handle = new FileWriter(file);
							handle.write("//#include=");
							handle.write(DEFAULT_SCRIPT);
							handle.write('\n');
						} finally {
							SafeClose(handle);
						}
						this.log().info(String.format("%sCreated new file: %s", LOG_PREFIX, filename));
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			// load script files
			loader =
				new xScriptLoader_File(
					this.plugin.getClass(),
					path_local,
					path_res,
					filename
				);
			try {
				loader.getSources();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			this.script = new xScriptThreadSafe(loader, false);
			this.script
				.setVariable("plugin", plugin)
				.setVariable("out", new LocalPlayerOut(location, DEFAULT_RADIUS))
				.setVariable("file_self", loader.getScriptFile())
//TODO: script flags
				.setVariable("pixels", new Color[MAP_SIZE][MAP_SIZE]);
		}
		// map screen
		{
//TODO: script flags
final boolean perplayer = false;
			// map screen
			final Location loc_screen = this.location.clone()
					.add(this.facing.getDirection());
			this.screen = new MapScreen(plugin, loc_screen, facing, perplayer, this) {
				@Override
				public void run() {
					Component_Screen.this.run();
					super.run();
				}
			};
		}
		// start script
		final Iabcd screen_size = this.screen.getScreenSize();
		this.script
			.setVariable("screen_margin_x", Integer.valueOf(screen_size.a))
			.setVariable("screen_margin_y", Integer.valueOf(screen_size.b))
			.setVariable("screen_width",    Integer.valueOf(screen_size.c))
			.setVariable("screen_height",   Integer.valueOf(screen_size.d));
		this.script.start();
		plugin.register(this);
	}



	// call loop()
	@Override
	public void run() {
		final Iabcd screen_size = this.screen.getScreenSize();
		final Map<String, Object> players = new ConcurrentHashMap<String, Object>();
		//PLAYERS_LOOP:
		for (final Player player : Bukkit.getOnlinePlayers()) {
//			final Map<String, Object> player_info = PlayerToHashMap(player);
//TODO: import flag
			final RayTraceResult ray = player.rayTraceBlocks(DEFAULT_RADIUS);
			if (ray != null) {
				if (EqualsLocation(ray.getHitBlock().getLocation(), this.location)) {
					final Vector vec = ray.getHitPosition();
					final Iab pos = FixCursorPosition(vec, screen_size, this.facing);
					if (pos != null) {
						final Map<String, Object> player_info = PlayerToHashMap(player);
						player_info.put("cursor_x", Integer.valueOf(pos.a));
						player_info.put("cursor_y", Integer.valueOf(pos.b));
						players.put(player.getName(), player_info);
					}
				}
			}
		} // end PLAYERS_LOOP
		this.script.setVariable("players", players);
		this.script.call("loop");
	}



	@Override
	public void close() {
		this.plugin.unregister(this);
		this.script.stop();
		this.screen.close();
	}



	// call click(player, x, y)
	@Override
	public void click(final Player player, final Vector vec) {
		final Iabcd screen_size = this.screen.getScreenSize();
		final Iab pos = FixClickPosition(vec, screen_size, this.facing, player.getLocation());
		if (pos != null) {
			final Map<String, Object> player_info = PlayerToHashMap(player);
			this.script.call("click", player_info, Integer.valueOf(pos.a), Integer.valueOf(pos.b));
		}
	}



	public Location[] getScreenLocations() {
		return this.screen.getScreenLocations();
	}



	@Override
	public Color[][] getPixels(final Player player) {
		final Object obj = this.script.getVariable("pixels");
		if (obj == null) return null;
		@SuppressWarnings("unchecked")
		final LinkedTransferQueue<LinkedTransferQueue<Integer>> list =
				(LinkedTransferQueue<LinkedTransferQueue<Integer>>) obj;
		Color[][] pixels = null;
		int ix = 0;
		int iy = 0;
		for (final LinkedTransferQueue<Integer> lst : list) {
			if (pixels == null)
				pixels = new Color[list.size()][lst.size()];
			for (final Integer entry : lst) {
				pixels[iy][ix] = new Color(entry.intValue());
				ix++;
			}
			ix = 0;
			iy++;
		}
		return pixels;
	}



	@Override
	public boolean isLocation(final Location loc) {
		if (loc == null) return false;
		if (super.isLocation(loc))
			return true;
		return this.screen.isLocation(loc);
	}



	@Override
	public int getScreensWidth() {
		return this.screens_width;
	}
	@Override
	public int getScreensHeight() {
		return this.screens_height;
	}



	public Logger log() {
		return this.plugin.getLogger();
	}



}
