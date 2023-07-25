package com.poixson.redterm.components;

import static com.poixson.commonmc.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.GraphicsUtils.LoadImage;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.poixson.commonmc.pxnCommonPlugin;
import com.poixson.commonmc.tools.mapstore.FreedMapStore;
import com.poixson.commonmc.tools.scripting.LocalOut;
import com.poixson.commonmc.tools.scripting.engine.CraftScriptManager;
import com.poixson.commonmc.tools.scripting.loader.ScriptLoader;
import com.poixson.commonmc.tools.scripting.loader.ScriptLoader_File;
import com.poixson.commonmc.tools.scripting.screen.MapScreen;
import com.poixson.commonmc.tools.scripting.screen.PixelSource;
import com.poixson.redterm.RedTermPlugin;
import com.poixson.tools.dao.Iabcd;
import com.poixson.utils.FileUtils;


public class Component_Screen extends Component implements PixelSource {
//	public static final int DEFAULT_FPS = 1;

	protected final MapScreen screen;
	protected final CraftScriptManager script;

	protected final PrintStream out;

	protected final int map_size = 128;



	public Component_Screen(final RedTermPlugin plugin,
			final Location loc, final BlockFace facing,
			final String filename) {
		super(plugin, loc, facing);
		final Location loc_screen = this.location.clone()
				.add(this.direction.getDirection());
		// load script
		{
			final String path_plugin = plugin.getDataFolder().getPath();
			final String path_local  = path_plugin + "/scripts";
			final String path_res    = "scripts";
			final ScriptLoader loader = new ScriptLoader_File(plugin, path_local, path_res, filename);
			try {
				loader.getSources();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			this.out = new PrintStream(new LocalOut(loc));
			this.script = (new CraftScriptManager())
				.setLoader(loader)
				.setSafeScope(false)
				.setThreaded(true)
				.setVariable("out", this.out)
				.setVariable("plugin", plugin);
			try {
				this.script.getSources();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		// map screen
		{
			// get map id
			final FreedMapStore mapstore = pxnCommonPlugin.GetFreedMapStore();
			if (mapstore == null) throw new RuntimeException("FreedMapStore is not available");
			final int map_id = mapstore.get();
			// map screen
			this.screen =
				(new MapScreen(
					plugin,
					loc_screen,
					facing,
					map_id,
					this.map_size
				))
				.setTickListener(new Runnable() {
					@Override
					public void run() {
						Component_Screen.this.script.tick();
					}
				})
				.setPixelSource(new PixelSource() {
					@Override
					public Color[][] getPixels(final Player player) {
						return Component_Screen.this.getPixels(player);
					}
				});
			this.loadDefaultImages();
//TODO
//			this.screen.setFPS( this.script.getFPS() );
			this.screen.setFPS(20);
			this.screen.start();
		}
		// start script
		{
			final Iabcd screen_size = this.screen.getScreenSize();
			this.script.setVariable("screen_width",  Integer.valueOf(screen_size.c));
			this.script.setVariable("screen_height", Integer.valueOf(screen_size.d));
			final int[][] pixels = new int[screen_size.d][screen_size.c];
			final int color_black = Color.BLACK.getRGB();
			for (int iy=0; iy<screen_size.d; iy++) {
				for (int ix=0; ix<screen_size.c; ix++)
					pixels[iy][ix] = color_black;
			}
			this.script.setVariable("pixels", pixels);
			final Map<String, String> cursors = new HashMap<String, String>();
			this.script.setVariable("cursors", cursors);
			this.script.start();
		}
		plugin.register(this);
	}



	@Override
	public void close() {
		this.plugin.unregister(this);
		this.script.stop();
		this.screen.stop();
		// free the map id
		{
			final FreedMapStore mapstore = pxnCommonPlugin.GetFreedMapStore();
			if (mapstore == null) throw new RuntimeException("FreedMapStore is not available");
			mapstore.release(this.screen.getMapID());
		}
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



	public void loadDefaultImages() {
		this.screen.setScreenMask( LoadImage(FileUtils.OpenResource(this.plugin.getClass(), "img/monitor/computer_monitor_screen_mask_128.png")) );
	}



	@Override
	public void click(final Player player, final int x, final int y) {
		this.script.addAction("click", player, Integer.valueOf(x), Integer.valueOf(y));
	}



	@Override
	public boolean isLocation(final Location loc) {
		if (super.isLocation(loc))
			return true;
		// screen
		if (EqualsLocation(loc, this.screen.getLocation()))
			return true;
		return false;
	}

	public Location getScreenLocation() {
		if (this.screen != null)
			return this.screen.getLocation();
		return null;
	}



}
