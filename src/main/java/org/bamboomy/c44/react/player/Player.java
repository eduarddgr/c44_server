
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

package org.bamboomy.c44.react.player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.move.EnPassant;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.pieces.*;

import java.util.ArrayList;

@Slf4j
public class Player {

	@Getter
	protected final ArrayList<Piece> piecez = new ArrayList<Piece>();

	@Getter
	private final Color color;

	@Getter
	protected GameMaster gameMaster;

	@Getter
	protected ColorsTaken colorsTaken;

	@Getter
	@Setter
	private King king;

	@Getter
	private boolean inited = false;

	private Piece myFirstPiece, mySecondPiece, myThirdPiece;

	@Getter
	@Setter
	private Move lastMove;

	@Setter
	private EnPassant enPassant;

	@Getter
	private ArrayList<LinePiece> linePiecez = new ArrayList<>();

	@Getter
	protected Alliance alliance;

	@Getter
	@Setter
	private boolean kamikaze = false;

	@Getter
	@Setter
	private boolean finished = false;

	@Getter
	@Setter
	private boolean dead = false;

	public Player(ColorsTaken userColor, GameMaster gameMaster, Alliance alliance) {

		this.colorsTaken = userColor;

		this.color = Color.getByName(userColor.getColor());

		this.gameMaster = gameMaster;

		this.alliance = alliance;
	}

	public Player(Player otherPlayer) {

		this.colorsTaken = otherPlayer.colorsTaken;

		this.color = Color.getByName(otherPlayer.colorsTaken.getColor());

		this.gameMaster = otherPlayer.gameMaster;

		this.alliance = otherPlayer.alliance;
	}

	public void initKing() {

		king.prepareMovez();
	}

	public void initPieces(GameMaster gameMaster) {

		initPieces(gameMaster.getBoard());
	}

	public void initPieces(Board board) {

		for (int i = 0; i < 8; i++) {

			Pawn pawn = new Pawn(
					board.getPlacez()[color.getXMapping().apply(10, i + 2)][color.getYMapping().apply(10, i + 2)],
					color.getSeq(), color.getXDirection(), color.getYDirection(), this, "p" + i);

			piecez.add(pawn);
		}

		Tower tower = new Tower(board.getPlacez()[color.getXMapping().apply(11, 2)][color.getYMapping().apply(11, 2)],
				color.getSeq(), this, "tl");

		piecez.add(tower);

		linePiecez.add(tower);

		tower = new Tower(board.getPlacez()[color.getXMapping().apply(11, 9)][color.getYMapping().apply(11, 9)],
				color.getSeq(), this, "tr");

		piecez.add(tower);

		linePiecez.add(tower);

		piecez.add(new Horse(board.getPlacez()[color.getXMapping().apply(11, 3)][color.getYMapping().apply(11, 3)],
				color.getSeq(), this, "hl"));
		piecez.add(new Horse(board.getPlacez()[color.getXMapping().apply(11, 8)][color.getYMapping().apply(11, 8)],
				color.getSeq(), this, "hr"));

		Bisshop bisshop = new Bisshop(
				board.getPlacez()[color.getXMapping().apply(11, 4)][color.getYMapping().apply(11, 4)], color.getSeq(),
				this, "bl");

		piecez.add(bisshop);

		linePiecez.add(bisshop);

		bisshop = new Bisshop(board.getPlacez()[color.getXMapping().apply(11, 7)][color.getYMapping().apply(11, 7)],
				color.getSeq(), this, "br");

		piecez.add(bisshop);

		linePiecez.add(bisshop);

		Queen queen = new Queen(board.getPlacez()[color.getXMapping().apply(11, 5)][color.getYMapping().apply(11, 5)],
				color.getSeq(), this, "q");

		piecez.add(queen);

		linePiecez.add(queen);

		king = new King(board.getPlacez()[color.getXMapping().apply(11, 6)][color.getYMapping().apply(11, 6)],
				color.getSeq(), color.getXDirection(), color.getYDirection(), this, "k");

		piecez.add(king);

		inited = true;
	}

	public void recalculateHashes() {

		for (Piece piece : piecez) {

			piece.recalculateHash();
		}
	}

