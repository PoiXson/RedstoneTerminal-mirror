
importClass(Packages.java.awt.Color);

importClass(Packages.com.poixson.commonmc.tools.scripts.screen.MapScreen);



// load images
var img_wallpaper      = LoadImage("monitor/computer_wallpaper_128.png" );
var img_cursor         = LoadImage("monitor/computer_cursor.png"        );
var img_cursor_mask    = LoadImage("monitor/computer_cursor_mask.png"   );
var img_hourglass      = LoadImage("monitor/computer_hourglass.png"     );
var img_hourglass_mask = LoadImage("monitor/computer_hourglass_mask.png");
if (isNullOrEmpty(img_wallpaper     )) error("Failed to load image: computer_wallpaper_128.png" );
if (isNullOrEmpty(img_cursor        )) error("Failed to load image: computer_cursor.png"        );
if (isNullOrEmpty(img_cursor_mask   )) error("Failed to load image: computer_cursor_mask.png"   );
if (isNullOrEmpty(img_hourglass     )) error("Failed to load image: computer_hourglass.png"     );
if (isNullOrEmpty(img_hourglass_mask)) error("Failed to load image: computer_hourglass_mask.png");



var c = 0;
function onRender() {
	if (!isNullOrEmpty(img_wallpaper))
		MapScreen.DrawImagePixels(pixels, img_wallpaper, 0, 0);
	DrawCursors();
}

function DrawCursors() {
	if (actions.length > 0)
		c = 10;
	if (c > 0) {
		c--;
		cursors.forEach((player, xy) => {
			MapScreen.DrawImagePixels(pixels, img_hourglass, img_hourglass_mask, xy.a-4, xy.b-6);
		});
	} else {
		cursors.forEach((player, xy) => {
			MapScreen.DrawImagePixels(pixels, img_cursor, img_cursor_mask, xy.a, xy.b);
		});
	}
}
