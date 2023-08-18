/*
 * Redstone Terminal - Computers in minecraft
 * copyright 2023
 * license AGPL-3
 * lorenzo at poixson.com
 * https://poixson.com/
 */

//#fps=20
//#include=gui.js
//#import=cursors
//#export=pixels



let player1 = null;
let player2 = null;

const PLAYER_PADDLE_SPEED = 0.1;
const     AI_PADDLE_SPEED = 0.18;

let score_1 = 0;
let score_2 = 0;
let score_points = 0;

let paddle_1 = 0.5;
let paddle_2 = 0.5;
let paddle_size = 0;
let paddle_half = 0;
let paddle_shrink = 0.99;
let paddle_angle = -2.4;

let ball_x = screen_width  * 0.5;
let ball_y = screen_height * 0.5;
let vel_x = 1.6;
let vel_y = 0.5;

const GAME_STATE = {
	MENU_MAIN:        0,
	MENU_NUM_PLAYERS: 1,
	MENU_SEL_PLAYERS: 2,
	TOP_SCORES:       3,
	PLAYING:          4,
	END_GAME:         5
};
let state = GAME_STATE.MENU_MAIN;

const menu_button_x          = 12;
const menu_button_A_y        = 20;
const menu_button_B_y        = 35;
const menu_button_w          = 62;
const menu_button_h          = 12;
const menu_button_pad_x      =  5;
const menu_button_pad_y      =  1;
const menu_num_players_pad_x = 10;
const menu_sel_players_pad_x =  4;
const menu_sel_players_go_x  = 31;
const menu_sel_players_go_y  = 50;
const menu_sel_players_go_w  = 25;



function loop() {
	ClearPixels();
	switch (state) {
	case GAME_STATE.MENU_MAIN:        display_menu_main();        break;
	case GAME_STATE.MENU_NUM_PLAYERS: display_menu_num_players(); break;
	case GAME_STATE.MENU_SEL_PLAYERS: display_menu_sel_players(); break;
	case GAME_STATE.TOP_SCORES:       display_top_scores();       break;
	case GAME_STATE.PLAYING:          game_loop();                break;
	case GAME_STATE.END_GAME:         display_end_game();         break;
	default: break;
	}
}

function click(player, x, y) {
	switch (state) {
	case GAME_STATE.MENU_MAIN:        click_menu_main       (player, x, y); break;
	case GAME_STATE.MENU_NUM_PLAYERS: click_menu_num_players(player, x, y); break;
	case GAME_STATE.MENU_SEL_PLAYERS: click_menu_sel_players(player, x, y); break;
	case GAME_STATE.TOP_SCORES:       state = GAME_STATE.MENU_MAIN;         break;
	case GAME_STATE.END_GAME:         state = GAME_STATE.MENU_MAIN;         break;
	case GAME_STATE.PLAYING: break;
	default: break;
	}
}

function click_menu_main(player, x, y) {
	// button - New Game
	if (CursorInRange(menu_button_x, menu_button_A_y,
	menu_button_w, menu_button_h, x, y)) {
		state = GAME_STATE.MENU_NUM_PLAYERS;
		return;
	}
	// button - Top Scores
	if (CursorInRange(menu_button_x, menu_button_B_y,
	menu_button_w, menu_button_h, x, y)) {
		state = GAME_STATE.TOP_SCORES;
		return;
	}
}

function click_menu_num_players(player, x, y) {
	// button - 1 Player
	if (CursorInRange(menu_button_x, menu_button_A_y,
	menu_button_w, menu_button_h, x, y)) {
		player1 = player.get("name");
		player2 = null;
		ResetGameStats();
		state = GAME_STATE.PLAYING;
		return;
	}
	// button - 2 Player
	if (CursorInRange(menu_button_x, menu_button_B_y,
	menu_button_w, menu_button_h, x, y)) {
		state = GAME_STATE.MENU_SEL_PLAYERS;
		return;
	}
	// default
	state = GAME_STATE.MENU_MAIN;
}

