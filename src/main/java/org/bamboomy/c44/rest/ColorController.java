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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import org.bamboomy.c44.domain.BoardController;
import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.Md5;
import org.bamboomy.c44.react.jsp.BookController;
import org.bamboomy.c44.react.jsp.Game;
import org.bamboomy.c44.react.jsp.GameController;
import org.bamboomy.c44.react.jsp.MetaJudge;
import org.bamboomy.c44.react.jsp.PortalController;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.RemoteBot;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class handles the negotiation for the color.
 *
 */
@CrossOrigin(origins = "http://localhost/", maxAge = 3600)
@RestController
@RequestMapping("/color/")
public class ColorController {

	private String[] humanGameHashes = { "b4738ee9b249caf4e3d32e6a86dfce74", "065d96772ced3404cbc4828aca210e77",
			"17399ad1cc9604779086434e0a5a89b3", "f187b92292f0015dca507c42771a5f40", "651248e5c3e8416078eabca5a0d92924",
			"b53f04b4880cc44e6d4f1137495822fd", "20b7b3d3dc5c2588719301d1c9cc8cf0", "d38c4549758f89faf155787c3e356359",
			"c14d9e40c9ebaaefcdb971cd821fbf15", "ad8ad88211e522b1e24c981b7087ebd9" };

	private static String[] botGameHashes = { "b4738ee9b24555f4e3d32e6a86dfce74", "065d96772c555404cbc4828aca210e77",
			"17399ad1555604779086434e0a5a89b3", "f187b92555f0015dca507c42771a5f40", "651248e55558416078eabca5a0d92924",
			"b53f04b4880cc44e6d4f1135555822fd", "20b7b3d3d5552588719301d1c9cc8cf0", "d38c4555558f89faf155787c3e356359",
			"c14d9e40c9ebaaefcdb97555821fbf15", "ad8ad882115552b1e24c981b7087ebd9" };

	private static String[] currentGameHashes;

	private static HashMap<String, String> gameMapper = new HashMap<String, String>();

	private static HashMap<String, String> colorMapper = new HashMap<String, String>();

	static HashMap<String, GameMaster> gameMasterMapper = new HashMap<String, GameMaster>();

	/*
	private static String firstRobot = "b2e732c3bf017fd522daa46d754de02c";
	private static String secondRobot = "8470006a786856cff652c63f56889e83";
	*/
	private static String firstRobot = "8470006a786856cff652c63f56889e83";
	private static String secondRobot = "b2e732c3bf017fd522daa46d754de02c";

	private static String firstBotColors;

	static String currentGame;

	private static String nextGame;

	private static int currentIndex = 0;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	/**
	 * Public because it is changed by other classes (you won't need this)
	 */
	public static String ALLIANCE;

	/**
	 * Public because it is changed by other classes (you won't need this)
	 */
	public static String BOT_AND_HUMAN = "Red";

	/**
	 * Public because it is changed by other classes (you won't need this)
	 */
	public static boolean robotPlay = false;

	/**
	 * Public because it is changed by other classes (you won't need this)
	 */
	public static HashMap<String, Game> gameHashMap = new HashMap<String, Game>();

	/**
	 * Public because it is changed by other classes (you won't need this)
	 */
	public static MetaJudge metaJudge = new MetaJudge(botGameHashes);

