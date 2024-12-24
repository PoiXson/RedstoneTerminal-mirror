/*
 * Redstone Terminal ${project.version}
 * Computers in minecraft
 * © 2023-2025 | AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=12
//#export=pixels

importClass(Packages.com.poixson.utils.FastNoiseLiteD);



let count = 0;
let noise = new FastNoiseLiteD();
noise.setFrequency(5.0);



function loop() {
	count++;
	let value;
	for (let iy=0; iy<screen_height; iy++) {
		for (let ix=0; ix<screen_width; ix++) {
			value = noise.getNoise(ix, count, iy);
			pixels[iy][ix] = (value<-0.1 ? Color.WHITE : Color.BLACK);
		}
	}
}

function click() {
}
