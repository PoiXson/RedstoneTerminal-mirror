
importClass(Packages.java.awt.Color);



var wrap = false;
var sick_chance = 18.0;

var extinct = 0;

var grid = [];
for (iy=0; iy<screen_height; iy++) {
	grid[iy] = [];
	for (ix=0; ix<screen_width; ix++)
		grid[iy][ix] = false;
}
seed();



function seed() {
	for (iy=0; iy<screen_height; iy++) {
		for (ix=0; ix<screen_width; ix++) {
			let rnd = Math.floor(Math.random() * 12.0);
			grid[iy][ix] = (rnd == 0);
		}
	}
}



function onRender() {
	for (iy=0; iy<screen_height; iy++) {
		for (ix=0; ix<screen_width; ix++) {
			pixels[iy][ix] = (grid[iy][ix] ? Color.WHITE : Color.BLACK);
		}
	}
	extinct++;
	for (iy=0; iy<screen_height; iy++) {
		for (ix=0; ix<screen_width; ix++) {
			let c = 0;
			if (isAlive(ix-1, iy-1)) c++;
			if (isAlive(ix,   iy-1)) c++;
			if (isAlive(ix+1, iy-1)) c++;
			if (isAlive(ix-1, iy  )) c++;
			if (isAlive(ix+1, iy  )) c++;
			if (isAlive(ix-1, iy+1)) c++;
			if (isAlive(ix,   iy+1)) c++;
			if (isAlive(ix+1, iy+1)) c++;
			if (isAlive(ix, iy)) {
				extinct = 0;
				if (c < 2 || c > 3) setAlive(ix, iy, false);
				else if (Math.floor(Math.random() * sick_chance) == 0)
					setAlive(ix, iy, false);
			} else {
				if (c == 3) setAlive(ix, iy, true);
			}
		}
	}
	if (extinct >= 20) {
		seed();
		extinct = 0;
	}
}



function isAlive(x, y) {
	if (wrap) {
		let xx = x; while (xx < 0) xx += screen_width;
		let yy = y; while (yy < 0) yy += screen_height;
		xx = xx % screen_width;
		yy = yy % screen_height;
		return grid[yy][xx];
	} else {
		if (x < 0 || x >= screen_width ) return false;
		if (y < 0 || y >= screen_height) return false;
		return grid[y][x];
	}
}

function setAlive(x, y, alive) {
	if (wrap) {
		if (x < 0 || x >= screen_width ) return;
		if (y < 0 || y >= screen_height) return;
		grid[y][x] = alive;
	} else {
		let xx = x; while (xx < 0) xx += screen_width;
		let yy = y; while (yy < 0) yy += screen_height;
		xx = xx % screen_width;
		yy = yy % screen_height;
		grid[yy][xx] = alive;
	}
}
