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
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.scripting.xScriptThreadSafe;
import com.poixson.scripting.loader.xScriptLoader_File;
import com.poixson.tools.LocalPlayerOut;
import com.poixson.tools.dao.Iab;
import com.poixson.tools.dao.Iabcd;
import com.poixson.tools.screen.PixelSource_Script;
import com.poixson.tools.screen.ScreenComposite;


public class Component_Screen extends Component {
	public static boolean DEBUG = true;

	public static final int    DEFAULT_FPS    = 1;
	public static final String DEFAULT_SCRIPT = "boot.js";
	public static final int    DEFAULT_RADIUS = 10;

	protected final PixelSource_Script source;
	protected final ScreenComposite screen;
	protected final xScriptThreadSafe script;

	protected final int screens_width, screens_height;



	public Component_Screen(final RedTermPlugin plugin,
			final Location location, final BlockFace facing,
			final int screens_width, final int screens_height)
			throws FileNotFoundException {
		super(plugin, location, facing);
		this.screens_width  = screens_width;
		this.screens_height = screens_height;
		boolean import_players;
		boolean export_pixels;
		boolean perplayer;
		// load script
		final xScriptLoader_File loader;
		{
			final String path_plugin    = plugin.getDataFolder().getPath();
			final String path_local     = path_plugin + "/scripts";
			final String path_res       = "scripts";
			final String path_locations = "locations";
//TODO: move this to getLocationScriptFile() ?
			final String filename =
				String.format(
					"%s/%s_%dx_%dy_%dz.js",
					path_locations,
					location.getWorld().getName(),
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()
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
			// flags
			import_players = loader.hasImport("players");
			export_pixels  = loader.hasExport("pixels");
			perplayer      = loader.hasFlag("perplayer");
			// load script engine
			this.script = new xScriptThreadSafe(loader, false);
			this.script
				.setVariable("plugin", plugin)
				.setVariable("out", new LocalPlayerOut(location, DEFAULT_RADIUS))
				.setVariable("file_self", loader.getScriptFile());
			// pixels
			if (export_pixels) {
				final int width  = 128 * this.screens_width;
				final int height = 128 * this.screens_height;
				final Color[][] pixels = new Color[height][];
				for (int iy=0; iy<height; iy++)
					pixels[iy] = new Color[width];
				this.script.setVariable("pixels", pixels);
			}
		}
		// map screen
		{
//TODO: script flags
final boolean perplayer = false;
			// map screen
			final Location loc_screen = this.location.clone()
					.add(this.facing.getDirection());
			this.source = new PixelSource_Script(this.script, this.location, this.facing);
			if (import_players) this.source.import_players.set(true);
			if (export_pixels ) this.source.export_pixels .set(true);
			this.screen =
				new ScreenComposite(
					plugin, this.source,
					loc_screen, facing,
					this.screens_width, this.screens_height,
					perplayer
				);
			// fps
			final Integer fps = NumberUtils.ToInteger(loader.getFlag("fps"));
			if (fps != null)
				this.screen.setFPS(fps.intValue());
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
		this.script.call("pixels");
	}



	@Override
	public void close() {
		this.plugin.unregister(this);
		this.screen.stop();
		this.script.stop();
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



	// current frame buffer
	@Override
	public Color[][] getPixels(final Player player) {
		final Object obj = this.script.getVariable("pixels");
		final PixelsHolder pixels = (PixelsHolder) obj;
		return (pixels == null ? null : pixels.getFrameArray());
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
