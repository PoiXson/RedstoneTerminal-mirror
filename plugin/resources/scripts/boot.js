/*
 * Redstone Terminal ${project.version}
 * Computers in minecraft
 * © 2023-2025 | AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=5
//#include=gui.js
//#import=players
//#export=pixels


// #include=pong.js



let menu_main = [
	{ "title": "Games", "func": function() { menu = menu_games; } }
];
let menu_games = [
	{ "title": "     < back >",  "func": function() { menu = menu_main; } },
	{ "title": "1. Life",        "script": "games/life.js"      },
	{ "title": "2. Pong",        "script": "games/pong.js"      },
	{ "title": "3. Tic-Tac-Toe", "script": "games/tictactoe.js" },
	{ "title": "4. PacMine",     "script": "games/pacmine.js"   },
	{ "title": "5. Connect 4",   "script": "games/connect4.js"  },
	{ "title": "6. Simon",       "script": "games/simon.js"     }
];

let buttons_width  = screen_width - 3;
let buttons_height = 12;
let num_buttons = Math.floor(screen_height / 12) - 1;
let buttons_top = Math.floor((screen_height - (num_buttons * 12.0)) / 2.0);

let menu = menu_main;
let scroll_index = 0;



function loop() {
	ClearPixels();
	let num_entries = menu.length;
	let y;
	if (num_entries > num_buttons) {
		// scroll up
		if (scroll_index > 0) {
			y = buttons_top - buttons_height;
			DrawButton(
				1, y,
				buttons_width, buttons_height,
				3, 1,
				"            /\\",
				//font  style    size
				null, Font.BOLD, 9,
				// border    fill         highlight   text
				Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
			);
		}
		// scroll down
		if (scroll_index + num_buttons < num_entries) {
			y = buttons_top + (num_buttons * buttons_height);
			DrawButton(
				1, y,
				buttons_width, buttons_height,
				3, 1,
				"            \\/",
				//font  style    size
				null, Font.BOLD, 9,
				// border    fill         highlight   text
				Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
			);
		}
	}
	let title;
	for (let i=0; i<num_buttons; i++) {
		// menu entry
		let index = scroll_index + i;
		if (index >= num_entries)
			break;
		y = buttons_top + (i * buttons_height);
		title = "";
		if (!isNullOrEmpty(menu[index])
		&&  menu[index].hasOwnProperty("title"))
			title = menu[index].title;
		DrawButton(
			1, y,
			buttons_width, buttons_height,
			3, 1,
			title,
			//font  style    size
			null, Font.PLAIN, 9,
			// border    fill         highlight   text
			Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
		);
	}
}



function click(player, x, y) {
	let index = Math.floor((y - buttons_top) / buttons_height);
	if (index < -1 || index > num_buttons)
		return;
	// scroll up
	if (index == -1) {
		scroll_index--;
		if (scroll_index < 0)
			scroll_index = 0;
	} else
	// scroll down
	if (index >= num_buttons) {
		let num_entries = menu.length;
		if (num_entries >= num_buttons) {
			scroll_index++;
			if (scroll_index > num_entries - num_buttons)
				scroll_index = num_entries - num_buttons;
		}
	// click menu entry
	} else {
		index += scroll_index;
		let entry = menu[index];
		if (!isNullOrEmpty(entry)) {
			if (entry.hasOwnProperty("script")) {
				let writer = new java.io.FileWriter(file_self);
				writer.append("//#include=");
				writer.append(entry["script"]);
				writer.append("\n");
				writer.close();
				reboot();
			} else
			if (entry.hasOwnProperty("func"))
				entry["func"]();
		}
	}
}
