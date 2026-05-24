
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

import java.security.SecureRandom;

import org.bamboomy.c44.react.board.gui.GuiPlace;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.json.JSONArray;

import lombok.Getter;
import lombok.Setter;

public class Board {

	public static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Getter
	private Place[][] placez = new Place[12][12];

	@Getter
	private final GameMaster gameMaster;

	@Setter
	@Getter
	private boolean dirty = false;

	public Board(GameMaster gameMaster) {

		this.gameMaster = gameMaster;

		for (int i = 0; i < 12; i++) {

			placez[i] = new Place[12];

			if (i < 2 || i > 9) {

				for (int j = 2; j < 10; j++) {

					placez[i][j] = new Place(gameMaster, i, j);
				}

			} else {

				for (int j = 0; j < 12; j++) {

					placez[i][j] = new Place(gameMaster, i, j);
				}
			}
		}

		recalcuateHashes();
	}

	void recalcuateHashes() {

		for (Place[] row : placez) {

			for (Place place : row) {

				if (place != null) {

					place.calculateHash();
				}
			}
		}
	}

	public boolean movePiece(Piece piece, String to) {

		boolean found = false;

		Place placeTo = null;

		for (Place[] row : placez) {
			for (Place place : row) {
				if (place != null && place.getMd5().equalsIgnoreCase(to)) {
					placeTo = place;
					found = true;
				}
			}
		}

		if (placeTo != null) {

			piece.moveTo(placeTo, true, false);
		}

		return found;
	}

	public GuiPlace[][] getGuiArray(String color, boolean currentPlayer) {

		GuiPlace[][] result = new GuiPlace[12][12];

		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {

				if (placez[i][j] != null) {

					result[i][j] = placez[i][j].toGuiPlace(color, currentPlayer);
				}
			}
		}

		return result;
	}

	public void setBoard(String boardJson) {

		System.out.println(boardJson);

		JSONArray columns = new JSONArray(boardJson);

		for (int i = 0; i < columns.length(); i++) {

			for (int j = 0; j < columns.length(); j++) {

				if (placez[i][j] != null) {

					placez[i][j].setPiece(null);
				}
			}
		}

		for (int i = 0; i < columns.length(); i++) {

			JSONArray row = columns.getJSONArray(i);

			for (int j = 0; j < row.length(); j++) {

				if (!row.isNull(j) && placez[i][j] != null) {

					placez[i][j].parsePiece(row.getJSONObject(j));
				}
			}
		}
	}
}
