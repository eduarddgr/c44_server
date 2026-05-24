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

import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.GameMaster;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.pieces.Piece;

import lombok.Getter;

public class BoardWrapper {

	private Board board;

	@Getter
	private Player[] playerz = new Player[4];

	BoardWrapper(GameMaster gameMaster) {

		System.out.println("creating BoardWrapper");

		GameMaster myGameMaster = new GameMaster("-1");

		board = myGameMaster.getBoard();

		for (int i = 0; i < playerz.length; i++) {

			playerz[i] = new Player(gameMaster.getPlayerz()[i]);

			playerz[i].copyPiecez(gameMaster.getPlayerz()[i], board);
		}
	}

	public void execute(Move move) {

		Piece piece = playerz[move.getPiece().getPlayer().getColor().getSeq()]
				.getPieceByIdentifier(move.getPiece().getIdentifier());

		Move internalMove = piece.getCorrespondingMove(move);

		internalMove.execute(false);
	}

}
