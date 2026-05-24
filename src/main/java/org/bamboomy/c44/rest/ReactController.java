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

import org.bamboomy.c44.domain.BoardController;
import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.gui.GuiMove;
import org.bamboomy.c44.react.board.gui.GuiPlace;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost/", maxAge = 3600)
@RestController
@RequestMapping("/react/")

/**
 * The REST(full) interface with the rest of the server.
 * 
 * The different end-points are documented below.
 * 
 * 
 */
public class ReactController {

	private ColorsTaken red, green, blue, yellow;

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

		red = new ColorsTaken("Red", "Marloes", "b1e39a47ba5f8aca96033978fb516e1f", "Green", "Y", "Y");
		green = new ColorsTaken("Green", "Frans", "bbad6bd689f97ce9e85f7815cdd47fa8", "Red", "N", "N");
		blue = new ColorsTaken("Blue", "Erik", "34d7fce254ffd08561f37b3bc4443796", "Yellow", "Y", "Y");
		yellow = new ColorsTaken("Yellow", "Ann", "ea0ce8ecf5fd1d70e141d02402e75f57", "Blue", "Y", "Y");
	}
	
	/**
	 * 
	 * Takes a user-hash and redirects to the board-method.
	 * 
	 * The user-hash for this server is hard-coded,
	 * but will be replaced with hashes generated upon creation of the game
	 * and are taken from the database online.
	 * 
	 * @param hash only bbad6bd689f97ce9e85f7815cdd47fa8 works in this version.
	 * @return
	 */
	@GetMapping("/getGame/{hash}")
	public synchronized RedirectView hello(@PathVariable("hash") String hash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return new RedirectView("/negative"); }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return new RedirectView("/react/getBoard/" + gameHash + "/" + hash);
	}

	/**
	 * 
	 * Takes a gameHash and userHash and returns the board rotated for that player as a JSON.
	 * 
	 * The JSON is not documented but should be human-readeable.
	 * 
	 * @param gameHash The gameHash, hardcoded to 8ac4d9c3d324225fdbeedf99dc6a44a6 in this version.
	 * @param userHash The userHash, the different userHashes can be found in the source-code.
	 * @return
	 */
	@RequestMapping(path = "/getBoard/{gameHash}/{userHash}", produces = "application/json")
	public GuiPlace[][] board(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		Color color = getColor(userHash);

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		GuiPlace[][] arr = gameMaster.getBoard().getGuiArray(color.getName(), gameMaster.isCurrentPlayer(color));

		return arr;
	}

	/**
	 * 
	 * Returns the gameHash from the userHash, always returns 8ac4d9c3d324225fdbeedf99dc6a44a6 in this version.
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/getGameHash/{hash}")
	public String gameHash(@PathVariable("hash") String hash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		// String gameHash = ;// user.getGame();

		return "8ac4d9c3d324225fdbeedf99dc6a44a6";
	}

	/**
	 * 
	 * Redirects to the currentPlayer method.
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/getIsCurrentPlayer/{hash}")
	public RedirectView amI(@PathVariable("hash") String hash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return new RedirectView("/react/isCurrentPlayer/" + gameHash + "/" + hash);
	}

	/**
	 * 
	 * Returns the color as int for the userHash given, Green is 0, Blue 1, Red 2 and Yellow 3.
	 * (See also the Color enum in the source code for the other values).
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/getMyColor/{hash}")
	public int color(@PathVariable("hash") String hash) {

		if (hash.equalsIgnoreCase("b1e39a47ba5f8aca96033978fb516e1f")) {

			return 2;

		} else if (hash.equalsIgnoreCase("34d7fce254ffd08561f37b3bc4443796")) {

			return 1;

		} else if (hash.equalsIgnoreCase("ea0ce8ecf5fd1d70e141d02402e75f57")) {

			return 3;
		}

		return 0;// getByName(colorsTakenRepository.findByHash(hash).getColor()).getSeq();
	}

	/**
	 * 
	 * Returns whether the given userHash for the given gameHash is currently having its turn (as a boolean).  
	 * 
	 * @param gameHash
	 * @param userHash
	 * @return
	 */
	@RequestMapping(path = "/isCurrentPlayer/{gameHash}/{userHash}")
	public String currentPlayer(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		Color color = getColor(userHash);

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
	 * Performs a move (if the userHash is the current player for the given gameHash).
	 * 
	 * @param gameHash The game
	 * @param userHash The user
	 * @param pieceHash The piece the user wants to move (to be found in the JSON returned by the board method).
	 * @param square The destination square the piece wants to move to (for Casteling you need to move the king two places). (Also given in the same JSON.)
	 * @return
	 */

	@RequestMapping(path = "/play/{gameHash}/{userHash}/{pieceHash}/{square}", produces = "application/json")
	public ArrayList<ArrayList<GuiPlace>> play(@PathVariable("gameHash") String gameHash,
			@PathVariable("userHash") String userHash, @PathVariable("pieceHash") String pieceHash,
			@PathVariable("square") String square) {

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		Color color = getColor(userHash);

		// ColorsTaken user = colorsTakenRepository.findByHash(userHash);

		ArrayList<ArrayList<GuiPlace>> arr = new ArrayList<>();

		gameMaster.movePiece(pieceHash, square, false, false);

		gameMaster.getBoard().getGuiArray(color.getName(), gameMaster.isCurrentPlayer(color));

		return arr;
	}

	/**
	 * 
	 * Indicates that the mater wants to continue play (and the matee should contemplate kamikaze).
	 * 
	 * (A mated player is allowed to play one last "kamikaze"-move (it balances the game) once mate.)
	 * 
	 * The point gained by mating this player is at peril until the other player in the allicance is mated a well.
	 * 
	 * If you (or your ally) is mated; and you (or your ally) can still mate the last foe, your allicance gains 1.47 points. 
	 * 
	 * Mating the second enemy gains 1.3 points in total if you or your ally is not mated. 
	 * 
	 * @param gameHash
	 * @param userHash
	 */
	@RequestMapping(path = "/kamikaze/{gameHash}/{userHash}")
	public void kamikaze(@PathVariable("gameHash") String gameHash, @PathVariable("userHash") String userHash) {

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		Color color = getColor(userHash);

		// ColorsTaken user = colorsTakenRepository.findByHash(userHash);

		//TODO: The server should decide kamikaze, this shouldn't be done by the user interface :( :( :(
		
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
	 * @param gameHash
	 * @param turn
	 */
	@RequestMapping(path = "/postTurn/{gameHash}/{turn}")
	public void postTurn(@PathVariable("gameHash") String gameHash, @PathVariable("turn") String turn) {

		getGameMasterFromGameHash(gameHash).setTurn(turn);
	}

	/**
	 * 
	 * The describing String of how a player is doing.
	 * 
	 * Gets a human readable description of the state a user is in (check for instance).
	 * 
	 * Used by the user interface.
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/getCurrentPlayerString/{hash}")
	public String currentPlayer(@PathVariable("hash") String hash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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
	 * @return
	 */
	@GetMapping("/getCurrentColor/{hash}")
	public String currentColor(@PathVariable("hash") String hash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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
	 * @param userHash
	 * @return
	 */
	@RequestMapping(path = "/lastMove/{hash}", produces = "application/json")
	public ArrayList<GuiMove> lastMove(@PathVariable("hash") String userHash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(userHash);
		 * 
		 * if (user == null) {
		 * 
		 * return null; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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
	 * Whether the current player is check (if the given hash is the current player).
	 * 
	 * @param userHash
	 * @return
	 */
	@RequestMapping(path = "/isCheck/{hash}", produces = "application/json")
	public String isCheck(@PathVariable("hash") String userHash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(userHash);
		 * 
		 * if (user == null) {
		 * 
		 * return null; }
		 */

		Color color = getColor(userHash);

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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

	/**
	 * 
	 * Returns the color from the userhash, see source code for hard-coded values.
	 * 
	 * @param userHash
	 * @return
	 */
	private Color getColor(String userHash) {

		switch (userHash) {
		case "b1e39a47ba5f8aca96033978fb516e1f": {

			return Color.RED;
		}
		case "bbad6bd689f97ce9e85f7815cdd47fa8": {

			return Color.GREEN;
		}
		case "34d7fce254ffd08561f37b3bc4443796": {

			return Color.BLUE;
		}
		case "ea0ce8ecf5fd1d70e141d02402e75f57": {

			return Color.YELLOW;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + userHash);
		}
	}

	private synchronized GameMaster getGameMasterFromGameHash(String gameHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMaster(gameHash);

		if (!gameMaster.isInited()) {

			/*
			 * Game game = gameRepository.findByHash(gameHash);
			 * 
			 * game.setStarted("Y");
			 * 
			 * gameRepository.save(game);
			 */

			ArrayList<ColorsTaken> myArrayList = new ArrayList<>();

			myArrayList.add(green);
			myArrayList.add(blue);
			myArrayList.add(red);
			myArrayList.add(yellow);

			gameMaster.init(myArrayList);

			if (gameMaster.getRobotHash() != null) {

				BoardController.getInstance().putGameMaster(gameHash, gameMaster.getRobotHash());
			}
		}

		return gameMaster;
	}

	/**
	 * 
	 * Rolls back the last move (will also be removed in the online version).
	 * 
	 * @param gameHash
	 */
	@RequestMapping(path = "/rollBack/{gameHash}")
	public void rollBack(@PathVariable("gameHash") String gameHash) {

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		gameMaster.rollBack();
	}

	/**
	 *
	 * Returns what the current robot is "thinking".
	 * 
	 * After an initial message the input send to the updateOutput method is used.
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/robotOutput/{hash}")
	public String robotOutput(@PathVariable("hash") String hash) {

		/*
		 * 
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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
	 * When a board situation is posted by the postBoard method this value is used to set the color by the user interface.
	 * 
	 * Will also be removed online.
	 * 
	 * @param hash
	 * @return
	 */
	@GetMapping("/dirty/{hash}")
	public boolean dirty(@PathVariable("hash") String hash) {

		/*
		 * 
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

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
	 * Gets the initing robot hashes available (for the time being only one game is initing at the same time).
	 * 
	 * @return
	 */
	@RequestMapping(path = "/getInitingRobotHashes", produces = "application/json")
	public String[] robotHashes() {

		System.out.println("sending initinRobotHashes to remote bot...");

		return BoardController.getInstance().getInitinRobotHashes();
	}

	/**
	 * 
	 * Registers the color(s) the remote bot wants to play in this game.
	 * 
	 * @param robotHash One of the initing robot hashes of robotHashes
	 * @param colors The colors, comma separated for examples: see the application.properties in the bots.
	 * @return The hashes of the colors, in the same order as the colors are given.
	 */
	@RequestMapping(path = "/register/{robotHash}/{colors}", produces = "application/json")
	public String register(@PathVariable("robotHash") String robotHash, @PathVariable("colors") String colors) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		System.out.println("recieved register request...");

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.register(colors);
	}

	/**
	 * 
	 * Returns the robot board when the bot should currently pounder upon it's move.
	 * 
	 * Sends null otherwise.
	 * 
	 * (You can depend on this method to start letting your bot calculate).
	 * 
	 * @param robotHash
	 * @param playerHash
	 * @return
	 */
	@RequestMapping(path = "/getRobotBoard/{robotHash}/{playerHash}", produces = "application/json")
	public GuiPlace[][] robotBoard(@PathVariable("robotHash") String robotHash,
			@PathVariable("playerHash") String playerHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		if (!gameMaster.isInited()) {

			System.out.println("somehow the gameMaster isn't inited (yet???)!!!");

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (gameMaster.isCurrentRemote(playerHash) && gameMaster.getCurrentPlayer() != null
				&& !(gameMaster.getCurrentPlayer().cannotMove() && !gameMaster.getCurrentPlayer().isKamikaze())) {

			System.out.println("sending board to remote bot...");

			gameMaster.resetRemoteOutput();

			return gameMaster.getBoard(playerHash);

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
	 * @param robotHash
	 * @param pieceHash
	 * @param placeHash
	 */
	@RequestMapping(path = "/playBot/{robotHash}/{pieceHash}/{placeHash}", produces = "application/json")
	public void playBot(@PathVariable("robotHash") String robotHash, @PathVariable("pieceHash") String pieceHash,
			@PathVariable("placeHash") String placeHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		gameMaster.movePiece(pieceHash, placeHash, false, true);

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
	 * A method used when play is begun:
	 * 
	 * returns the color of the bot that is going to play (if a remote bot plays multiple colors).
	 * 
	 * @param robotHash
	 * @param playerHash
	 * @return
	 */
	@RequestMapping(path = "/getColor/{robotHash}/{playerHash}")
	public String getColor(@PathVariable("robotHash") String robotHash, @PathVariable("playerHash") String playerHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		String name = gameMaster.getRegisteredRemotez().get(playerHash).getColor().getName();

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
	 * @param robotHash
	 * @param playerHash
	 * @return
	 */
	@RequestMapping(path = "/getAllianceColor/{robotHash}/{playerHash}")
	public String getAlliaceColor(@PathVariable("robotHash") String robotHash,
			@PathVariable("playerHash") String playerHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMasterFromRobotHash(robotHash);

		RemoteBot bot = gameMaster.getRegisteredRemotez().get(playerHash);

		System.out
				.println("sending " + bot.getAlliance().getOtherColor(bot.getColor()).getName() + " to remote bot...");

		return bot.getAlliance().getOtherColor(bot.getColor()).getName();
	}

	/**
	 * 
	 * Sends the (debug) output of the bot to the server.
	 * 
	 * The output is shown in the interface (by the robotoutput method).
	 * 
	 * @param robotHash
	 * @param output
	 * @return
	 */
	@PostMapping(value = "/updateOutput/{robotHash}", consumes = "text/html; charset=utf-8", produces = "text/html; charset=utf-8")
	public String updateOutput(@PathVariable("robotHash") String robotHash, @RequestBody String output) {

		BoardController.getInstance().getGameMasterFromRobotHash(robotHash)
				.setRobotOutput("Remote output:\n\n" + output);

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
	 * @param robotHash
	 * @param boardJson
	 * @return
	 */
	@PostMapping(value = "/postBoard/{robotHash}", consumes = "application/json", produces = "text/html; charset=utf-8")
	public String postBoard(@PathVariable("robotHash") String robotHash, @RequestBody String boardJson) {

		BoardController.getInstance().getGameMasterFromRobotHash(robotHash).setBoard(boardJson);

		System.out.println("postBoard recieved -> " + robotHash);

		return "ok";
	}

	/**
	 * 
	 * Whether the current playver is mate.
	 * 
	 * @param userHash
	 * @return
	 */
	@RequestMapping(path = "/mate/{hash}", produces = "application/json")
	public boolean isMate(@PathVariable("hash") String userHash) {

		/*
		 * ColorsTaken user = colorsTakenRepository.findByHash(userHash);
		 * 
		 * if (user == null) {
		 * 
		 * return null; }
		 */

		Color color = getColor(userHash);

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		try {
			gameMaster.getMateLatch().await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

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
	 * @param hash
	 * @return
	 */
	@GetMapping("/done/{hash}")
	public boolean done(@PathVariable("hash") String hash) {

		/*
		 * 
		 * ColorsTaken user = colorsTakenRepository.findByHash(hash);
		 * 
		 * if (user == null) {
		 * 
		 * return "/negative"; }
		 */

		String gameHash = "8ac4d9c3d324225fdbeedf99dc6a44a6";// user.getGame();

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		if (!gameMaster.isInited()) {

			try {
				gameMaster.getLatch().await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return gameMaster.isDone();
	}

}
