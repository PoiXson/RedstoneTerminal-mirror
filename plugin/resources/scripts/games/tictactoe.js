/*
 * Redstone Terminal ${project.version}
 * Computers in minecraft
 * © 2023-2025 | AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=2
//#include=gui.js
//#import=players
//#export=pixels



function loop() {
	let x = Math.round(screen_width  / 3);
	let y = Math.round(screen_height / 3);
	for (let ix=0; ix<screen_width;  ix++) pixels[y  ][ix ] = Color.WHITE;
	for (let ix=0; ix<screen_width;  ix++) pixels[y*2][ix ] = Color.WHITE;
	for (let iy=0; iy<screen_height; iy++) pixels[iy ][x  ] = Color.WHITE;
	for (let iy=0; iy<screen_height; iy++) pixels[iy ][x*2] = Color.WHITE;
	DrawText("X",     8,   0, null, null, 20, Color.WHITE);
	DrawText("0", x  +9, y-1, null, null, 20, Color.WHITE);
	DrawText("X",     8, y+y, null, null, 20, Color.WHITE);
	DrawText("0", x+x+8, y+y, null, null, 20, Color.WHITE);
}
