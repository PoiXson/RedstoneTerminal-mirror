package com.poixson.redterm.components.actions;

import org.bukkit.entity.Player;


public class MonitorAction_Click implements MonitorAction {

	public final Player player;
	public final int x, y;



	public MonitorAction_Click(final Player player, final int x, final int y) {
		this.player = player;
		this.x = x;
		this.y = y;
	}



}
