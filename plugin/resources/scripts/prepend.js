
importClass(Packages.java.io.File);
importClass(Packages.java.nio.file.Files);
importClass(Packages.java.nio.file.FileSystems);

importClass(Packages.java.awt.Color);
importClass(Packages.java.awt.Font);
importClass(Packages.java.awt.image.BufferedImage);

importClass(Packages.com.poixson.utils.FileUtils);
importClass(Packages.com.poixson.utils.GraphicsUtils);
importClass(Packages.com.poixson.commonmc.utils.MapUtils);



function isNullOrEmpty(value) {
	if (!value)             return true;
	if (value == undefined) return true;
	if (value == "")        return true;
	if (value.length == 0)  return true;
	return false;
}



function println() {
	out.println();
}
function print(line) {
	if (isNullOrEmpty(line)) {
		println();
	} else {
		out.println(line);
	}
}
function error(line) {
	if (isNullOrEmpty(line)) {
		println();
	} else {
		out.println("ERROR: " + line);
	}
}



function Trim(str, trim) {
	return TrimFront( TrimEnd(str, trim), trim);
}
function Trim_front(str, trim) {
	if (isNullOrEmpty(str) || isNullOrEmpty(trim))
		return str;
	let trim_len = trim.length;
	while (true) {
		if (str.length < trim_len) return str;
		if (!str.startsWith(trim)) return str;
		str = str.substring(trim_len);
	}
}
function TrimEnd(str, trim) {
	if (isNullOrEmpty(str) || isNullOrEmpty(trim))
		return str;
	let trim_len = trim.length;
	while (true) {
		if (str.length < trim_len)
			return str;
		if (!str.endsWith(trim))
			return str;
		str = str.substring(0, 0-trim_len);
	}
}



function ForceStartsWith(str, prepend) {
	if (isNullOrEmpty(str))      return str;
	if (str.startsWith(prepend)) return str;
	return prepend + str;
}
function ForceEndsWith(str, append) {
	if (isNullOrEmpty(str))   return str;
	if (str.endsWith(append)) return str;
	return str + append;
}



function ReplaceAt(str, index, replace) {
	return str.substr(0, index) + replace + str.substr(index+replace.length);
}



function toHex(value) {
	let str = toHexChar(value);
	if (str.length == 0) return "x00";
	if (str.length == 1) return "x0"+str;
	return "x"+str;
}
function toHexChar(value) {
	return value.toString(16).toUpperCase();
}



// --------------------------------------------------



// load/include a file
function loadjs(file) {
	file = ""+file;
	if (file.startsWith("/")
	||  file.includes("..") ) {
		error("error: Invalid file: "+file);
		return;
	}
	file = ForceEndsWith(file, ".js");
	if (options.Debug)
		print("Loading file: "+file);
	const f = FileSystems.getDefault().getPath(wePath, file);
	const reader = Files.newBufferedReader(f);
	return ""+CharStreams.toString(reader);
}



function LoadImage(filename) {
	return GraphicsUtils.LoadImage(
		FileUtils.OpenResource(plugin.getClass(), "img/" + filename)
	)
}



// --------------------------------------------------



function GetTextWidth(text, size) {
	return Math.ceil(0.6 * text.length * size);
}

function DrawText(text, x, y, font, style, size, color, back_color) {
	if (isNullOrEmpty(font )) font = "Noto Sans Mono";
	if (isNullOrEmpty(style)) style = Font.PLAIN;
	if (isNullOrEmpty(size )) size = 8;
	if (isNullOrEmpty(color)) color = Color.WHITE;
	else                      color = new Color(color);
	let w = GetTextWidth(text, size);
	let h = size;
	let img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	let graphics = img.createGraphics();
	graphics.setFont(new Font(font, style, size));
	graphics.setColor(color);
	graphics.setBackground(isNullOrEmpty(back_color) ? Color.BLACK : new Color(back_color));
	graphics.drawString(text, 0, size);
	MapUtils.DrawImagePixels(pixels, x, y, img, isNullOrEmpty(back_color) ? Color.BLACK.getRGB() : null);
	graphics.dispose();
}