	public Piece getPiece(String pieceHash) {

		for (Piece piece : piecez) {

			if (piece.getMd5().equals(pieceHash)) {

				return piece;
			}
		}

		return null;
	}

	public String getString() {

		return color.getName() + " (" + colorsTaken.getName() + ")";
	}

	public void calculateMovez() {

		for (Piece piece : piecez) {

			if (!(piece instanceof King) && !piece.isRemoved()) {

				piece.calculateMovez();
			}
		}
	}

	public void moveRandomPiece() {

		Move move = null;

		if (Math.random() * 2 < 1 && !myFirstPiece.getMovez().isEmpty() && !myFirstPiece.isRemoved()) {

			myFirstPiece.getMovez().get(0).execute(true);

			move = myFirstPiece.getMovez().get(0);

		} else if (!mySecondPiece.getMovez().isEmpty() && !mySecondPiece.isRemoved()) {

			mySecondPiece.getMovez().get(0).execute(true);

			move = mySecondPiece.getMovez().get(0);

		} else {

			myThirdPiece.getMovez().get(0).execute(true);

			move = myThirdPiece.getMovez().get(0);
		}

		setLastMove(move);
	}

	public void destroyEnPassant() {

		if (enPassant != null) {

			enPassant.destroy();
		}
	}

	public boolean isCheck() {

		return king.isCheck();
	}

	public void calculateKingMovez() {

		king.calculateMovez();
	}

	public void filterCheckMovez(King firstKing, King secondKing) {

		for (Piece piece : piecez) {

			if (!piece.isRemoved() && !(piece instanceof King)) {

				piece.calculateMovez();
			}
		}

		for (Piece piece : piecez) {

			if (!piece.isRemoved() && !(piece instanceof King)) {

				piece.filterCheckMovez(firstKing, secondKing);
			}
		}
	}

	public void calculateKingLinez() {

		for (LinePiece linePiece : linePiecez) {

			linePiece.setChecks();
		}
	}

	public void rollBack() {

		lastMove.rollback();
	}

	public int getScore() {

		int result = 0;

		for (Piece piece : piecez) {

			if (!piece.isRemoved()) {

				result += piece.getValue().getValue();
			}
		}

		return result;
	}

	public Piece getPieceByIdentifier(String identifier) {

		for (Piece piece : piecez) {

			if (piece.getIdentifier().equalsIgnoreCase(identifier)) {

				return piece;
			}
		}

		throw new RuntimeException("couldn't find piece :(");
	}

	public void copyPiecez(Player otherPlayer, Board board) {

		System.out.println(piecez.size());

		for (Piece piece : otherPlayer.piecez) {

			if (piece.isRemoved()) {

				continue;
			}

			if (piece instanceof Pawn) {

				piecez.add(new Pawn((Pawn) piece, this, board));

			} else if (piece instanceof Bisshop) {

				piecez.add(new Bisshop((Bisshop) piece, this, board));

			} else if (piece instanceof Horse) {

				piecez.add(new Horse((Horse) piece, this, board));

			} else if (piece instanceof King) {

				king = new King((King) piece, this, board);

				piecez.add(king);

			} else if (piece instanceof Queen) {

				piecez.add(new Queen((Queen) piece, this, board));

			} else if (piece instanceof Tower) {

				piecez.add(new Tower((Tower) piece, this, board));
			}
		}
	}

	public boolean cannotMove() {

		int counter = 0;

		for (Piece piece : piecez) {

			if (!piece.isRemoved() && piece.getMovez().size() > 0) {

				counter++;

				break;
			}
		}

		return counter == 0;
	}

	public void createFinishingMovez(Player matedPlayer) {

		calculateMovez();

		for (Piece piece : piecez) {

			if (!piece.isRemoved()) {

				piece.filterFinishingMovez(matedPlayer.getKing());
			}
		}
	}

	public int getNumberOfMovez() {

		int result = 0;

		for (Piece piece : piecez) {

			if (!piece.isRemoved()) {

				result += piece.getMovez().size();
			}
		}

		return result;
	}

	public void die() {

		dead = true;

		for (Piece piece : piecez) {

			piece.setDead(true);
		}
	}
}
