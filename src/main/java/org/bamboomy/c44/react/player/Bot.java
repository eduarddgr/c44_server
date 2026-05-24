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

package org.bamboomy.c44.react.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.pieces.Piece;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bot extends Player {

	private int NB_OF_AVAILABLE_CORES = 25;

	private GameMaster gameMaster;

	private BoardWrapper[] boardWrappers = new BoardWrapper[NB_OF_AVAILABLE_CORES];

	private final Semaphore available = new Semaphore(NB_OF_AVAILABLE_CORES);

	private ArrayList<Move> movez;

	private Move bestMove;

	private int maxValue;

	private static final int MAX_DEPTH = 8;

	private int totalNodes = 0;

	private String[] movezStringz;

	private String output = "";

	private static final boolean DEBUG = false;

	public Bot(ColorsTaken userColor, GameMaster gameMaster, Alliance alliance) {

		super(userColor, gameMaster, alliance);

		this.gameMaster = gameMaster;

		log.debug("I'm a bot, my color is: " + userColor.getColor());
	}

	public void init() {

		bestMove = null;
		maxValue = -Integer.MAX_VALUE;

		for (int i = 0; i < NB_OF_AVAILABLE_CORES; i++) {

			boardWrappers[i] = new BoardWrapper(gameMaster);
		}
	}

	private synchronized void recieveMove(Move move, int value, int moveCounter, int nodes) {

		totalNodes += nodes;

		if (value > maxValue) {

			bestMove = move;

			maxValue = value;
		}

		movezStringz[moveCounter] = "(" + value + "(" + nodes + "))";

		output = "Local bot:\n\n";

		output += "total: " + totalNodes + "\n";

		for (int i = 0; i < movezStringz.length; i++) {

			for (int j = 0; j < 4; j++) {

				i++;

				if (i >= movezStringz.length) {

					break;
				}

				output += movezStringz[i];
			}

			output += "\n\n";
		}

		gameMaster.setRobotOutput(output);
	}

	public void executeMove() {

		log.debug("Going to move: my color is: " + getColor().getName());

		calculateMovez();

		calculateKingMovez();

		movez = new ArrayList<>();

		for (Piece piece : piecez) {

			movez.addAll(piece.getMovez());
		}

		(new Thread(new Calculator(movez))).start();
	}

	private class Calculator implements Runnable {

		private ArrayList<Move> movez;

		private ArrayList<Integer> availableBoardIndexes = new ArrayList<Integer>();
		private ArrayList<Integer> busyBoardIndexes = new ArrayList<Integer>();

		public Calculator(ArrayList<Move> movez) {
			this.movez = new ArrayList<>(movez);
		}

		public synchronized void releaseIndex(int index) {

			if (DEBUG) {

				log.debug("releasing:" + index);
			}

			for (int i = 0; i < busyBoardIndexes.size(); i++) {

				if (busyBoardIndexes.get(i) == index) {

					boardWrappers[busyBoardIndexes.get(i)] = new BoardWrapper(gameMaster);

					availableBoardIndexes.add(busyBoardIndexes.remove(i));

					return;
				}
			}

			throw new RuntimeException("couldn't find busy index");
		}

		@Override
		public void run() {

			ArrayList<Thread> threadz = new ArrayList<>();

			for (int i = 0; i < NB_OF_AVAILABLE_CORES; i++) {

				availableBoardIndexes.add(i);
			}

			int numberOfMovez = 0;

			movezStringz = new String[movez.size()];

			try {

				int counter = 0;

				while (!movez.isEmpty()) {

					log.debug("waiting for next calculator...");

					available.acquire();

					log.debug("going forth with:" + availableBoardIndexes.get(0));

					MoveCalculator calculator = new MoveCalculator(boardWrappers[availableBoardIndexes.get(0)],
							movez.remove(0), Color.getByName(colorsTaken.getColor()), alliance,
							availableBoardIndexes.get(0), this, counter++);

					busyBoardIndexes.add(availableBoardIndexes.remove(0));

					Thread thread = new Thread(calculator);
					threadz.add(thread);
					thread.start();

					numberOfMovez++;

					// movezString = "Movez(" + numberOfMovez + "/" + movez.size() + ")";
				}

				for (Thread thread : threadz) {
					thread.join();
				}

				gameMaster.movePiece(bestMove.getPiece().getMd5(), bestMove.getTo().getMd5(), true, false);

				// bestMove.execute(false);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class MoveCalculator implements Runnable {

		private BoardWrapper boardWrapper;
		private Color color;
		private Move move;
		private Alliance alliance;
		private int index;
		private Calculator calculator;

		private int nodes = 0;

		private int moveCounter;

		public MoveCalculator(BoardWrapper boardWrapper, Move move, Color color, Alliance alliance, int index,
				Calculator calculator, int moveCounter) {
			this.boardWrapper = boardWrapper;
			this.color = color;
			this.alliance = alliance;
			this.move = move;
			this.index = index;
			this.calculator = calculator;
			this.moveCounter = moveCounter;
		}

		@Override
		public void run() {

			nodes = 0;

			System.out.println(index);

			boardWrapper.getPlayerz()[color.getSeq()].calculateMovez();
			boardWrapper.getPlayerz()[color.getSeq()].calculateKingMovez();

			boardWrapper.execute(move);

			int evaluation = 0;

			// TODO: get next (non dead) player

			evaluation = evaluate(1, -Integer.MAX_VALUE, Integer.MAX_VALUE, color.getSeq(),
					alliance.isInAlliance(boardWrapper.getPlayerz()[(color.getSeq() + 1) % 4].getColor()),
					alliance.isInAlliance(boardWrapper.getPlayerz()[(color.getSeq() + 2) % 4].getColor()));

			recieveMove(move, evaluation, moveCounter, nodes);

			calculator.releaseIndex(index);

			boardWrapper = null;

			available.release();
		}

		private int evaluate(int depth, int alpha, int beta, int playerIndex, boolean isMax, boolean isNextMax) {

			int finalEvaluation;

			int currentPlayerIndex = (playerIndex + 1) % 4;

			ArrayList<Move> moves = new ArrayList<Move>();

			if (depth >= MAX_DEPTH) {

				// moveCounter++;

				/*
				 * 
				 * if (moveCounter == increment) {
				 * 
				 * updateMoveCounter(increment);
				 * 
				 * moveCounter -= increment; }
				 */

				/*
				 * if (playerz[currentPlayerIndex].equals(bot) && bot.getKing().isCheck() &&
				 * !bot.canPrevent()) {
				 * 
				 * finalEvaluation = -1000_000;
				 * 
				 * } else {
				 */

				finalEvaluation = calculateBoardValue(color, alliance);

				// }

			} else {

				/*
				 * ArrayList<Piece> preventPieces;
				 * 
				 * if (playerz.get(currentPlayerIndex).getKing().isCheck()) {
				 * 
				 * if (playerz.get(currentPlayerIndex).canPrevent()) {
				 * 
				 * preventPieces = playerz.get(currentPlayerIndex).getPreventPieces();
				 * 
				 * for (Piece piece : preventPieces) {
				 * 
				 * moves.addAll(piece.getPreventMovezOfPiece()); }
				 * 
				 * } else {
				 * 
				 * if (!playerz.get(currentPlayerIndex).equals(bot)) {
				 * 
				 * ArrayList<Integer> newDeadPlayers = new ArrayList<>(deadPlayers);
				 * 
				 * newDeadPlayers.add(currentPlayerIndex);
				 * 
				 * return evaluate(depth, alpha, beta, currentPlayerIndex, newDeadPlayers,
				 * isAlly(playerz.get((currentPlayerIndex + 1) % 4)),
				 * isAlly(playerz.get((currentPlayerIndex + 2) % 4))); } }
				 * 
				 * } else {
				 * 
				 * ArrayList<Piece> copyOfPiecez = new
				 * ArrayList<>(playerz.get(currentPlayerIndex).getPiecez());
				 * 
				 * for (Piece piece : copyOfPiecez) {
				 * 
				 * if (piece.canMove(myGameMaster)) {
				 * 
				 * moves.addAll(piece.getPossibleMovez()); } } }
				 */

				boardWrapper.getPlayerz()[currentPlayerIndex].calculateMovez();
				boardWrapper.getPlayerz()[currentPlayerIndex].calculateKingMovez();

				for (Piece piece : boardWrapper.getPlayerz()[currentPlayerIndex].piecez) {

					moves.addAll(piece.getMovez());
				}

				if (isMax) {

					finalEvaluation = -Integer.MAX_VALUE;

				} else {

					finalEvaluation = Integer.MAX_VALUE;
				}

				Collections.shuffle(moves);

				for (Move move : moves) {

					move.execute(false);

					int evaluation;

					try {

						evaluation = evaluate(depth + 1, alpha, beta, currentPlayerIndex,
								alliance.isInAlliance(
										boardWrapper.getPlayerz()[(currentPlayerIndex + 1) % 4].getColor()),
								alliance.isInAlliance(
										boardWrapper.getPlayerz()[(currentPlayerIndex + 2) % 4].getColor()));

					} finally {

						move.rollback();
					}

					if (isMax) {

						finalEvaluation = Math.max(finalEvaluation, evaluation);

						alpha = Math.max(finalEvaluation, alpha);

						if (alpha >= beta) {

							break;
						}

					} else {

						finalEvaluation = Math.min(finalEvaluation, evaluation);

						beta = Math.min(finalEvaluation, beta);

						if (beta <= alpha) {

							break;
						}
					}
				}
			}

			return finalEvaluation;
		}

		public int calculateBoardValue(Color color, Alliance alliance) {

			nodes++;

			int result = 0;

			for (Player player : boardWrapper.getPlayerz()) {

				if (alliance.isInAlliance(player.getColor())) {

					result += player.getScore();

				} else { // if (!player.isDead()) {

					result -= player.getScore();
				}
			}

			return result;
		}
	}
}
