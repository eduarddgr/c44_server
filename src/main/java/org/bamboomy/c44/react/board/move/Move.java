
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

package org.bamboomy.c44.react.board.move;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.gui.GuiMove;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.bamboomy.c44.react.player.Color;

@Data
@Slf4j
public class Move {

	protected Place from, to;

	protected Piece piece;

	private EnPassant enPassant;

	private Piece takenPiece;

	private static final boolean DEBUG = false;

	protected String identifier = "m";

	public Move(Place from, Place to, Piece piece, String identifier) {

		this.from = from;
		this.to = to;

		this.identifier = identifier;

		if (to == null) {

			throw new RuntimeException("to is null");
		}

		synchronized (this) {

			if (DEBUG) {

				if (from != null) {

					log.debug("f: (" + from.getX() + ", " + from.getY() + ") -> " + "(" + to.getX() + ", " + to.getY()
							+ ")");

				} else {

					log.debug("f: () -> " + "(" + to.getX() + ", " + to.getY() + ")");
				}
			}
		}

		this.piece = piece;
	}

	public boolean execute(boolean fromGameMaster) {

		if (enPassant != null) {

			enPassant.attach();

			piece.getPlayer().setEnPassant(enPassant);
		}

		if (to.getPiece() != null) {

			takenPiece = to.getPiece();

			takenPiece.setRemoved(true);
		}

		return piece.moveTo(to, fromGameMaster, false);
	}

	public GuiMove toGuiColorMove(Color color) {

		return new GuiMove(from.toGuiPlace(), to.toGuiPlace(), color, identifier);
	}

	protected void resetPiece() {

		piece.moveTo(from, false, false);

		piece.rollBackMoved();
	}

	public void rollback() {

		resetPiece();

		if (takenPiece != null) {

			to.setPiece(takenPiece);

			takenPiece.setRemoved(false);
		}

		if (enPassant != null) {

			EnPassant result = enPassant.release();

			enPassant = result;

			piece.getPlayer().setEnPassant(result);
		}
	}
}
