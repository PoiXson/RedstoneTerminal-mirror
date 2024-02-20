package com.poixson.redterm.components;

import static com.poixson.redterm.RedTermPlugin.LOG_PREFIX;
import static com.poixson.tools.screen.PixelSource_Script.DEFAULT_RADIUS;
import static com.poixson.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.CraftScriptUtils.FixClickPosition;
import static com.poixson.utils.CraftScriptUtils.GetLocationScriptFile;
import static com.poixson.utils.CraftScriptUtils.PlayerToHashMap;
import static com.poixson.utils.Utils.SafeClose;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.poixson.redterm.RedTermPlugin;
import com.poixson.scripting.xScriptThreadSafe;
import com.poixson.scripting.loader.xScriptLoader_File;
import com.poixson.tools.LocalPlayerOut;
import com.poixson.tools.dao.Iab;
import com.poixson.tools.dao.Iabcd;
import com.poixson.tools.screen.PixelSource_Script;
import com.poixson.tools.screen.ScreenComposite;
import com.poixson.utils.NumberUtils;


public class Component_Screen extends Component {
	public static final String DEFAULT_SCRIPT = "boot.js";

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
			final String path_plugin = plugin.getDataFolder().getPath();
			final String path_local  = path_plugin + "/scripts";
			final String path_res    = "scripts";
			final String filename    = "locations/" + GetLocationScriptFile(location);
//TODO: move this to setBootScript() ?
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
		{
//TODO: set this when changing the screen mask
//TODO: and create a new pixels array
			final Iabcd screen_size = this.screen.getScreenSize();
			this.script
				.setVariable("screen_margin_x", Integer.valueOf(screen_size.a))
				.setVariable("screen_margin_y", Integer.valueOf(screen_size.b))
				.setVariable("screen_width",    Integer.valueOf(screen_size.c))
				.setVariable("screen_height",   Integer.valueOf(screen_size.d));
			this.script.start();
			this.screen.start();
			plugin.register(this);
		}
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



	@Override
	public boolean isLocation(final Location loc) {
		if (loc == null) return false;
		if (EqualsLocation(loc, this.location)) return true;
		if (this.screen.isLocation(loc))        return true;
		return false;
	}



	public Logger log() {
		return this.plugin.getLogger();
	}



}