	{

		BookController.metaJudge = metaJudge;

		if (ALLIANCE.equalsIgnoreCase(BOT_AND_HUMAN)) {

			for (String md5 : botGameHashes) {

				String string = System.currentTimeMillis() + "Some salted message :)";

				string += (SECURE_RANDOM.nextDouble() * 115523);
				String greenHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String redHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String blueHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String yellowHash = Md5.md5(string);

				ColorsTaken red, green, blue, yellow;

				green = new ColorsTaken("Green", "Frans", greenHash, "Red", "N", "N");
				red = new ColorsTaken("Red", "Marloes", redHash, "Green", "Y", "Y");
				blue = new ColorsTaken("Blue", "Erik", blueHash, "Yellow", "Y", "Y");
				yellow = new ColorsTaken("Yellow", "Ann", yellowHash, "Blue", "Y", "Y");

				Game game = new Game(humanGameHashes[0], "Naamloos.jpg", "Not begun (yet)", "undecided", 0, green, red,
						blue, yellow);

				GameController.getInstance().putGameByPlayerHash(greenHash, game);
				GameController.getInstance().putGameByPlayerHash(redHash, game);
				GameController.getInstance().putGameByPlayerHash(blueHash, game);
				GameController.getInstance().putGameByPlayerHash(yellowHash, game);

				gameHashMap.put(humanGameHashes[0], game);

				PortalController.game = game;
			}

		} else {

			for (String md5 : botGameHashes) {

				String string = System.currentTimeMillis() + "Some salted message :)";

				string += (SECURE_RANDOM.nextDouble() * 115523);
				String greenHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String redHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String blueHash = Md5.md5(string);
				string += (SECURE_RANDOM.nextDouble() * 115523);
				String yellowHash = Md5.md5(string);

				ColorsTaken red, green, blue, yellow;

				green = new ColorsTaken("Green", "Frans", greenHash, "Red", "Y", "Y");
				red = new ColorsTaken("Red", "Marloes", redHash, "Green", "Y", "Y");
				blue = new ColorsTaken("Blue", "Erik", blueHash, "Yellow", "Y", "Y");
				yellow = new ColorsTaken("Yellow", "Ann", yellowHash, "Blue", "Y", "Y");

				Game game = new Game(md5, "Naamloos.jpg", "Not begun (yet)", "undecided", 0, green, red, blue, yellow);

				BookController.books.add(game);

				GameController.getInstance().putGameByPlayerHash(greenHash, game);
				GameController.getInstance().putGameByPlayerHash(redHash, game);
				GameController.getInstance().putGameByPlayerHash(blueHash, game);
				GameController.getInstance().putGameByPlayerHash(yellowHash, game);

				gameHashMap.put(md5, game);

				metaJudge.add(md5, game);
			}
		}

		if (ALLIANCE.equalsIgnoreCase(BOT_AND_HUMAN)) {

			currentGameHashes = humanGameHashes;

		} else {

			currentGameHashes = botGameHashes;
			robotPlay = true;

			ReactController.setRobotPlay(robotPlay);
		}

		firstBotColors = ALLIANCE;

		mapGame(currentGameHashes[0]);
		mapColors(firstBotColors);

		nextGame = currentGameHashes[1];
	}

	/**
	 * Only Spring uses this.
	 */
	public ColorController() {
	}

	private static void mapGame(String gameHash) {

		gameMapper.put(firstRobot, gameHash);
		gameMapper.put(secondRobot, gameHash);

		currentGame = gameHash;

		getGameMasterFromGameHash(currentGame);
	}

	private static void mapColors(String firstBotColors) {

		if (ALLIANCE.equalsIgnoreCase(BOT_AND_HUMAN)) {

			colorMapper.put(secondRobot, "Blue,Yellow," + firstBotColors);

		} else {

			colorMapper.put(firstRobot, firstBotColors);
			colorMapper.put(secondRobot, "Blue,Yellow");
		}

	}

	/**
	 * Asks which games are available for the given "identifierHash".
	 * 
	 * @param identifierHash the (unique) identifierHash which identifies the robot.
	 * @return A valid game hash.
	 */
	@GetMapping("/askGame/{identifierHash}")
	public synchronized String askGame(@PathVariable("identifierHash") String identifierHash) {

		return gameMapper.get(identifierHash);
	}

