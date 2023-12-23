package com.poixson.redterm.screen;

import java.awt.Color;

import org.bukkit.entity.Player;


public interface PixelSource {


	public Color[][] getPixels(final Player player);

	public int getScreensWidth();
	public int getScreensHeight();


}
