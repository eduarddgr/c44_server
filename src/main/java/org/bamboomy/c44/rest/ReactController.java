/**
	Copyright 2026 Sander Theetaert

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

**/

package org.bamboomy.c44.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.bamboomy.c44.domain.BoardController;
import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.Judge;
import org.bamboomy.c44.react.board.gui.GuiMove;
import org.bamboomy.c44.react.board.gui.GuiPlace;
import org.bamboomy.c44.react.jsp.BookController;
import org.bamboomy.c44.react.jsp.GameController;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.RemoteBot;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lombok.Setter;

/**
 * Class which handles the communication with the engine.
 */
@CrossOrigin(origins = "http://localhost/", maxAge = 3600)
@RestController
@RequestMapping("/react/")
public class ReactController {

	private int debugColor = -1;
	private int leafColor = -1;

	@Setter
	private static boolean robotPlay = false;

	{
		System.out.println("launching web page...");

		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String url =
		// "http://localhost:8080/c44/c44.html?id=bbad6bd689f97ce9e85f7815cdd47fa8";
		String url = "http://localhost:8080/";

		Runtime rt = Runtime.getRuntime();

		String os = System.getProperty("os.name").toLowerCase();

		if (os.indexOf("win") >= 0) {

			try {
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		/*
		 * Change these to -> "N", "N" to play as a human (you can use the hash in the
		 * url of the interface to go to this user's side) "Y", "Y" to play as a your
		 * bot (don't forget to set the color(s) in the application.properties of the
		 * bot)
		 */
	}

	/**
	 * Takes a user-hash and redirects to the board-method.
	 * 
	 * @param hash is written in the jsp so old games can be retrieved
	 * @return a redirect view (see) getBoard
	 */
	@GetMapping("/getGame/{hash}")
	public synchronized RedirectView hello(@PathVariable("hash") String hash) {

		return new RedirectView(
				"/react/getBoard/" + GameController.getInstance().getGameFromPlayerHash(hash).getMd5() + "/" + hash);
	}

	/**
	 * 
	 * Takes a gameHash and userHash and returns the board rotated for that player
	 * as a JSON.
	 * 
	 * The JSON is not documented but should be human-readeable.
	 * 
	 * @param gameHash A gameHash, hardcoded in the ColorControler (and also visible
	 *                 in the jsp).
	 * @param userHash A userHash, the different userHashes can be found in the jsp.
	 * @return a json which represents the current state of the board (for parsing
	 *         see internal methods of the Robots
	 */
	@RequestMapping(path = "/getBoard/{gameHash}/{userHash}", produces = "application/json")
	public GuiPlace[][] board(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		Color color = GameController.getInstance().getGameFromPlayerHash(userHash).getColorMap().get(userHash);

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		GuiPlace[][] arr;

		try {
			gameMaster.getFilterLatch().await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (debugColor != -1) {

			arr = gameMaster.getBoard().getGuiArray(Color.getBySeq(debugColor).getName(),
					gameMaster.isCurrentPlayer(Color.getBySeq(debugColor)), null, null);

		} else {

			arr = gameMaster.getBoardForCurrentPlayer();
		}

		return arr;
	}

	/**
	 * 
	 * Returns the gameHash from the given userHash.
	 * 
	 * @param hash the userHash (robot or player)
	 * @return the corresponding game hash.
	 */
	@GetMapping("/getGameHash/{hash}")
	public String gameHash(@PathVariable("hash") String hash) {

		return GameController.getInstance().getGameFromPlayerHash(hash).getMd5();
	}

	/**
	 * 
	 * Redirects to the currentPlayer method.
	 * 
	 * @param hash a player hash
	 * @return a redirect to isCurrentPlayer (see the currentPlayer method)
	 */
	@GetMapping("/getIsCurrentPlayer/{hash}")
	public RedirectView amI(@PathVariable("hash") String hash) {

		String gameHash = GameController.getInstance().getGameFromPlayerHash(hash).getMd5();

		return new RedirectView("/react/isCurrentPlayer/" + gameHash + "/" + hash);
	}

	/**
	 * 
	 * Returns the color as int for the userHash given, Green is 0, Blue 1, Red 2
	 * and Yellow 3. (See also the Color enum in the source code for the other
	 * values).
	 * 
	 * If a debug value is set in the interface (see postTurn) this color is used.
	 * 
	 * @param hash the player hash
	 * @return the color
	 */
	@GetMapping("/getMyColor/{hash}")
	public int color(@PathVariable("hash") String hash) {

		if (debugColor != -1) {

			return debugColor;
		}

		return GameController.getInstance().getGameFromPlayerHash(hash).getColorMap().get(hash).getSeq();
	}

	/**
	 * Returns the AllyColor of the given player.
	 * 
	 * @param hash A player hash.
	 * @return The color of the Ally.
	 */
	@GetMapping("/getAllayColor/{hash}")
	public int allayColor(@PathVariable("hash") String hash) {

		return Color.getByName(
				GameController.getInstance().getGameFromPlayerHash(hash).getColorsTakenMap().get(hash).getAllyColor())
				.getSeq();
	}

	/**
	 * 
	 * Returns whether the given userHash for the given gameHash is currently having
	 * its turn (as a (String) boolean ("true" or "false")).
	 * 
	 * @param gameHash the gameHash
	 * @param userHash the userHash
	 * @return whether the userHash is the current player for the given game
	 */
	@RequestMapping(path = "/isCurrentPlayer/{gameHash}/{userHash}")
	public String currentPlayer(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		Color color = GameController.getInstance().getGameFromPlayerHash(userHash).getColorMap().get(userHash);

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (gameMaster.isCurrentPlayer(color)) {

			return "true";

		} else {

			return "false";
		}
	}

	/**
	 * 
	 * Performs a move (if the userHash is the current player for the given
	 * gameHash).
	 * 
	 * If you are in "debug-mode" (you posted a board situation, you choose 
	 * a debug color, the interface is turned to this color).
	 * (see also postTurn and postDebugColor)
	 * That move is sent to the bot as the only move possible
	 * (so you can investigate why it did (or didn't) do a certain move...).
	 * 
	 * @param gameHash  The game
	 * @param userHash  The user
	 * @param pieceHash The piece the user wants to move (to be found in the JSON
	 *                  returned by the board method).
	 * @param square    The destination square the piece wants to move to (also
	 *                  found in this json).
	 * @return a json which can be ignored
	 */
	@RequestMapping(path = "/play/{gameHash}/{userHash}/{pieceHash}/{square}", produces = "application/json")
	public ArrayList<ArrayList<GuiPlace>> play(@PathVariable("gameHash") String gameHash,
			@PathVariable("userHash") String userHash, @PathVariable("pieceHash") String pieceHash,
			@PathVariable("square") String square) {

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		Color color = GameController.getInstance().getGameFromPlayerHash(userHash).getColorMap().get(userHash);

		ArrayList<ArrayList<GuiPlace>> arr = new ArrayList<>();

		boolean switchPlayer = true;

		if (gameMaster.isDebug() && debugColor != -1 && gameMaster.getRecordedPieceHash() == null) {

			System.out.println("recording move...");

			gameMaster.setRecordedMove(pieceHash, square);

			switchPlayer = false;
		}

		gameMaster.movePiece(pieceHash, square, false, false, switchPlayer);

		gameMaster.getBoard().getGuiArray(color.getName(), gameMaster.isCurrentPlayer(color), null, null);

		gameMaster.setMate(false);

		return arr;
	}

	/**
	 * 
	 * Indicates that the mater wants to continue play (and the matee should
	 * contemplate kamikaze).
	 * 
	 * (A mated player is allowed to play one last "kamikaze"-move (it balances the
	 * game) once mate.)
	 * 
	 * The point gained by mating this player is at peril until the other player in
	 * the allyance is mated a well.
	 * 
	 * If you (or your ally) is mated; and you (or your ally) can still mate the
	 * last foe, your allyance gains 1.47 points.
	 * 
	 * Mating the second enemy gains 1.3 points in total if you or your ally is not
	 * mated.
	 * 
	 * @param gameHash the gameHash
	 * @param userHash the userHash
	 */
	@RequestMapping(path = "/kamikaze/{gameHash}/{userHash}")
	public void kamikaze(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		gameMaster.kamikaze();
	}

	/**
	 * 
	 * The postTurn method: changes the turn of the current user.
	 * 
	 * This is a helper-method for debugging purposes only.
	 * 
	 * It will not be present in the online version of the server.
	 * 
	 * @param gameHash the gameHash
	 * @param turn     the desired turn
	 * @param debug    whether the user wants to debug
	 */
	@RequestMapping(path = "/postTurn/{gameHash}/{turn}/{debug}")
	public void postTurn(@PathVariable("gameHash") String gameHash, @PathVariable("turn") String turn,
			@PathVariable("debug") String debug) {

		System.out.println(debug);

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		gameMaster.setDebug(Boolean.parseBoolean(debug));

		gameMaster.setTurn(turn);

		debugColor = Integer.parseInt(turn);
	}

	/**
	 * 
	 * The postDebugColor method:
	 *
	 * Not implemented at the time of writing.
	 * 
	 * This is a helper-method for debugging purposes only.
	 * 
	 * It will not be present in the online version of the server.
	 * 
	 * @param gameHash the gameHash
	 * @param turn     the color to be debugged
	 */
	@RequestMapping(path = "/postDebugColor/{gameHash}/{turn}")
	public void postDebugColor(@PathVariable("gameHash") String gameHash, @PathVariable("turn") String turn) {

		leafColor = Integer.parseInt(turn);

		System.out.println("leafColor = " + leafColor);
	}

	/**
	 * 
	 * The showDebugColor method: whether the debug dialog should be shown.
	 * 
	 * The user interface needs this to show the debug dialog once a board has been posted.
	 * 
	 * This is a helper-method for debugging purposes only.
	 * 
	 * It will not be present in the online version of the server.
	 * 
	 * @param gameHash the
	 * @return a boolean indicating this
	 */
	@RequestMapping(path = "/showDebugColor/{gameHash}")
	public String showDebugColor(@PathVariable("gameHash") String gameHash) {

		if (debugColor != -1 && leafColor == -1) {

			return "true";

		} else {

			return "false";
		}
	}

	/**
	 * 
	 * The describing String of how a player is doing.
	 * 
	 * Gets a human readable description of the state a user is in (check for
	 * instance).
	 * 
	 * Used by the user interface.
	 * 
	 * @param hash the gamehash
	 * @return the described String
	 */
	@GetMapping("/getCurrentPlayerString/{hash}")
	public String currentPlayer(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		String result = "no current player";

		if (gameMaster.getCurrentPlayer() != null) {

			result = gameMaster.getCurrentPlayer().getString();

			if (gameMaster.getCurrentPlayer().isCheck()) {

				result += ": ((s)he is in check...)";
			}
		}

		return result;
	}

	/**
	 * 
	 * Returns the current color (as a String) of the game the user is playing in.
	 * 
	 * @param hash The userHash of the game.
	 * @return the color
	 */
	@GetMapping("/getCurrentColor/{hash}")
	public String currentColor(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.getCurrentColor();
	}

	/**
	 * 
	 * Returns the last played moves of all the players.
	 * 
	 * Used by the user interface.
	 * 
	 * Retuned as JSON (again, not documented, should be understandable on its own).
	 * 
	 * @param userHash the userhash
	 * @return the last moves of that game
	 */
	@RequestMapping(path = "/lastMove/{hash}", produces = "application/json")
	public ArrayList<GuiMove> lastMove(@PathVariable("hash") String userHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(userHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.getLastMoves();
	}

	/**
	 * 
	 * Whether the current player is check (if the given hash is the current
	 * player).
	 * 
	 * @param userHash the userHash
	 * @return a boolean indicating this
	 */
	@RequestMapping(path = "/isCheck/{hash}", produces = "application/json")
	public String isCheck(@PathVariable("hash") String userHash) {

		Color color = GameController.getInstance().getGameFromPlayerHash(userHash).getColorMap().get(userHash);

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(userHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (gameMaster.getCurrentPlayer() == null) {

			return "false";
		}

		return String.valueOf(
				gameMaster.getCurrentPlayer().isCheck() && gameMaster.getCurrentPlayer().getColor().equals(color));
	}

	private synchronized GameMaster getGameMasterFromGameHash(String gameHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMaster(gameHash, robotPlay);

		if (!gameMaster.isInited()) {

			throw new RuntimeException("gameMaster is not inited...");
		}

		return gameMaster;
	}

	/**
	 *
	 * Returns what the current robot is "thinking".
	 * 
	 * After an initial message the input send to the updateOutput method is used.
	 * 
	 * @param hash the userHash
	 * @return what the bot is thinking (debug info, can be anything)
	 */
	@GetMapping("/robotOutput/{hash}")
	public String robotOutput(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		String result = gameMaster.getRobotOutput();

		return result;
	}

	/**
	 * 
	 * Whether the board is "dirty".<br/>
	 * <br/>
	 * 
	 * When a board situation is posted by the postBoard method this value is used
	 * to set the color by the user interface.
	 * 
	 * Will also be removed online.
	 * 
	 * @param hash the player hash
	 * @return the boolean indicating this
	 */
	@GetMapping("/dirty/{hash}")
	public boolean dirty(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.getBoard().isDirty();
	}

	/**
	 * 
	 * Returns the robot board when the bot should currently pounder upon it's move.
	 * 
	 * Sends null otherwise.
	 * 
	 * (You can depend on this method to start letting your bot calculate).
	 * 
	 * @param colorHash the colorHash
	 * @return either the board or null, if the return value is not null the bot is
	 *         expected to play
	 */
	@RequestMapping(path = "/getRobotBoard/{colorHash}", produces = "application/json")
	public GuiPlace[][] robotBoard(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		if (!gameMaster.isInited()) {

			System.out.println("somehow the gameMaster isn't inited (yet???)!!!");

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		boolean deciding = false;

		if (gameMaster.getJudge() != null) {

			deciding = gameMaster.getJudge().isDeciding() && !gameMaster.getJudge().isDecided();
		}

		if (gameMaster.isCurrentRemote(colorHash) && gameMaster.getCurrentPlayer() != null && !deciding
				&& !(gameMaster.getCurrentPlayer().cannotMove() && !gameMaster.getCurrentPlayer().isKamikaze())) {

			System.out.println("sending board to remote bot...");

			gameMaster.resetRemoteOutput();

			return gameMaster.getBoardForCurrentPlayer();

		} else {

			System.out.println("sending null to remote bot...");

			return null;
		}
	}

	/**
	 * 
	 * Plays a move for the bot (if it's the current player).
	 * 
	 * (See also the play method.)
	 * 
	 * @param colorHash the colorHash of the bot
	 * @param pieceHash The piece the bot wants to move (to be found in the JSON
	 *                  returned by the board method).
	 * @param placeHash The destination square the piece wants to move to (also
	 *                  found in this json).
	 */
	@RequestMapping(path = "/playBot/{colorHash}/{pieceHash}/{placeHash}", produces = "application/json")
	public void playBot(@PathVariable("colorHash") String colorHash, @PathVariable("pieceHash") String pieceHash,
			@PathVariable("placeHash") String placeHash) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		gameMaster.movePiece(pieceHash, placeHash, false, true, true);

		if (!gameMaster.isInited()) {

			System.out.println("somehow the gameMaster isn't inited (yet???)!!!");

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println("piece moved...");
	}

	/**
	 * 
	 * A method used to indicate with which color the bot needs to play
	 * (if it plays different colors in the same game).
	 * 
	 * Returns the color of the bot that is going to play (if a remote bot plays
	 * multiple colors).
	 * 
	 * @param colorHash the colorHash
	 * @return the color
	 */
	@RequestMapping(path = "/getColor/{colorHash}")
	public String getColorFromHash(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		String name = gameMaster.getRegisteredRemotez().get(colorHash).getColor().getName();

		System.out.println("sending " + name + " to remote bot...");

		return name;
	}

	/**
	 * 
	 * Returns the color with which the playing color is in alliance with.
	 * 
	 * Allied colors try to defeat the other alliance.
	 * 
	 * Allied colors cannot take each other pieces.
	 * 
	 * @param colorHash the colorHash
	 * @return the ally color as a String
	 */
	@RequestMapping(path = "/getAllianceColor/{colorHash}")
	public String getAlliaceColor(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		RemoteBot bot = gameMaster.getRegisteredRemotez().get(colorHash);

		String allicanceColor = bot.getAlliance().getOtherColor(bot.getColor()).getName();

		System.out.println("sending (Alliance Color: )" + allicanceColor + " to remote bot...");

		return allicanceColor;
	}

	/**
	 * 
	 * Sends the (debug) output of the bot to the server.
	 * 
	 * The output is shown in the interface (by the robotoutput method).
	 * 
	 * @param colorHash the colorHash
	 * @param output    the output
	 * @return always "ok"
	 */
	@PostMapping(value = "/updateOutput/{colorHash}", consumes = "text/html; charset=utf-8", produces = "text/html; charset=utf-8")
	public String updateOutput(@PathVariable("colorHash") String colorHash, @RequestBody String output) {

		ColorController.gameMasterMapper.get(colorHash).setRobotOutput("Remote output:\n\n" + output);

		System.out.println("updateOutput recieved...");

		System.out.println(output);

		return "ok";
	}

	/**
	 * 
	 * Rewrites the board content.
	 * 
	 * After this method the postTurn must be called to continue the game.
	 * 
	 * This is a helper-method for debugging purposes only.
	 * 
	 * It will not be present in the online version of the server.
	 * 
	 * @param robotHash the robotHash
	 * @param boardJson the json replacing the current board
	 * @return always "ok"
	 */
	@PostMapping(value = "/postBoard/{robotHash}", consumes = "application/json", produces = "text/html; charset=utf-8")
	public String postBoard(@PathVariable("robotHash") String robotHash, @RequestBody String boardJson) {

		BoardController.getInstance().getGameMasterFromRobotHash(robotHash).setBoard(boardJson);

		System.out.println("postBoard recieved -> " + robotHash);

		return "ok";
	}

	/**
	 * 
	 * Whether the current player is mate.
	 * 
	 * @param userHash the userHash
	 * @return a boolean whether (s)he is mate
	 */
	@RequestMapping(path = "/mate/{hash}", produces = "application/json")
	public boolean isMate(@PathVariable("hash") String userHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(userHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println("true");

		return gameMaster.currentPlayerIsMate() && !gameMaster.getCurrentPlayer().isKamikaze() && !gameMaster.isDone();
	}

	/**
	 * 
	 * Whether the game is done.
	 * 
	 * The difference between this "done" endpoint and the "finished" endpoint
	 * is that the done endpoint is called from the interface to know whether a game is done,
	 * (so this is usefull only once at the end of an active game)
	 * while the finished endpoint retains the state of a game, 
	 * also when it is not active anymore.
	 * 
	 * (The "finished" endpoint is also called when a finished game is opened from the overview jsp.)
	 * 
	 * @param hash the userHash
	 * @return a boolean
	 */
	@GetMapping("/done/{hash}")
	public boolean done(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.isDone();
	}

	/**
	 * 
	 * Whether the game is finished.
	 * 
	 * The difference between this "finished" endpoint and the "done" endpoint
	 * is that the done endpoint is called from the interface to know whether a game is done,
	 * (so this is usefull only once at the end of an active game)
	 * while the finished endpoint retains the state of a game, 
	 * also when it is not active anymore.
	 * 
	 * (The "finished" endpoint is also called when a finished game is opened from the overview jsp.)
	 * 
	 * @param hash the player hash
	 * @return whether the game is finished
	 */
	@GetMapping("/finished/{hash}")
	public boolean finished(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.isFinished();
	}

	/**
	 * 
	 * The robotHash, used in the editor.
	 * 
	 * Is used by the editor to send the game to the engine.
	 * 
	 * @param hash unused
	 * @return the robotHash of the currently active game
	 */
	@GetMapping("/robotHash/{hash}")
	public String robotHash(@PathVariable("hash") String hash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMaster(ColorController.currentGame, robotPlay);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		String result = gameMaster.getRobotHash();

		return result;
	}

	/**
	 * 
	 * The calculateMovez call, used for legacy reasons and will not be present in
	 * the online version.
	 * 
	 * Is used by the editor to request calculating the moves.
	 * 
	 * @param robotHash the robotHash of the currently active game (see robotHash)
	 * @return always returns "ok"
	 */
	@RequestMapping(path = "/calculateMovez/{robotHash}")
	public String calculateMovez(@PathVariable("robotHash") String robotHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		gameMaster.calculateMovez();

		System.out.println("Movez calculated :D");

		return "ok";
	}

	/**
	 * 
	 * Blocks until a move is made in robot play to update the user interface.
	 * 
	 * @param hash the user hash
	 * @return always "ok" (but only after a (robot) move is played)
	 */
	@GetMapping("/isMoveMade/{hash}")
	public String isMoveMade(@PathVariable("hash") String hash) {

		if (!ColorController.robotPlay) {

			CountDownLatch eternalLatch = new CountDownLatch(1);

			try {
				eternalLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// user.getGame();

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(hash);

		try {
			gameMaster.getNextMoveReadyLatch().await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "ok";
	}

	/**
	 * 
	 * POSTs the name of the bot, for interface reasons.
	 * 
	 * @param colorHash the colorHash
	 * @param name      the name
	 * @return always "ok"
	 */
	@PostMapping(value = "/getName/{colorHash}", consumes = "text/html; charset=utf-8", produces = "text/html; charset=utf-8")
	public String getName(@PathVariable("colorHash") String colorHash, @RequestBody String name) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		gameMaster.getRegisteredRemotez().get(colorHash).setName(name);

		if (gameMaster.getRegisteredRemotez().get(colorHash).getColor().equals(Color.GREEN)) {

			BookController.setChallengee(name);
		}

		if (gameMaster.getRegisteredRemotez().get(colorHash).getColor().equals(Color.BLUE)) {

			BookController.setChallenger(name);
		}

		System.out.println("gotten name: " + name);

		return "ok";
	}

	/**
	 * The current count of the moves (used in the jsp).
	 * 
	 * @param colorHash the colorHash
	 * @return the count of the number of moves.
	 */
	@RequestMapping(path = "/numberOfMove/{colorHash}")
	public String numberOfMove(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(colorHash);

		String numberOfMove = gameMaster.getMoveIndex() + "";

		System.out.println("Move number: " + numberOfMove);

		return numberOfMove;
	}

	/**
	 * 
	 * What the judge adjucates. (This is always "STOP" as bots are not required
	 * (yet) to be able to continue game once one mate is reached.)
	 * 
	 * @param colorHash the colorHash
	 * @return the adjucation
	 */
	@RequestMapping(path = "/adjucate/{colorHash}")
	public String adjucate(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(colorHash);

		String adjucation = gameMaster.getJudge().getAdjucation();

		System.out.println("adjucation: " + adjucation);

		return adjucation;
	}

	/**
	 * 
	 * A post-endpoint for communicating an exception to the engine.
	 * If an exception has occured in the bot you can post it to this endpoint,
	 * it finishes the game and displays the exception (and stack) to the front-end.<br/>
	 * <br/>
	 * You are not obliged to post exceptions to the engine but 
	 * you are required to make a move in the given time, even if exceptions occur. <br/>
	 * <br/>
	 * This is a helper-method for debugging purposes only.<br/>
	 * <br/>
	 * It will not be present in the online version of the server.
	 * 
	 * @param robotHash the robotHash
	 * @param stack     the stack
	 * @return always "ok"
	 */
	@PostMapping(value = "/exception/{robotHash}", consumes = "text/html; charset=utf-8", produces = "text/html; charset=utf-8")
	public String exception(@PathVariable("robotHash") String robotHash, @RequestBody String stack) {

		GameMaster gameMaster = ColorController.gameMasterMapper.get(robotHash);

		gameMaster.setException(stack);

		System.out.println("exception recieved -> " + robotHash);

		gameMaster.getNextMoveReadyLatch().countDown();
		gameMaster.setNextMoveReadyLatch(new CountDownLatch(1));

		return "ok";
	}

	/**
	 * 
	 * A getter for the exception (see exception).
	 * 
	 * Blocks game, so use with prudence.
	 * 
	 * Handy if you want to have an exception-free bot.
	 * 
	 * The only requirement at the time of writing is that exceptions can occur, but
	 * the bot is required to make it's move (within 3 minutes).
	 * 
	 * @param colorHash the colorHash
	 * @return the exception given to the exception method
	 */
	@RequestMapping(path = "/getException/{colorHash}")
	public String getException(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(colorHash);

		String exception = gameMaster.getException();

		System.out.println("exception: " + exception);

		return exception;
	}

	/**
	 * 
	 * The final result of the game.
	 * 
	 * @param colorHash the colorHash
	 * @return the final result
	 */
	@RequestMapping(path = "/result/{colorHash}")
	public String getResult(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(colorHash);

		String result = gameMaster.getResult();

		System.out.println("result: " + result);

		return result;
	}

	/**
	 * 
	 * A mid-game result.
	 * 
	 * @param colorHash the colorHash
	 * @return the temporary result
	 */
	@RequestMapping(path = "/temporaryResult/{colorHash}")
	public String getTemporaryResult(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(colorHash);

		String temporaryResult = null;

		if (gameMaster.getJudge() != null) {

			temporaryResult = gameMaster.getJudge().getTemporaryResult();
		}

		System.out.println("temporaryResult: " + temporaryResult);

		return temporaryResult;
	}

	/**
	 * 
	 * The final situation once the game ended (is used to pass the winners and
	 * points to the interface).
	 * 
	 * @param colorHash the colorHash
	 * @return the final situation as a String
	 */
	@RequestMapping(path = "/finalSituation/{colorHash}")
	public String getTemporarySituation(@PathVariable("colorHash") String colorHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromPlayerHash(colorHash);

		String temporarySituation = null;

		if (gameMaster.getJudge() != null) {

			temporarySituation = gameMaster.getJudge().getResult();
		}

		System.out.println("temporarySituation: " + temporarySituation);

		return temporarySituation;
	}

	/**
	 * 
	 * Sets the game to done.
	 * 
	 * This is a helper-method for debugging purposes only.
	 * 
	 * It will not be present in the online version of the server.
	 * 
	 * @param colorHash the color hash
	 * @return always "ok"
	 */
	@RequestMapping(path = "/setDone/{colorHash}")
	public String setDone(@PathVariable("colorHash") String colorHash) {

		BoardController.getInstance().getGameMasterFromPlayerHash(colorHash).setDone(true);

		System.out.println("done.");

		return "ok";
	}

}