	/**
	 * Requests the colors for the given game and identifierHash.
	 * 
	 * @param gameHash       the gameHash
	 * @param identifierHash the identifierHash
	 * @return the colors
	 */
	@GetMapping("/askColors/{gameHash}/{identifierHash}")
	public synchronized String askColors(@PathVariable("gameHash") String gameHash,
			@PathVariable("identifierHash") String identifierHash) {

		// we ignore the gameHash in this stub but once life this will determine the
		// colors (and will record) the gameIdentifierHashes...

		return colorMapper.get(identifierHash);
	}

	/**
	 * Returns (and generates) the player hash for given game, color and identifier.
	 * 
	 * @param gameHash       the gameHash
	 * @param color          the color
	 * @param identifierHash the identifierHash
	 * @return the player hash
	 */
	@GetMapping("/askHash/{gameHash}/{color}/{identifierHash}")
	public synchronized String askHash(@PathVariable("gameHash") String gameHash, @PathVariable("color") String color,
			@PathVariable("identifierHash") String identifierHash) {

		String string = System.currentTimeMillis() + "Some salted message :)";

		string += (SECURE_RANDOM.nextDouble() * 117223) + identifierHash + gameHash + color;

		String md5 = Md5.md5(string);

		GameMaster gameMaster = getGameMasterFromGameHash(gameHash);

		RemoteBot remote = (RemoteBot) gameMaster.getPlayerWithColor(Color.getByName(color));

		remote.setRegistered(true);

		remote.setPlayerHash(md5);

		gameMaster.getRegisteredRemotez().put(md5, remote);

		gameMasterMapper.put(md5, gameMaster);

		return md5;
	}

	private static synchronized GameMaster getGameMasterFromGameHash(String gameHash) {

		GameMaster gameMaster = BoardController.getInstance().getGameMaster(gameHash, robotPlay);

		if (!gameMaster.isInited()) {

			gameMaster.setMetaJudge(metaJudge);

			ColorsTaken green = null, red = null, blue = null, yellow = null;

			if (gameHashMap.get(gameHash) != null) {

				Game game = gameHashMap.get(gameHash);

				green = game.getGreen();
				red = game.getRed();
				blue = game.getBlue();
				yellow = game.getYellow();

				BoardController.getInstance().putGameMasterByPlayerHash(green.getJavaHash(), gameMaster);
				BoardController.getInstance().putGameMasterByPlayerHash(red.getJavaHash(), gameMaster);
				BoardController.getInstance().putGameMasterByPlayerHash(blue.getJavaHash(), gameMaster);
				BoardController.getInstance().putGameMasterByPlayerHash(yellow.getJavaHash(), gameMaster);
			}

			ArrayList<ColorsTaken> myArrayList = new ArrayList<>();

			myArrayList.add(green);
			myArrayList.add(blue);
			myArrayList.add(red);
			myArrayList.add(yellow);

			gameMaster.init(myArrayList);

			if (robotPlay) {

				gameMaster.setRobotPlay(true);
			}

			if (gameHashMap.get(gameHash) != null) {

				Game game = gameHashMap.get(gameHash);

				gameMaster.setGame(game);

				game.begin();
			}

			if (gameMaster.getRobotHash() != null) {

				BoardController.getInstance().putGameMaster(gameHash, gameMaster.getRobotHash());
			}
		}

		return gameMaster;
	}

	/**
	 * (Deprecated) call to go to the next game.
	 * 
	 * Retained for legacy reasons,
	 * 
	 * will not be present and can be ignored.
	 * 
	 * @return a vanilla "ok" message.
	 */
	@GetMapping("/goToNextGame")
	public synchronized static String goToNextGame() {

		currentGame = nextGame;

		currentIndex++;

		nextGame = currentGameHashes[currentIndex + 1];

		mapGame(currentGame);
		mapColors(firstBotColors);

		return "ok :)";
	}

	/**
	 * Asks whether the server is booted in robotplay (or human play).
	 * 
	 * @return A boolean whether the server is booted in robotplay.
	 */
	@GetMapping("/robotPlay")
	public synchronized boolean robotPlay() {

		return robotPlay;
	}
}
