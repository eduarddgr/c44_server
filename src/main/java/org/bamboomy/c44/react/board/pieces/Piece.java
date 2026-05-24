
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
import org.bamboomy.c44.react.board.Md5;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.move.LinePieceLine;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Piece {

	@Getter
	@Setter
	protected int color, oldColor;

	@Getter
	protected Player player;

	@Getter
	private final String identifier;

	@Getter
	@Setter
	protected Place currentPlace;

	@Getter
	private String md5;

	@Getter
	@Setter
	protected ArrayList<Move> movez = new ArrayList<>();

	@Getter
	protected boolean pinned = false;

	protected boolean inited = false;

	protected ArrayList<Piece> capturedPieces = new ArrayList<>();

	@Setter
	protected boolean removed = false;

	@Getter
	protected final PieceValue value;

	@Getter
	protected Stack<Boolean> moved = new Stack<>();

	private static final boolean DEBUG = false;

	@Getter
	@Setter
	private boolean dead = false;

	public Piece(Place place, int color, Player player, String identifier, PieceValue value) {

		this.currentPlace = place;

		place.setPiece(this);

		this.color = color;
		this.oldColor = color;

		this.player = player;

		recalculateHash();

		this.identifier = identifier;

		this.value = value;

		moved.push(false);

		// Stack<Boolean> copy = (Stack<Boolean>) moved.clone();
	}

	public Piece(Place place, int color, Player player, String identifier, PieceValue value, boolean removed) {

		this(place, color, player, identifier, value);

		this.removed = removed;
	}

	public Piece(Place place, int color, Player player, String identifier, PieceValue value, boolean moved, Board board,
			String md5, int oldColor) {

		this.currentPlace = place;

		this.color = color;
		this.oldColor = oldColor;

		this.player = player;

		this.identifier = identifier;

		this.value = value;

		this.moved.push(moved);

		this.md5 = md5;

		movez = new ArrayList<>();
	}

	public void recalculateHash() {

		String time = System.currentTimeMillis() + "6+time.getBytes(\"UTF-8\");";

		time += (Board.SECURE_RANDOM.nextDouble() * 777);

		md5 = Md5.md5(time);
	}

	public void calculateMovez() {

		movez = new ArrayList<>();

		pinned = calculatePinned();
	}

	public boolean moveTo(Place placeTo, boolean unused, boolean alsoUnused) {

		currentPlace.setPiece(null);

		currentPlace = placeTo;

		if (currentPlace.getPiece() != null) {

			if (DEBUG) {

				log.debug("taken: (" + currentPlace.getPiece().getIdentifier() + ") -> (" + getIdentifier() + ")");
			}

			currentPlace.getPiece().setRemoved(true);

			capturedPieces.add(currentPlace.getPiece());
		}

		currentPlace.setPiece(this);

		moved.push(true);

		return true;
	}

	public Move getMove(String to) {

		for (Move move : movez) {

			if (move.getTo().getMd5().equals(to)) {

				return move;
			}
		}

		return null;
	}

	public synchronized boolean handleOtherPlaceNE(Place otherPlace) {

		if (otherPlace != null && otherPlace.getPiece() != null) {

			if (DEBUG) {

				log.debug("attack (" + otherPlace.getPiece().getColor() + "(" + otherPlace.getPiece().getIdentifier()
						+ "))(" + color + ")");
			}

			if (otherPlace.getPiece().getColor() != color && otherPlace.getPiece().getColor() != player.getAlliance()
					.getOtherColor(Color.getBySeq(color)).getSeq()) {

				movez.add(new Move(currentPlace, otherPlace, this, "m"));
			}

			// }

			/*
			 * if (!isPinnable()) { //player.kingAttack(otherPlace); }
			 * 
			 */

			return true;

		} else if (otherPlace != null) {

			movez.add(new Move(currentPlace, otherPlace, this, "m"));

			// player.kingAttack(otherPlace);
		}

		return false;
	}

	public void setChecks() {

		// nothing to be done here, this method is only important for LinePieces
		// (and is overwritten there)
	}

	private void recalculateChecks(King firstKing, King secondKing) {

		firstKing.prepareMovez();
		secondKing.prepareMovez();

		player.getGameMaster().updateMovez(player.getAlliance());

		firstKing.calculateMovez();
		secondKing.calculateMovez();
	}

	public void filterCheckMovez(King firstKing, King secondKing) {

		ArrayList<Move> legalMovez = new ArrayList<>();

		recalculateChecks(firstKing, secondKing);

		for (Move move : movez) {

			if (DEBUG) {

				log.debug("evaluating: move(" + this.getClass() + "): " + " from: (" + move.getFrom().getX() + ", "
						+ move.getFrom().getY() + ")" + " to: (" + move.getTo().getX() + ", " + move.getTo().getY()
						+ ")");
			}

			move.execute(false);

			recalculateChecks(firstKing, secondKing);

			if (!firstKing.isCheck() && !secondKing.isCheck()) {

				log.debug("not check");

				legalMovez.add(move);
			}

			move.rollback();
		}

		movez = legalMovez;
	}

	public void rollBackMoved() {

		moved.pop();
	}

	private boolean calculatePinned() {

		for (LinePieceLine kingLine : currentPlace.getKingLinez()) {

			// TODO: kingLine

		}

		return false;

	}

	public Move getCorrespondingMove(Move move) {

		System.out.println("m (" + identifier + "): f x:" + move.getFrom().getX() + ", y:" + move.getFrom().getY()
				+ " t x:" + move.getTo().getX() + ", y:" + move.getTo().getY());

		System.out.println(movez.size());

		for (Move correspondingCandidate : movez) {

			System.out.println("cm: f x:" + correspondingCandidate.getFrom().getX() + ", y:"
					+ correspondingCandidate.getFrom().getY() + " t x:" + correspondingCandidate.getTo().getX() + ", y:"
					+ correspondingCandidate.getTo().getY());
		}

		for (Move correspondingCandidate : movez) {

			if (correspondingCandidate.getFrom().getX() == move.getFrom().getX()
					&& correspondingCandidate.getFrom().getY() == move.getFrom().getY()
					&& correspondingCandidate.getTo().getX() == move.getTo().getX()
					&& correspondingCandidate.getTo().getY() == move.getTo().getY()) {

				return correspondingCandidate;
			}
		}

		throw new RuntimeException("couldn't find corresponding move :(");
	}

	public void filterFinishingMovez(King king) {

		ArrayList<Move> finishingMovez = new ArrayList<Move>();

		for (Move move : movez) {

			if (move.getTo().getPiece() != null && move.getTo().getPiece().equals(king)) {

				finishingMovez.add(move);
			}
		}

		movez = finishingMovez;
	}

	public boolean isRemoved() {

		return removed || dead;
	}
}
