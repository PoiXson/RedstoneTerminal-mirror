package com.poixson.redterm.components;

import com.poixson.scripting.PixelsHolder;


public class RunNextFrame implements Runnable {

	protected final PixelsHolder pixels;



	public RunNextFrame(final PixelsHolder pixels) {
		this.pixels = pixels;
	}



	@Override
	public void run() {
		this.pixels.goNextFrame();
	}



}