function click_menu_sel_players(player, x, y) {
	// button - 1 Player
	if (CursorInRange(menu_button_x, menu_button_A_y,
	menu_button_w, menu_button_h, x, y)) {
		player1 = player.get("name");
		return;
	}
	// button - 2 Player
	if (CursorInRange(menu_button_x, menu_button_B_y,
	menu_button_w, menu_button_h, x, y)) {
		player2 = player.get("name");
		return;
	}
	// button - GO
	if (!isNullOrEmpty(player1)
	&&  !isNullOrEmpty(player2)) {
		if (CursorInRange(menu_sel_players_go_x, menu_sel_players_go_y,
		menu_sel_players_go_w, menu_button_h, x, y)) {
			ResetGameStats();
			state = GAME_STATE.PLAYING;
			return;
		}
	}
	// default
	state = GAME_STATE.MENU_NUM_PLAYERS;
}



// -------------------------------------------------------------------------------
// menu



function display_menu_main() {
//TODO: change this font
	DrawText("PONG", 18, 0, null, Font.BOLD, 16, Color.WHITE);
	// button - New Game
	DrawButton(
		menu_button_x, menu_button_A_y,
		menu_button_w, menu_button_h,
		menu_button_pad_x, menu_button_pad_y,
		"New Game",
		//font  style    size
		null, Font.BOLD, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
	// button - Top Scores
	DrawButton(
		menu_button_x, menu_button_B_y,
		menu_button_w, menu_button_h,
		menu_button_pad_x, menu_button_pad_y,
		"Top Scores",
		//font  style    size
		null, Font.BOLD, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
}

function display_menu_num_players() {
	DrawText("Number of", 18, 0, null, Font.BOLD, 9, Color.WHITE);
	DrawText("Players",   26, 8, null, Font.BOLD, 9, Color.WHITE);
	// button - 1 Player
	DrawButton(
		menu_button_x, menu_button_A_y,
		menu_button_w, menu_button_h,
		menu_num_players_pad_x, menu_button_pad_y,
		"1 Player",
		//font  style    size
		null, Font.BOLD, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
	// button - 2 Player
	DrawButton(
		menu_button_x, menu_button_B_y,
		menu_button_w, menu_button_h,
		menu_num_players_pad_x, menu_button_pad_y,
		"2 Player",
		//font  style    size
		null, Font.BOLD, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
}

function display_menu_sel_players() {
	DrawText("Players", 22, 2, null, Font.BOLD, 10, Color.WHITE);
	// button - 1 Player
	DrawButton(
		menu_button_x, menu_button_A_y,
		menu_button_w, menu_button_h,
		menu_sel_players_pad_x, menu_button_pad_y,
		(isNullOrEmpty(player1) ? "<Player 1>" : TruncateString(""+player1, 10)),
		//font  style    size
		null, Font.PLAIN, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
	// button - 2 Player
	DrawButton(
		menu_button_x, menu_button_B_y,
		menu_button_w, menu_button_h,
		menu_sel_players_pad_x, menu_button_pad_y,
		(isNullOrEmpty(player2) ? "<Player 2>" : TruncateString(""+player2, 10)),
		//font  style    size
		null, Font.PLAIN, 9,
		// border    fill         highlight   text
		Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
	);
	// button - GO
	if (!isNullOrEmpty(player1)
	&&  !isNullOrEmpty(player2)) {
		DrawButton(
			menu_sel_players_go_x, menu_sel_players_go_y,
			menu_sel_players_go_w, menu_button_h,
			menu_button_pad_x, menu_button_pad_y,
			"GO",
			//font  style    size
			null, Font.BOLD, 9,
			// border    fill         highlight   text
			Color.WHITE, Color.BLACK, Color.GRAY, Color.WHITE
		);
	}
}



function display_top_scores() {
	DrawText("Top Scores", 12, 0, null, Font.BOLD, 10, Color.WHITE);
	for (let i=0; i<7; i++) {
//TODO
		DrawText("100 - lorenzop", 2, (i*7)+12, null, Font.PLAIN, 7, Color.WHITE);
	}
}



function display_end_game() {
//TODO
}



// -------------------------------------------------------------------------------
// game loop



function game_loop() {
	if (isNullOrEmpty(player1)) {
		paddle_1 -= ((paddle_1 - (ball_y/screen_height)) * DEFAULT_PADDLE_SPEED_AI) * (1.0-(ball_x/screen_width));
	} else {
		let cursor = players.get(player1);
		if (!isNullOrEmpty(cursor))
			paddle_1 -= (paddle_1 - (cursor.get("cursor_y") / screen_height)) * DEFAULT_PADDLE_SPEED_PLAYER;
	}
	if (isNullOrEmpty(player2)) {
		paddle_2 -= ((paddle_2 - (ball_y/screen_height)) * DEFAULT_PADDLE_SPEED_AI) * (ball_x/screen_width);
	} else {
		let cursor = players.get(player2);
		if (!isNullOrEmpty(cursor))
			paddle_2 -=  (paddle_2 - (cursor.get("cursor_y") / screen_height)) * DEFAULT_PADDLE_SPEED_PLAYER;
	}
	if (paddle_1 < 0.0) paddle_1 = 0.0; else
	if (paddle_1 > 1.0) paddle_1 = 1.0;
	if (paddle_2 < 0.0) paddle_2 = 0.0; else
	if (paddle_2 > 1.0) paddle_2 = 1.0;
	// ball movement
	let last_x = ball_x;
	ball_x += vel_x;
	ball_y += vel_y;
	if (ball_x <= 0.0          ) { ball_x = 0.0 - ball_x;                             vel_x = 0.0 - vel_x; score_2++; } else
	if (ball_x >= screen_width ) { ball_x = screen_width  + (screen_width  - ball_x); vel_x = 0.0 - vel_x; score_1++; }
	if (ball_y <= 0.0          ) { ball_y = 0.0 - ball_y;                             vel_y = 0.0 - vel_y;            } else
	if (ball_y >= screen_height) { ball_y = screen_height + (screen_height - ball_y); vel_y = 0.0 - vel_y;            }
	// ball hits paddle a
	if (ball_x <= 5.0 && last_x > 5.0) {
		let div_y = ball_y / screen_height;
		let half  = paddle_half / screen_height;
		if (div_y > paddle_1-half
		&&  div_y < paddle_1+half) {
			ball_x = last_x;
			vel_x = 0.0 - vel_x;
			vel_y += ((div_y - paddle_1) / half) * paddle_angle;
			vel_y = vel_y % 2.0;
			UpdatePaddleSize();
			score_points++;
		}
	} else
	// ball hits paddle b
	if (ball_x >= screen_width-5.0 && last_x < screen_width-5.0) {
		let div_y = ball_y / screen_height;
		let half = paddle_half / screen_height;
		if (div_y > paddle_2-half
		&&  div_y < paddle_2+half) {
			ball_x = last_x;
			vel_x = 0.0 - vel_x;
			vel_y += ((div_y - paddle_2) / half) * paddle_angle;
			vel_y = vel_y % 2.0;
			UpdatePaddleSize();
			score_points++;
		}
	}
	// score
	let score_top = "" + score_1 + " - " + score_2;
	let score_bot = "" + score_points;
	let top_left = Math.round( (screen_width - GetTextWidth(score_top, 8)) * 0.5 );
	let bot_left = Math.round( (screen_width - GetTextWidth(score_bot, 8)) * 0.5 );
	DrawText(score_top, top_left, 0,                null, 0, 8, Color.WHITE, Color.BLACK);
	DrawText(score_bot, bot_left, screen_height-10, null, 0, 8, Color.WHITE, Color.BLACK);
	DrawPaddle(             5, paddle_1);
	DrawPaddle(screen_width-5, paddle_2);
	DrawBall(ball_x, ball_y);
}



function ResetGameStats() {
	score_1 = 0;
	score_2 = 0;
	score_points = 0;
	ball_x = screen_width  * 0.5;
	ball_y = screen_height * 0.5;
	// random ball serve direction
	if (0 == (Math.round(Math.random() * 9999.0) % 2))
		vel_x = 0.0 - vel_x;
	UpdatePaddleSize();
}



function UpdatePaddleSize() {
	paddle_size = 20;
	for (let i=0; i<score_points; i++)
		paddle_size *= paddle_shrink;
	paddle_half = Math.round(paddle_size * 0.5);
}



function DrawPaddle(x, pos) {
	let y = Math.round(pos * screen_height);
	let yy;
	for (let iy=0-paddle_half; iy<paddle_half; iy++) {
		yy = iy+y;
		if (yy >= 0 && yy < screen_height)
			pixels[yy][x] = Color.WHITE;
	}
}

function DrawBall(x, y) {
	let xx = Math.floor(x);
	let yy = Math.floor(y);
	if (xx < 0.0) xx = 0.0; else
	if (xx >= screen_width)  xx = screen_width  - 1;
	if (yy < 0.0) yy = 0.0; else
	if (yy >= screen_height) yy = screen_height - 1;
	pixels[yy][xx] = Color.WHITE;
}
