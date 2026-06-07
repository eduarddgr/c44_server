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

import java.rmi.Remote;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import org.bamboomy.c44.domain.BoardController;
import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.Md5;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.RemoteBot;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost/", maxAge = 3600)
@RestController
@RequestMapping("/color/")

public class ColorController {

	private String[] humanGameHashes = { "b4738ee9b249caf4e3d32e6a86dfce74", "065d96772ced3404cbc4828aca210e77",
			"17399ad1cc9604779086434e0a5a89b3", "f187b92292f0015dca507c42771a5f40", "651248e5c3e8416078eabca5a0d92924",
			"b53f04b4880cc44e6d4f1137495822fd", "20b7b3d3dc5c2588719301d1c9cc8cf0", "d38c4549758f89faf155787c3e356359",
			"c14d9e40c9ebaaefcdb971cd821fbf15", "ad8ad88211e522b1e24c981b7087ebd9" };

	private String[] botGameHashes = { "b4738ee9b24555f4e3d32e6a86dfce74", "065d96772c555404cbc4828aca210e77",
			"17399ad1555604779086434e0a5a89b3", "f187b92555f0015dca507c42771a5f40", "651248e55558416078eabca5a0d92924",
			"b53f04b4880cc44e6d4f1135555822fd", "20b7b3d3d5552588719301d1c9cc8cf0", "d38c4555558f89faf155787c3e356359",
			"c14d9e40c9ebaaefcdb97555821fbf15", "ad8ad882115552b1e24c981b7087ebd9" };

	private String[] currentGameHashes;

	private HashMap<String, String> gameMapper = new HashMap<String, String>();

	private HashMap<String, String> colorMapper = new HashMap<String, String>();

	static HashMap<String, GameMaster> gameMasterMapper = new HashMap<String, GameMaster>();

	private String firstRobot = "b2e732c3bf017fd522daa46d754de02c";
	private String secondRobot = "8470006a786856cff652c63f56889e83";

	private static final String ONE_BOT = "Red";
	private static final String ALL_BOTS = "Red,Green";

	private String firstBotColors;

	private ColorsTaken red, green, blue, yellow;

	static String currentGame;

	public static final SecureRandom SECURE_RANDOM = new SecureRandom();

	{
		currentGameHashes = humanGameHashes;
		firstBotColors = ONE_BOT;

		red = new ColorsTaken("Red", "Marloes", "b1e39a47ba5f8aca96033978fb516e1f", "Green", "Y", "Y");
		green = new ColorsTaken("Green", "Frans", "bbad6bd689f97ce9e85f7815cdd47fa8", "Red", "N", "N");
		blue = new ColorsTaken("Blue", "Erik", "34d7fce254ffd08561f37b3bc4443796", "Yellow", "Y", "Y");
		yellow = new ColorsTaken("Yellow", "Ann", "ea0ce8ecf5fd1d70e141d02402e75f57", "Blue", "Y", "Y");

		mapGame(currentGameHashes[0]);
		mapColors(firstBotColors);
	}

	private void mapGame(String gameHash) {

		gameMapper.put(firstRobot, gameHash);
		gameMapper.put(secondRobot, gameHash);

		currentGame = gameHash;

		getGameMasterFromGameHash(currentGame);
	}

	private void mapColors(String firstBotColors) {

		colorMapper.put(firstRobot, firstBotColors);
		colorMapper.put(secondRobot, "Blue,Yellow");
	}

	@GetMapping("/askGame/{identifierHash}")
	public synchronized String askGame(@PathVariable("identifierHash") String identifierHash) {

		return gameMapper.get(identifierHash);
	}

	@GetMapping("/askColors/{gameHash}/{identifierHash}")
	public synchronized String askColors(@PathVariable("gameHash") String gameHash,
			@PathVariable("identifierHash") String identifierHash) {

		// we ignore the gameHash in this stub but once life this will determine the
		// colors (and will record) the gameIdentifierHashes...

		return colorMapper.get(identifierHash);
	}

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

}
