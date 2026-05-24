
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

import java.util.ArrayList;
import java.util.Stack;

import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.move.Attack;
import org.bamboomy.c44.react.board.move.LinePieceLine;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.move.Rocade;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class King extends PlaceAttackingPiece {

	private final int xDelta, yDelta;

	private ArrayList<Place> possibleMoves = new ArrayList<>();

	@Getter
	private ArrayList<Attack> attackedPlaces = new ArrayList<>();

	private ArrayList<Rocade> rocades = new ArrayList<>();

	public King(Place place, int color, int xDelta, int yDelta, Player player, String identifier) {

		super(place, color, player, identifier, PieceValue.KING);

		this.xDelta = xDelta;
		this.yDelta = yDelta;

		log.debug("king created...");
	}

	public King(King otherKing, Player player, Board board) {

		super(board.getPlacez()[otherKing.getCurrentPlace().getX()][otherKing.getCurrentPlace().getY()],
				otherKing.color, player, otherKing.getIdentifier(), PieceValue.KING, otherKing.removed);

		this.xDelta = otherKing.xDelta;
		this.yDelta = otherKing.yDelta;

		log.debug("king copied...");

		moved = (Stack<Boolean>) moved.clone();
	}

	public King(Place place, int color, int xDelta, int yDelta, Player player, String identifier, boolean moved,
			Board board, String md5, int oldColor) {

		super(place, color, player, identifier, PieceValue.KING, moved, board, md5, oldColor);

		this.xDelta = xDelta;
		this.yDelta = yDelta;

		log.debug("king created...");
	}

	public void prepareMovez() {

		possibleMoves = new ArrayList<>();

		for (Rocade rocade : rocades) {

			rocade.destroy();
		}

		rocades = new ArrayList<Rocade>();

		int k = currentPlace.getX() + 1;
		int l = currentPlace.getY() + 1;

		if (k < 12 && l < 12) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX();

		if (k < 12 && l < 12) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX() - 1;

		if (k >= 0 && l < 12) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX() + 1;
		l = currentPlace.getY();

		if (k < 12 && l < 12) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX() - 1;

		if (k >= 0 && l < 12) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX() + 1;
		l = currentPlace.getY() - 1;

		if (k < 12 && l >= 0) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX();

		if (k < 12 && l >= 0) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		k = currentPlace.getX() - 1;

		if (k >= 0 && l >= 0) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[k][l];

			if (otherPlace != null) {

				if (otherPlace.getPiece() == null || otherPlace.getPiece().color != color) {

					possibleMoves.add(otherPlace);
				}
			}
		}

		checkRocadeRight();

		checkRocadeLeft();

		log.debug("there are: {} possible movez", possibleMoves.size());

		inited = true;
	}

	public synchronized boolean isCheck() {

		if (currentPlace.isAttackedForKingByKingline(player.getColor())) {

			return true;
		}

		for (Attack place : attackedPlaces) {

			if (place.getPlace() == currentPlace) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void calculateMovez() {

		movez = new ArrayList<>();

		ArrayList<Rocade> possibleRocades = new ArrayList<Rocade>(rocades);

		ArrayList<Rocade> toRemove = new ArrayList<Rocade>();

		for (Place possiblePlace : possibleMoves) {

			boolean valid = true;

			for (LinePieceLine kingLine : possiblePlace.getKingLinez()) {

				if (!player.getAlliance().isInAlliance((kingLine.getPiece().getPlayer().getColor()))
						&& (kingLine.getPossibleKingLineList().size() == 0) || kingLine.isAttacking(this)) {

					valid = false;
					break;
				}
			}

			for (Rocade rocade : possibleRocades) {

				if (!valid && rocade.contains(possiblePlace)) {

					toRemove.add(rocade);
				}
			}

			for (Attack attack : attackedPlaces) {

				for (Rocade rocade : possibleRocades) {

					if (rocade.contains(attack.getPlace())) {

						toRemove.add(rocade);
					}
				}

				if (possiblePlace == attack.getPlace()) {

					valid = false;
				}
			}

			if (valid && possiblePlace.getRocade() == null) {

				movez.add(new Move(currentPlace, possiblePlace, this, "m"));
			}
		}

		possibleRocades.removeAll(toRemove);

		for (Rocade rocade : possibleRocades) {

			movez.add(rocade);
		}

		inited = true;
	}

	public boolean shouldBeAttacked(Place otherPlace) {

		if (otherPlace == currentPlace) {

			return true;
		}

		for (Rocade rocade : rocades) {

			if (rocade.contains(otherPlace)) {

				return true;
			}
		}

		for (Place possibleMove : possibleMoves) {

			if (otherPlace == possibleMove) {

				return true;
			}
		}

		return false;
	}

	public Attack addAttack(Place otherPlace) {

		Attack attackingPlace = new Attack(this, otherPlace);

		attackedPlaces.add(attackingPlace);

		return attackingPlace;
	}

	private void checkRocadeRight() {

		if (moved.peek()
				|| (!inBounds(currentPlace.getX(), (yDelta * 3)) || !inBounds(currentPlace.getY(), -(xDelta * 3)))
				|| currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + (yDelta * 3)][currentPlace
						.getY() - (xDelta * 3)] == null) {

			return;
		}

		Piece otherPiece = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
				+ (yDelta * 3)][currentPlace.getY() - (xDelta * 3)].getPiece();

		if (!isCheck() && inBounds(currentPlace.getX(), yDelta) && inBounds(currentPlace.getY(), -xDelta)
				&& inBounds(currentPlace.getX(), (yDelta * 2)) && inBounds(currentPlace.getY(), -(xDelta * 2))
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + yDelta][currentPlace.getY()
						- xDelta] != null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + yDelta][currentPlace.getY()
						- xDelta].getPiece() == null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + (yDelta * 2)][currentPlace
						.getY() - (xDelta * 2)] != null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + (yDelta * 2)][currentPlace
						.getY() - (xDelta * 2)].getPiece() == null
				&& otherPiece instanceof Tower && otherPiece.getColor() == color && !otherPiece.moved.peek()) {

			Place rocadePlace = getRocadePlace((Tower) otherPiece, true);

			possibleMoves.add(rocadePlace);
		}
	}

	private void checkRocadeLeft() {

		if (moved.peek()
				|| (!inBounds(currentPlace.getX(), -(yDelta * 4)) || !inBounds(currentPlace.getY(), (xDelta * 4)))
				|| currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - (yDelta * 4)][currentPlace
						.getY() + (xDelta * 4)] == null) {

			return;
		}

		Piece otherPiece = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
				- (yDelta * 4)][currentPlace.getY() + (xDelta * 4)].getPiece();

		if (!isCheck() && inBounds(currentPlace.getX(), -yDelta) && inBounds(currentPlace.getY(), xDelta)
				&& inBounds(currentPlace.getX(), -(yDelta * 2)) && inBounds(currentPlace.getY(), +(xDelta * 2))
				&& inBounds(currentPlace.getX(), -(yDelta * 3)) && inBounds(currentPlace.getY(), +(xDelta * 3))
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - yDelta][currentPlace.getY()
						+ xDelta] != null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - yDelta][currentPlace.getY()
						+ xDelta].getPiece() == null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - (yDelta * 2)][currentPlace
						.getY() + (xDelta * 2)] != null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - (yDelta * 2)][currentPlace
						.getY() + (xDelta * 2)].getPiece() == null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - (yDelta * 3)][currentPlace
						.getY() + (xDelta * 3)] != null
				&& currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - (yDelta * 3)][currentPlace
						.getY() + (xDelta * 3)].getPiece() == null
				&& otherPiece instanceof Tower && otherPiece.getColor() == color && !otherPiece.moved.peek()) {

			Place rocadePlace = getRocadePlace((Tower) otherPiece, false);

			possibleMoves.add(rocadePlace);
		}
	}

	private boolean inBounds(int current, int delta) {

		return current + delta > 0 && current + delta < 12;
	}

	private Place getRocadePlace(Tower otherPiece, boolean right) {

		Place otherPlace;

		int xIndex, yIndex;

		if (right) {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
					+ (yDelta * 2)][currentPlace.getY() - (xDelta * 2)];

			xIndex = currentPlace.getX() + yDelta;
			yIndex = currentPlace.getY() - xDelta;

		} else {

			otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()
					- (yDelta * 2)][currentPlace.getY() + (xDelta * 2)];

			xIndex = currentPlace.getX() - yDelta;
			yIndex = currentPlace.getY() + xDelta;
		}

		if (otherPlace.getRocade() == null) {

			Rocade rocade = new Rocade(currentPlace, otherPlace, this, otherPiece,
					currentPlace.getGameMaster().getBoard().getPlacez()[xIndex][yIndex]);

			otherPlace.setRocade(rocade);

			rocades.add(rocade);
		}

		return otherPlace;
	}

	public void addAttacks() {

		for (Place place : possibleMoves) {

			attack(place);
		}
	}

	@Override
	public boolean moveTo(Place placeTo, boolean createAttackingPlaces, boolean unused) {

		boolean result = super.moveTo(placeTo, false, false);

		prepareMovez();

		return result;
	}

	public void forceMovez() {

		movez = new ArrayList<Move>();

		for (Place place : possibleMoves) {

			movez.add(new Move(currentPlace, place, this, "m"));
		}

		for (Rocade rocade : rocades) {

			movez.add(rocade);
		}
	}
}
