//#fps=20
//#export=pixels



var wrap = false;
var sick_chance = 20.0;

var extinct = 0;

var gridA = [];
var gridB = [];
for (iy=0; iy<screen_height; iy++) {
	gridA[iy] = [];
	gridB[iy] = [];
	for (ix=0; ix<screen_width; ix++) {
		gridA[iy][ix] = false;
		gridB[iy][ix] = false;
	}
}
seed();



function seed() {
	for (iy=0; iy<screen_height; iy++) {
		for (ix=0; ix<screen_width; ix++) {
			let rnd = Math.floor(Math.random() * 12.0);
			gridA[iy][ix] = (rnd == 0);
		}
	}
}



function loop() {
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
				if (c < 2 || c > 3) {
					setAlive(ix, iy, false);
					extinct = 0;
				} else
				if (Math.floor(Math.random() * sick_chance) == 0) {
					setAlive(ix, iy, false);
					extinct = 0;
				}
			} else {
				if (c == 3) setAlive(ix, iy, true);
			}
		}
	}
	if (extinct >= 10) {
		seed();
		extinct = 0;
	} else {
		for (iy=0; iy<screen_height; iy++) {
			for (ix=0; ix<screen_width; ix++)
				gridA[iy][ix] = gridB[iy][ix];
		}
	}
	let color_white = Color.WHITE.getRGB();
	let color_black = Color.BLACK.getRGB();
	for (iy=0; iy<screen_height; iy++) {
		for (ix=0; ix<screen_width; ix++)
			pixels[iy][ix] = (gridA[iy][ix] ? color_white : color_black);
	}
}



function isAlive(x, y) {
	if (wrap) {
		let xx = x; while (xx < 0) xx += screen_width;
		let yy = y; while (yy < 0) yy += screen_height;
		xx = xx % screen_width;
		yy = yy % screen_height;
		return gridA[yy][xx];
	} else {
		if (x < 0 || x >= screen_width ) return false;
		if (y < 0 || y >= screen_height) return false;
		return gridA[y][x];
	}
}

function setAlive(x, y, alive) {
	if (wrap) {
		if (x < 0 || x >= screen_width ) return;
		if (y < 0 || y >= screen_height) return;
		gridB[y][x] = alive;
	} else {
		let xx = x; while (xx < 0) xx += screen_width;
		let yy = y; while (yy < 0) yy += screen_height;
		xx = xx % screen_width;
		yy = yy % screen_height;
		gridB[yy][xx] = alive;
	}
}
