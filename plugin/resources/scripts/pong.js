//#fps=20
//#export=pixels

importClass(Packages.java.lang.Thread);



var player1 = "lorenzop";
var player2 = "DuckTransport";

var score_1 = 0;
var score_2 = 0;
var score_points = 0;

var paddle_1 = 0.5;
var paddle_2 = 0.5;
var paddle_size  = 20;
var paddle_half  = Math.round(paddle_size * 0.5);
var paddle_shrink = 0.99;
var paddle_angle = -2.4;

var ball_x = screen_width  * 0.5;
var ball_y = screen_height * 0.5;
var vel_x = 1.6;
var vel_y = 0.5;

var color_black = Color.BLACK.getRGB();
var color_white = Color.WHITE.getRGB();



function loop() {
//	let isPlayer2 = (player.getName() == player2);
//	if (isPlayer2) {
		for (let iy=0; iy<screen_height; iy++) {
			for (let ix=0; ix<screen_width; ix++)
				pixels[iy][ix] = color_black;
		}
		let cursor_1 = cursors.get(player1);
		let cursor_2 = cursors.get(player2);
		if (!isNullOrEmpty(cursor_1)) paddle_1 -= (paddle_1 - (cursor_1.b / screen_height)) * 0.1;
		if (!isNullOrEmpty(cursor_2)) paddle_2 -= (paddle_2 - (cursor_2.b / screen_height)) * 0.1;
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
				ShrinkPaddles();
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
				ShrinkPaddles();
				score_points++;
			}
		}
//	}
	// score
	let score_top;



score_top = "" + score_1 + " - " + score_2;
//score_top = "" + (score_2>99 ? 99 : score_2) + " - " + (score_1>99 ? 99 : score_1);
//	if (isPlayer2) score_top = "" + (score_2>99 ? 99 : score_2) + " - " + (score_1>99 ? 99 : score_1);
//	else           score_top = "" + (score_1>99 ? 99 : score_1) + " - " + (score_2>99 ? 99 : score_2);
	let score_bot = "" + score_points;
//TODO: why +6?
//TODO: make transparent background
	let top_left = Math.round( (screen_width - GetTextWidth(score_top, 8)) * 0.5 );
	let bot_left = Math.round( (screen_width - GetTextWidth(score_bot, 8)) * 0.5 );
	DrawText(score_top, top_left, 0,                null, 0, 8, color_white, Color.BLACK.getRGB());
	DrawText(score_bot, bot_left, screen_height-10, null, 0, 8, color_white, Color.BLACK.getRGB());
//	// player 2
//	if (isPlayer2) {
//		DrawPaddle(             5, paddle_2);
//		DrawPaddle(screen_width-5, paddle_1);
//		DrawBall(screen_width-ball_x, ball_y);
//	// other players
//	} else {
		DrawPaddle(             5, paddle_1);
		DrawPaddle(screen_width-5, paddle_2);
		DrawBall(ball_x, ball_y);
//	}
}



function ShrinkPaddles() {
	paddle_size *= paddle_shrink;
	paddle_half  = Math.round(paddle_size * 0.5);
	if (paddle_half < 1) paddle_half = 1;
}



function DrawPaddle(x, pos) {
	let y = Math.round(pos * screen_height);
	let yy;
	for (let iy=0-paddle_half; iy<paddle_half; iy++) {
		yy = iy+y;
		if (yy >= 0 && yy < screen_height)
			pixels[yy][x] = color_white;
	}
}

function DrawBall(x, y) {
	let xx = Math.floor(x);
	let yy = Math.floor(y);
	if (xx < 0.0) xx = 0.0; else
	if (xx >= screen_width)  xx = screen_width  - 1;
	if (yy < 0.0) yy = 0.0; else
	if (yy >= screen_height) yy = screen_height - 1;
	pixels[yy][xx] = color_white;
}
