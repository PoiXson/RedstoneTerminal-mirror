package com.poixson.redterm.components;

import static com.poixson.commonmc.tools.plugin.xJavaPlugin.LOG;
import static com.poixson.commonmc.tools.plugin.xJavaPlugin.LOG_PREFIX;
import static com.poixson.commonmc.utils.BukkitUtils.EqualsLocation;
import static com.poixson.utils.GraphicsUtils.LoadImage;

import java.awt.Color;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.poixson.commonmc.pxnCommonPlugin;
import com.poixson.commonmc.tools.mapstore.FreedMapStore;
import com.poixson.commonmc.tools.scripts.CraftScript;
import com.poixson.commonmc.tools.scripts.LocalOut;
import com.poixson.commonmc.tools.scripts.loader.ScriptLoader;
import com.poixson.commonmc.tools.scripts.loader.ScriptLoader_File;
import com.poixson.commonmc.tools.scripts.screen.MapScreen;
import com.poixson.commonmc.tools.scripts.screen.PixelSource;
import com.poixson.redterm.RedTermPlugin;
import com.poixson.redterm.components.actions.MonitorAction;
import com.poixson.redterm.components.actions.MonitorAction_Click;
import com.poixson.tools.dao.Iab;
import com.poixson.tools.dao.Iabcd;
import com.poixson.utils.FileUtils;


public class Component_Monitor extends Component implements PixelSource {

	protected final int map_id;
	protected final int map_size;

	protected final MapScreen screen;
	protected final CraftScript script;

	protected final PrintStream out;
	protected final LinkedList<MonitorAction> actions = new LinkedList<MonitorAction>();

	protected final AtomicBoolean stopping = new AtomicBoolean(false);



	public Component_Monitor(final RedTermPlugin plugin,
			final Location loc, final BlockFace facing,
			final String filename) {
		super(plugin, loc, facing);
		final Location loc_screen = this.location.clone()
				.add(this.direction.getDirection());
		final int fps;
		// screen
		{
			// get map id
			final FreedMapStore mapstore = pxnCommonPlugin.GetFreedMapStore();
			if (mapstore == null) throw new RuntimeException("FreedMapStore is not available");
			this.map_id = mapstore.get();
			this.map_size = 128;
			this.screen =
				new MapScreen(
					plugin,
					this.map_id,
					loc_screen,
					facing,
					this.map_size,
					this
				);
			this.loadDefaultImages();
			this.screen.start(fps);
		}
		// script
		{
			final String path_plugin = plugin.getDataFolder().getPath();
			final String path_local  = path_plugin + "/scripts";
			final String path_res    = "scripts";
			final ScriptLoader loader = new ScriptLoader_File(plugin, path_local, path_res, filename);
			this.script = new CraftScript(loader, false);
			this.script.setVariable("plugin", plugin);
			final Iabcd size = this.screen.findScreenSize();
			this.script.setVariable("screen_offset_x", Integer.valueOf(size.a));
			this.script.setVariable("screen_offset_y", Integer.valueOf(size.b));
			this.script.setVariable("screen_width",    Integer.valueOf(size.c));
			this.script.setVariable("screen_height",   Integer.valueOf(size.d));
			this.out = new PrintStream(new LocalOut(loc));
			this.script.setVariable("out", this.out);
			try {
				this.script.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		plugin.register(this);
	}



	@Override
	public void close() {
		super.close();
		this.script.close();
		this.plugin.unregister(this);
		this.stopping.set(true);
		this.screen.close();
		// free the map id
		{
			final FreedMapStore mapstore = pxnCommonPlugin.GetFreedMapStore();
			if (mapstore == null) throw new RuntimeException("FreedMapStore is not available");
			mapstore.release(this.map_id);
		}
	}



	@Override
	public Color[][] getPixels() {
		final Color[][] pixels = new Color[this.map_size][this.map_size];
		final HashMap<Player, Iab> cursors = new HashMap<Player, Iab>();
		for (final Player player : Bukkit.getOnlinePlayers()) {
			final RayTraceResult ray = player.rayTraceBlocks(MapScreen.DEFAULT_MAX_DISTANCE);
			if (ray != null) {
				if (EqualsLocation(ray.getHitBlock().getLocation(), this.location)) {
					final Vector vec = ray.getHitPosition();
					final int x = 127 - Math.abs( (int) (((vec.getX() + vec.getZ())%1.0) * 128.0) );
					final int y = 127 - Math.abs( (int) ( (vec.getY()              %1.0) * 128.0) );
					cursors.put(player, new Iab(x, y));
				}
			}
		}
		final MonitorAction[] actions = this.actions.toArray(new MonitorAction[0]);
		this.actions.clear();
		try {
			this.script.setVariable("pixels",  pixels );
			this.script.setVariable("cursors", cursors);
			this.script.setVariable("actions", actions);
			this.script.call("onRender");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pixels;
	}



	@Override
	public void click(final Player player, final int x, final int y) {
		if (this.stopping.get()) return;
		final MonitorAction action = new MonitorAction_Click(player, x+42, y+26);
		this.actions.addLast(action);
	}



	public void loadDefaultImages() {
		this.screen.img_screen_mask.set(LoadImage(FileUtils.OpenResource(this.plugin.getClass(), "img/monitor/computer_monitor_screen_mask_128.png")));
		if (this.screen.img_screen_mask.get() == null) LOG.warning(LOG_PREFIX + "Failed to load image: computer_monitor_screen_mask_128.png");
	}



	@Override
	public boolean isLocation(final Location loc) {
		if (super.isLocation(loc))
			return true;
		// screen
		if (EqualsLocation(loc, this.screen.loc))
			return true;
		return false;
	}

	public Location getScreenLocation() {
		if (this.screen != null)
			return this.screen.loc;
		return null;
	}



}
