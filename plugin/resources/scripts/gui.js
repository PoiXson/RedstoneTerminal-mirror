/*
 * Redstone Terminal - Computers in minecraft
 * copyright 2023
 * license AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */



function DrawButton(x, y, w, h, pad_x, pad_y, text, font, style, size,
		color_border, color_fill, color_highlight, color_text) {
	let back = (CursorsInRange(x, y, w, h) ? color_highlight : color_fill);
	DrawRect(x, y, x+w, y+h, color_border, back, 1);
	DrawText(text, x+pad_x, y+pad_y, font, style, size, color_text, color_fill);
}



// -------------------------------------------------------------------------------



function ClearPixels() {
	for (let iy=0; iy<screen_height; iy++) {
		for (let ix=0; ix<screen_width; ix++)
			pixels[iy][ix] = Color.BLACK;
	}
}

function SetPixel(x, y, color) {
	if (x < 0 || x >= screen_width
	||  y < 0 || y >= screen_height)
		return;
	pixels[y][x] = color;
}



function CursorsInRange(x, y, w, h) {
	for (let player of players.values()) {
		let cursor_x = player.get("cursor_x");
		let cursor_y = player.get("cursor_y");
		if (CursorInRange(x, y, w, h, cursor_x, cursor_y))
			return true;
	}
	return false;
}
function CursorInRange(x, y, w, h, cursor_x, cursor_y) {
	return (
		cursor_x > x  &&  cursor_x <= x+w  &&
		cursor_y > y  &&  cursor_y <= y+h
	);
}



// -------------------------------------------------------------------------------
// text



function GetTextWidth(text, size) {
	return Math.ceil(0.8 * text.length * size);
}
function GetTextHeight(size) {
return 20;
}

function DrawText(text, x, y, font, style, size, color, back_color) {
	if (isNullOrEmpty(font )) font  = "Noto Sans Mono";
	if (isNullOrEmpty(style)) style = Font.PLAIN;
	if (isNullOrEmpty(size )) size  = 8;
	if (isNullOrEmpty(color)) color = Color.WHITE;
	let w = GetTextWidth(text, size);
	let h = size + 2;
	let img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	let graphics = img.createGraphics();
	graphics.setFont(new Font(font, style, size));
	graphics.setColor(color);
	graphics.setBackground(isNullOrEmpty(back_color) ? Color.BLACK : back_color);
	graphics.drawString(text, 0, size);
	if (isNullOrEmpty(back_color)) ScriptUtils.DrawImagePixels_ColorMask(pixels, x, y, img, Color.BLACK);
	else                           ScriptUtils.DrawImagePixels_ColorMask(pixels, x, y, img, back_color );
	graphics.dispose();
}



// -------------------------------------------------------------------------------
// shapes



function DrawRectFill(x1, y1, x2, y2, color) {
	let x_min = Math.min(x1, x2); let y_min = Math.min(y1, y2);
	let x_max = Math.max(x1, x2); let y_max = Math.max(y1, y2);
	for (let iy=y_min; iy<y_max; iy++) {
		for (let ix=x_min; ix<x_max; ix++)
			SetPixel(ix, iy, color);
	}
}

function DrawRect(x1, y1, x2, y2, color, fill, width) {
	if (isNullOrEmpty(width)) width = 1;
	// outline
	for (let i=0; i<width; i++) {
		DrawLineHoriz(x1+i, x2-i,   y1+i, color); // top
		DrawLineHoriz(x1+i, x2-i+1, y2-i, color); // bottom
		DrawLineVert (x1+i, y1+i,   y2-i, color); // left
		DrawLineVert (x2-i, y1+i,   y2-i, color); // right
	}
	// fill
	if (!isNullOrEmpty(fill))
		DrawRectFill(x1+width, y1+width, x2-width+1, y2-width+1, fill);
}

function DrawLineHoriz(x1, x2, y, color) {
	let x_min = Math.min(x1, x2);
	let x_max = Math.max(x1, x2);
	for (let ix=x_min; ix<x_max; ix++)
		SetPixel(ix, y, color);
}
function DrawLineVert(x, y1, y2, color) {
	let y_min = Math.min(y1, y2);
	let y_max = Math.max(y1, y2);
	for (let iy=y_min; iy<y_max; iy++)
		SetPixel(x, iy, color);
}
