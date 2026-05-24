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

package org.bamboomy.c44.react.board;

import static org.bamboomy.c44.react.player.Color.BLUE;
import static org.bamboomy.c44.react.player.Color.GREEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.gui.GuiMove;
import org.bamboomy.c44.react.board.gui.GuiPlace;
import org.bamboomy.c44.react.board.move.Attack;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.pieces.King;
import org.bamboomy.c44.react.board.pieces.LinePiece;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.bamboomy.c44.react.board.pieces.PlaceAttackingPiece;
import org.bamboomy.c44.react.player.Alliance;
import org.bamboomy.c44.react.player.Bot;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;
import org.bamboomy.c44.react.player.RemoteBot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameMaster {

	private static final String DEFAULT_REMOTE_OUTPUT = "The Remote Bot\n\nis still...\n\nbeginning\n\nto\n\ncontemplate...";

	private static final String DEFAULT_LOCAL_OUTPUT = "The Local Bot\n\nis still...\n\nbeginning\n\nto\n\ncontemplate...";

	private static final String DEFAULT_OUTPUT = "No bot\n\nis poundering...";

	private static final String MATE_OUTPUT = "Bot is contemplateing\nKamikaze...";

	private static final String FINISH_HIM = "You have:\n_\nLegal move(s)...";

	private static final String GOODBYE = "Goodbye!\n\n(cruel world :(...)";

	private boolean inited;

	@Getter
	private String gameName, hash, chatId, title, status, statusPre = "";

	@Getter
	@Setter
	private Player[] playerz = new Player[4];

	@Getter
	private Board board;

	@Getter
	private Player currentPlayer;

	@Getter
	private Player previousPlayer;

	@Getter
	private int currentPlayerIndex = GREEN.getSeq();

	@Getter
	private CountDownLatch latch;

	@Getter
	private CountDownLatch mateLatch = new CountDownLatch(1);

	private int countDown = 6;

	@Setter
	@Getter
	private String robotOutput = DEFAULT_OUTPUT;

	@Getter
	private String robotHash;

	@Getter
	private boolean initing = false;

	private ArrayList<RemoteBot> remotez = new ArrayList<RemoteBot>();

	@Getter
	private HashMap<String, RemoteBot> registeredRemotez = new HashMap();

	@Getter
	private ArrayList<Player> matedPlayers = new ArrayList<Player>();

	@Getter
	private boolean done = false;

	public GameMaster(String hash) {

		latch = new CountDownLatch(4);

		this.hash = hash;

		board = new Board(this);
	}

	public void init(Iterable<ColorsTaken> userIterable) {

		if (inited) {

			return;
		}

		HashMap<Color, Alliance> allianceMap = new HashMap<>();

		for (ColorsTaken userColor : userIterable) {

			if (allianceMap.get(Color.getByName(userColor.getColor())) == null) {

				Alliance alliance = new Alliance(Color.getByName(userColor.getColor()),
						Color.getByName(userColor.getAllyColor()));

				allianceMap.put(Color.getByName(userColor.getColor()), alliance);
				allianceMap.put(Color.getByName(userColor.getAllyColor()), alliance);
			}
		}

		for (ColorsTaken userColor : userIterable) {

			int seq = Color.getByName(userColor.getColor()).getSeq();

			if (userColor.getIsBot().equalsIgnoreCase("Y")
					&& (userColor.getIsExternalBot() == null || !userColor.getIsExternalBot().equalsIgnoreCase("Y"))) {

				playerz[seq] = new Bot(userColor, this, allianceMap.get(Color.getByName(userColor.getColor())));

			} else if (userColor.getIsBot().equalsIgnoreCase("Y")
					&& (userColor.getIsExternalBot() != null && userColor.getIsExternalBot().equalsIgnoreCase("Y"))) {

				playerz[seq] = new RemoteBot(userColor, this, allianceMap.get(Color.getByName(userColor.getColor())));

				robotHash = getMd5();

				initing = true;

				remotez.add((RemoteBot) playerz[seq]);

			} else {

				playerz[seq] = new Player(userColor, this, allianceMap.get(Color.getByName(userColor.getColor())));
			}
		}

		for (Player player : playerz) {

			player.initPieces(this);
		}

		for (Player player : playerz) {

			player.initKing();
		}

		for (ColorsTaken userColor : userIterable) {

			int seq = Color.getByName(userColor.getColor()).getSeq();

			if (userColor.getIsBot().equalsIgnoreCase("Y") && userColor.getIsExternalBot() != null
					&& !userColor.getIsExternalBot().equalsIgnoreCase("Y")) {

				((Bot) playerz[seq]).init();
			}
		}

		board.recalcuateHashes();

		for (Player player : playerz) {

			player.calculateMovez();
		}

		for (Player player : playerz) {

			player.calculateKingMovez();
		}

		for (Player player : playerz) {

			player.recalculateHashes();

			latch.countDown();
		}

		currentPlayer = playerz[currentPlayerIndex];

		inited = true;
	}

	public boolean isCurrentPlayer(Color userColor) {

		return currentPlayer != null && userColor == currentPlayer.getColor();
	}

	public boolean movePiece(String pieceHash, String to, boolean fromBot, boolean fromRemoteBot) {

		Piece piece = currentPlayer.getPiece(pieceHash);

		if (piece == null) {

			log.debug("piece == null");

			return false;
		}

		Move move = piece.getMove(to);

		if (move == null) {

			log.debug("move == null");

			return false;
		}

		boolean success = move.execute(true);

		log.debug("success " + success);

		currentPlayer.setLastMove(move);

		if (success) {

			currentPlayer.getKing().prepareMovez();

			currentPlayer.calculateKingLinez();

			currentPlayer.getLastMove().getPiece().calculateMovez();

			previousPlayer = currentPlayer;

			currentPlayer = getNextPlayer();
		}

		if (move.getTakenPiece() instanceof King) {

			currentPlayer.die();

			currentPlayer = getNextPlayer();

			robotOutput = GOODBYE;
		}

		if (success) {

			handleNextTurn();
		}

		if (currentPlayer instanceof Bot && !fromRemoteBot) {

			robotOutput = DEFAULT_LOCAL_OUTPUT;

			((Bot) currentPlayer).init();

			((Bot) currentPlayer).executeMove();

		} else {

			if (!robotOutput.contains("Legal move")) {

				robotOutput = DEFAULT_OUTPUT;
			}
		}

		return success;
	}

	private void calculateCurrentPlayerVanillaMovez() {

		currentPlayer.getKing().prepareMovez();

		calculateChecks(currentPlayer.getKing());

		currentPlayer.getKing().calculateMovez();

		currentPlayer.filterCheckMovez(currentPlayer.getKing(), currentPlayer.getKing());
	}

	private void calculateCurrentPlayerMovez() {

		if (!matedPlayers.isEmpty() && !matedPlayers.get(0).getAlliance().isInAlliance(currentPlayer.getColor())) {

			currentPlayer.createFinishingMovez(matedPlayers.get(0));

			if (currentPlayer.cannotMove()) {

				calculateCurrentPlayerVanillaMovez();

			} else {

				robotOutput = FINISH_HIM.replace("_", "" + currentPlayer.getNumberOfMovez());
			}

		} else {

			calculateCurrentPlayerVanillaMovez();
		}
	}

	private void handleNextTurn() {

		calculateCurrentPlayerMovez();

		boolean wasMate = currentPlayer.cannotMove();

		if (!currentPlayer.isCheck() && currentPlayer.getAlliance() != null) {

			Player ally = playerz[currentPlayer.getAlliance().getOtherColor(currentPlayer.getColor()).getSeq()];

			ally.getKing().prepareMovez();

			calculateChecks(ally.getKing());

			ally.getKing().calculateMovez();

			ally.filterCheckMovez(currentPlayer.getKing(), ally.getKing());

			if (ally.cannotMove()) {

				currentPlayer.filterCheckMovez(currentPlayer.getKing(), ally.getKing());
			}
		}

		if (currentPlayer.cannotMove() && !wasMate) {

			calculateCurrentPlayerMovez();
		}
	}

	private void executeRandomMove() {

		if (currentPlayer.getColor().equals(BLUE)) {

			countDown--;
		}

		if (!currentPlayer.getColor().equals(GREEN) && countDown < 0) {

			return;
		}

		currentPlayer.moveRandomPiece();

		getNextPlayer();

		currentPlayer.calculateMovez();

		if (!currentPlayer.getColor().equals(GREEN)) {

			executeRandomMove();
		}
	}

	private Player getNextPlayer() {

		currentPlayerIndex = (currentPlayerIndex + 1) % playerz.length;

		currentPlayer = playerz[currentPlayerIndex];

		while (currentPlayer.isDead()) {

			currentPlayerIndex = (currentPlayerIndex + 1) % playerz.length;

			currentPlayer = playerz[currentPlayerIndex];
		}

		currentPlayer.recalculateHashes();

		board.recalcuateHashes();

		currentPlayer.destroyEnPassant();

		return currentPlayer;
	}

	public boolean isInited() {

		boolean result = inited;

		for (Player player : playerz) {

			result &= player != null && player.isInited();
		}

		return result;
	}

	public String getCurrentColor() {

		if (currentPlayer != null) {

			return currentPlayer.getColor().toString();

		} else {

			return "Gray";
		}
	}

	public ArrayList<GuiMove> getLastMoves() {

		ArrayList<GuiMove> result = new ArrayList<>();

		for (Player player : playerz) {

			if (player.getLastMove() != null) {

				result.add(player.getLastMove().toGuiColorMove(player.getColor()));

			} else {

				result.add(new GuiMove(player.getColor()));
			}
		}

		return result;
	}

	public void rollBack() {

		previousPlayer.rollBack();

		currentPlayer = previousPlayer;

		currentPlayerIndex = (currentPlayerIndex - 1) % playerz.length;
	}

	public synchronized String register(String colors) {

		String[] colorArray = colors.split(",");

		String output = "";

		for (String color : colorArray) {

			String md5 = getMd5();

			RemoteBot remote = remotez.get(registeredRemotez.size());

			remote.setRegistered(true);

			remote.setPlayerHash(md5);

			registeredRemotez.put(md5, remote);

			output += md5 + ",";
		}

		output = output.substring(0, output.length() - 1);

		System.out.println(output);

		return output;
	}

	private String getMd5() {

		String time = System.currentTimeMillis() + "6+time.getBytes(\"UTF-8\");";

		time += (Board.SECURE_RANDOM.nextDouble() * 777);

		return Md5.md5(time);
	}

	public boolean isCurrentRemote(String playerHash) {

		return (currentPlayer instanceof RemoteBot)
				&& ((RemoteBot) currentPlayer).getPlayerHash().equalsIgnoreCase(playerHash);
	}

	public GuiPlace[][] getBoard(String playerHash) {

		String color = "gray";

		if (currentPlayer != null) {

			color = currentPlayer.getColor().getName();
		}

		return board.getGuiArray(color, true);
	}

	public void resetRemoteOutput() {

		robotOutput = DEFAULT_REMOTE_OUTPUT;
	}

	public void setBoard(String boardJson) {

		/*
		 * for (Player player : playerz) {
		 * 
		 * for (Piece piece : player.getPiecez()) {
		 * 
		 * if (piece instanceof LinePiece) {
		 * 
		 * ((LinePiece) piece).cleanKingeLinez(); } } }
		 * 
		 * 
		 */

		for (Player player : playerz) {

			for (Piece piece : player.getPiecez()) {

				if (piece instanceof LinePiece) {

					((LinePiece) piece).cleanKingeLinez();

					((LinePiece) piece).getKingLinez().clear();
				}

				if (piece instanceof PlaceAttackingPiece) {

					for (Attack attack : ((PlaceAttackingPiece) piece).getAttackingPlaces()) {

						attack.destroy();
					}

					((PlaceAttackingPiece) piece).getAttackingPlaces().clear();
				}
			}
		}

		for (

		Place[] row : board.getPlacez()) {

			for (Place place : row) {

				if (place != null) {

					place.reset();
				}
			}
		}

		for (Player player : playerz) {

			for (Piece piece : player.getPiecez()) {

				piece.setRemoved(true);
			}
		}

		for (Player player : playerz) {

			player.getPiecez().clear();
		}

		board.setBoard(boardJson);

		board.recalcuateHashes();

		for (Player player : playerz) {

			player.recalculateHashes();
		}

		board.setDirty(true);

		currentPlayer = null;
	}

	public void calculateMovez() {

		handleNextTurn();
	}

	public void updateMovez(Alliance alliance) {

		Alliance otherAlliance = alliance.getOtherAlliance();

		for (Player player : playerz) {

			if (otherAlliance.isInAlliance(player.getColor())) {

				player.calculateMovez();
			}
		}
	}

	private void calculateChecks(King king) {

		getPlayerz()[king.getPlayer().getAlliance().getOtherAlliance().getOne().getSeq()].calculateMovez();
		getPlayerz()[king.getPlayer().getAlliance().getOtherAlliance().getTwo().getSeq()].calculateMovez();
	}

	public boolean currentPlayerIsMate() {

		if (currentPlayer != null && currentPlayer.cannotMove()) {

			if (matedPlayers.size() == 0) {

				robotOutput = MATE_OUTPUT;

			} else {

				done = true;

				robotOutput = GOODBYE;
			}

			return true;
		}

		return false;
	}

	public void setTurn(String turn) {

		mateLatch = new CountDownLatch(1);

		board.setDirty(false);

		currentPlayerIndex = Integer.parseInt(turn);

		currentPlayer = playerz[currentPlayerIndex];

		handleNextTurn();

		mateLatch.countDown();

		System.out.println("Turn set to: " + turn);
	}

	public void kamikaze() {

		currentPlayer.getKing().prepareMovez();

		currentPlayer.getKing().forceMovez();

		currentPlayer.calculateMovez();

		currentPlayer.setKamikaze(true);

		if (!matedPlayers.contains(currentPlayer)) {

			matedPlayers.add(currentPlayer);
		}
	}

}
