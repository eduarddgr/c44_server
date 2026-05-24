
/**
 * Copyright 2020 Sander Theetaert
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.bamboomy.c44.react.board;

import java.util.ArrayList;

import org.bamboomy.c44.react.board.gui.GuiPlace;
import org.bamboomy.c44.react.board.move.EnPassant;
import org.bamboomy.c44.react.board.move.LinePieceLine;
import org.bamboomy.c44.react.board.move.Rocade;
import org.bamboomy.c44.react.board.pieces.Bisshop;
import org.bamboomy.c44.react.board.pieces.Horse;
import org.bamboomy.c44.react.board.pieces.King;
import org.bamboomy.c44.react.board.pieces.Pawn;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.bamboomy.c44.react.board.pieces.Queen;
import org.bamboomy.c44.react.board.pieces.Tower;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Place {

	@Setter
	private Piece piece;

	private int x, y;

	private GameMaster gameMaster;

	private String md5;

	@Setter
	private EnPassant enPassant;

	@Setter
	private ArrayList<LinePieceLine> kingLinez = new ArrayList<>();

	@Setter
	private boolean attackedForKing = false;

	@Setter
	@Getter
	private Rocade rocade;

	public Place(GameMaster gameMaster, int i, int j) {

		this.gameMaster = gameMaster;

		x = i;
		y = j;
	}

	void calculateHash() {

		String time = System.currentTimeMillis() + "002154025";

		time += (Board.SECURE_RANDOM.nextDouble() * 63524187);

		md5 = Md5.md5(time);
	}

	public GuiPlace toGuiPlace(String color, boolean currentPlayer) {

		return new GuiPlace(piece, color, currentPlayer, x, y);
	}

	public GuiPlace toGuiPlace(boolean showMd5) {

		if (showMd5) {

			return new GuiPlace(x, y, md5);
		}

		return new GuiPlace(x, y);
	}

	public GuiPlace toGuiPlace() {

		return new GuiPlace(x, y);
	}

	public void parsePiece(JSONObject jsonObject) {

		if (!jsonObject.isNull("guiPiece")) {

			parseColor(jsonObject.getJSONObject("guiPiece"));
		}
	}

	private void parseColor(JSONObject jsonObject) {

		log.debug(jsonObject.getInt("color") + "");

		int oldColor = jsonObject.getInt("color");

		if (!jsonObject.isNull("oldColor")) {

			oldColor = jsonObject.getInt("oldColor");
		}

		switch (jsonObject.getInt("color")) {

		case 0:

			parsePiece(jsonObject, Color.GREEN, oldColor);

			break;

		case 1:

			parsePiece(jsonObject, Color.BLUE, oldColor);

			break;

		case 2:

			parsePiece(jsonObject, Color.RED, oldColor);

			break;

		case 3:

			parsePiece(jsonObject, Color.YELLOW, oldColor);

			break;

		case 4:

			parsePiece(jsonObject, Color.DEAD, oldColor);

			break;

		default:
			throw new IllegalArgumentException("Unexpected value: identifier");

		}

	}

	private void parsePiece(JSONObject jsonObject, Color color, int oldColor) {

		Player player = null;

		boolean dead = false;

		if (color.getSeq() != 4) {

			player = gameMaster.getPlayerz()[color.getSeq()];

		} else {

			gameMaster.getPlayerz()[oldColor].setDead(true);
			// player = gameMaster.getPlayerz()[oldColor];

			if (!gameMaster.getMatedPlayers().contains(gameMaster.getPlayerz()[oldColor])) {

				gameMaster.getMatedPlayers().add(gameMaster.getPlayerz()[oldColor]);
			}

			dead = true;
		}

		if (jsonObject.getString("identifier").startsWith("p")) {

			Pawn pawn = new Pawn(this, color.getSeq(), color.getXDirection(), color.getYDirection(), player,
					jsonObject.getString("identifier"), jsonObject.getBoolean("moved"), gameMaster.getBoard(),
					jsonObject.getString("md5"), oldColor);

			pawn.setDead(dead);

			setPiece(pawn);

			if (player != null) {

				player.getPiecez().add(pawn);
			}

			log.debug("adding pawn: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}

		if (jsonObject.getString("identifier").startsWith("b")) {

			Bisshop bisshop = new Bisshop(this, color.getSeq(), player, jsonObject.getString("identifier"),
					jsonObject.getBoolean("moved"), gameMaster.getBoard(), jsonObject.getString("md5"), oldColor);

			bisshop.setDead(dead);

			setPiece(bisshop);

			if (player != null) {

				player.getPiecez().add(bisshop);
			}

			log.debug("adding bisshop: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}

		if (jsonObject.getString("identifier").startsWith("t")) {

			Tower tower = new Tower(this, color.getSeq(), player, jsonObject.getString("identifier"),
					jsonObject.getBoolean("moved"), gameMaster.getBoard(), jsonObject.getString("md5"), oldColor);

			tower.setDead(dead);

			setPiece(tower);

			if (player != null) {

				player.getPiecez().add(tower);
			}

			log.debug("adding tower: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}

		if (jsonObject.getString("identifier").startsWith("h")) {

			Horse horse = new Horse(this, color.getSeq(), player, jsonObject.getString("identifier"),
					jsonObject.getBoolean("moved"), gameMaster.getBoard(), jsonObject.getString("md5"), oldColor);

			horse.setDead(dead);

			setPiece(horse);

			if (player != null) {

				player.getPiecez().add(horse);
			}

			log.debug("adding horse: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}

		if (jsonObject.getString("identifier").equalsIgnoreCase("q")) {

			Queen queen = new Queen(this, color.getSeq(), player, jsonObject.getString("identifier"),
					jsonObject.getBoolean("moved"), gameMaster.getBoard(), jsonObject.getString("md5"), oldColor);

			queen.setDead(dead);

			setPiece(queen);

			if (player != null) {

				player.getPiecez().add(queen);
			}

			log.debug("adding queen: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}

		if (jsonObject.getString("identifier").equalsIgnoreCase("k")) {

			King king = new King(this, color.getSeq(), color.getXDirection(), color.getYDirection(), player,
					jsonObject.getString("identifier"), jsonObject.getBoolean("moved"), gameMaster.getBoard(),
					jsonObject.getString("md5"), oldColor);

			if (dead) {

				throw new RuntimeException("The King cannot be dead!!!");
			}

			setPiece(king);

			if (player != null) {

				player.getPiecez().add(king);

				player.setKing(king);
			}

			log.debug("adding king: " + jsonObject.getString("identifier") + " for color: " + color.getName());
		}
	}

	public void reset() {

		enPassant = null;

		kingLinez = new ArrayList<LinePieceLine>();

		rocade = null;

		piece = null;
	}

	public boolean isAttackedForKingByKingline(Color color) {

		for (LinePieceLine kingLine : getKingLinez()) {

			if (!kingLine.getPiece().isRemoved() && !kingLine.getPiece().getPlayer().getAlliance().isInAlliance((color))
					&& (kingLine.getPossibleKingLineList().size() == 0 || kingLine.isAttacking((King) piece))) {

				return true;
			}
		}

		return false;
	}

}
