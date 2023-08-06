/*
 * Redstone Terminal - Computers in minecraft
 * copyright 2023
 * license AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=20
//#export=pixels



// load images
let img_wallpaper      = LoadImage("monitor/computer_wallpaper_86-67.png");
let img_cursor         = LoadImage("monitor/computer_cursor.png"         );
let img_cursor_mask    = LoadImage("monitor/computer_cursor_mask.png"    );
//let img_hourglass      = LoadImage("monitor/computer_hourglass.png"      );
//let img_hourglass_mask = LoadImage("monitor/computer_hourglass_mask.png" );
if (isNullOrEmpty(img_wallpaper     )) error("Failed to load image: computer_wallpaper_86-67.png");
if (isNullOrEmpty(img_cursor        )) error("Failed to load image: computer_cursor.png"         );
if (isNullOrEmpty(img_cursor_mask   )) error("Failed to load image: computer_cursor_mask.png"    );
//if (isNullOrEmpty(img_hourglass     )) error("Failed to load image: computer_hourglass.png"      );
//if (isNullOrEmpty(img_hourglass_mask)) error("Failed to load image: computer_hourglass_mask.png" );



let c = 0;
function loop() {
//	if (!isNullOrEmpty(img_wallpaper))
		MapUtils.DrawImagePixels(pixels, 0, 0, img_wallpaper);
	MapUtils.DrawImagePixels(pixels, 55, 48, img_cursor, img_cursor_mask);
//	DrawCursors();
}

function DrawCursors() {
	if (actions.length > 0)
		c = 10;
	if (c > 0) {
		c--;
		cursors.forEach((player, xy) => {
			MapUtils.DrawImagePixels(pixels, xy.a-4, xy.b-6, img_hourglass, img_hourglass_mask);
		});
	} else {
		cursors.forEach((player, xy) => {
			MapUtils.DrawImagePixels(pixels, xy.a, xy.b, img_cursor, img_cursor_mask);
		});
	}
}
