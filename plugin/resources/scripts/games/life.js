/*
 * Redstone Terminal - Computers in minecraft
 * copyright 2023
 * license AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=20
//#export=pixels



let wrap = true;
let sick_chance = 50.0;

let extinct = 0;

let gridA = [];
let gridB = [];
for (let iy=0; iy<screen_height; iy++) {
	gridA[iy] = [];
	gridB[iy] = [];
	for (let ix=0; ix<screen_width; ix++) {
		gridA[iy][ix] = false;
		gridB[iy][ix] = false;
	}
}
seed();



function seed() {
	let chance = Math.floor(Math.random() * 8) + 1;
	for (let iy=0; iy<screen_height; iy++) {
		for (let ix=0; ix<screen_width; ix++) {
			let rnd = Math.floor(Math.random() * chance);
			gridA[iy][ix] = (rnd == 0);
		}
	}
}



function loop() {
	extinct++;
	for (let iy=0; iy<screen_height; iy++) {
		for (let ix=0; ix<screen_width; ix++) {
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
		for (let iy=0; iy<screen_height; iy++) {
			for (let ix=0; ix<screen_width; ix++)
				gridA[iy][ix] = gridB[iy][ix];
		}
	}
	for (let iy=0; iy<screen_height; iy++) {
		for (let ix=0; ix<screen_width; ix++)
			pixels.set(ix, iy, (gridA[iy][ix] ? Color.WHITE : Color.BLACK));
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
