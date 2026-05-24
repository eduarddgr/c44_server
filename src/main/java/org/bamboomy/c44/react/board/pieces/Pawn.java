
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

package org.bamboomy.c44.react.board.pieces;

import java.util.Stack;

import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.move.EnPassant;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pawn extends PlaceAttackingPiece {

	@Getter
	private final int xDelta, yDelta;

	private static final boolean DEBUG = false;

	public Pawn(Place place, int color, int xDelta, int yDelta, Player player, String identifier) {

		super(place, color, player, identifier, PieceValue.PAWN);

		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}

	public Pawn(Pawn otherPawn, Player player, Board board) {

		super(board.getPlacez()[otherPawn.getCurrentPlace().getX()][otherPawn.getCurrentPlace().getY()],
				otherPawn.color, player, otherPawn.getIdentifier(), PieceValue.PAWN, otherPawn.removed);

		this.xDelta = otherPawn.xDelta;
		this.yDelta = otherPawn.yDelta;

		if (DEBUG) {

			log.debug("(" + Color.getBySeq(getColor()) + ") (" + getIdentifier() + "): x: "
					+ otherPawn.getCurrentPlace().getX() + ", y: " + otherPawn.getCurrentPlace().getY() + "removed: ("
					+ removed + ")");
		}

		moved = (Stack<Boolean>) moved.clone();
	}

	public Pawn(Place place, int color, int xDelta, int yDelta, Player player, String identifier, boolean moved,
			Board board, String md5, int oldColor) {

		super(place, color, player, identifier, PieceValue.PAWN, moved, board, md5, oldColor);

		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}

	@Override
	public void calculateMovez() {

		super.calculateMovez();

		if (pinned || removed) {

			return;
		}

		// go forward moves

		Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
				+ xDelta][currentPlace.getY() + yDelta];

		if (otherPlace != null) {

			if (otherPlace.getPiece() == null) {

				if ((currentPlace.getX() + xDelta > 0 && currentPlace.getX() + xDelta < 11)
						&& (currentPlace.getY() + yDelta > 0 && currentPlace.getY() + yDelta < 11)) {

					movez.add(new Move(currentPlace, otherPlace, this, "m"));

				} else {

					// Promotion

					/*
					 * attackableMoves.add(new Promotion(currentPlace, otherPlace, this, addMove,
					 * Color.getBySeq(color), player, board));
					 * 
					 */
				}

				if (!moved.peek()) {

					Place secondPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
							+ (xDelta * 2)][currentPlace.getY() + (yDelta * 2)];

					if (secondPlace.getPiece() == null) {

						Move move = new Move(currentPlace, secondPlace, this, "m");

						EnPassant enPassant = new EnPassant(player, this, otherPlace, "ep");

						move.setEnPassant(enPassant);

						movez.add(move);
					}
				}
			}
		}

		// attacking moves

		if (xDelta != 0 && currentPlace.getY() + 1 < 12) {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + xDelta][currentPlace
					.getY() + 1];

			pawnAttack(otherPlace);
		}

		if (xDelta != 0 && currentPlace.getY() - 1 >= 0) {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + xDelta][currentPlace
					.getY() - 1];

			pawnAttack(otherPlace);
		}

		if (yDelta != 0 && currentPlace.getX() + 1 < 12) {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + 1][currentPlace
					.getY() + yDelta];

			pawnAttack(otherPlace);
		}

		if (yDelta != 0 && currentPlace.getX() - 1 >= 0) {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - 1][currentPlace
					.getY() + yDelta];

			pawnAttack(otherPlace);
		}

		inited = true;
	}

	private void pawnAttack(Place otherPlace) {

		if (otherPlace.getPiece() != null
				&& !getPlayer().getAlliance().isInAlliance(Color.getBySeq(otherPlace.getPiece().color))) {

			movez.add(new Move(currentPlace, otherPlace, this, "m"));
		}

		if (otherPlace.getEnPassant() != null && otherPlace.getEnPassant().getSubject().getColor() != color) {

			EnPassant enPassant = otherPlace.getEnPassant();

			enPassant.setFrom(currentPlace);
			enPassant.setTaker(this);

			movez.add(enPassant);
		}

		attack(otherPlace);
	}
}
