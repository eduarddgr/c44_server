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

package org.bamboomy.c44.react.board.gui;

import lombok.Getter;
import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.bamboomy.c44.react.player.Color;

import java.util.ArrayList;

@Getter
public class GuiPiece {

	private final String identifier;

	private final int color, oldColor;

	private String md5 = "md5";

	ArrayList<GuiMove> movez = new ArrayList<>();

	private boolean pinned = false;

	private boolean moved = false;

	public GuiPiece(Piece piece, String color, boolean currentPlayer) {

		identifier = piece.getIdentifier();

		if (piece.isDead()) {

			this.color = 4;
			this.oldColor = piece.getOldColor();

		} else {

			this.color = piece.getColor();
			this.oldColor = piece.getColor();
		}

		this.pinned = piece.isPinned();

		moved = piece.getMoved().get(0);

		if (currentPlayer && Color.getBySeq(piece.getColor()).getName().equalsIgnoreCase(color)) {

			this.md5 = piece.getMd5();

			for (Move move : piece.getMovez()) {

				if (move.getFrom() != null) { // exclude enPassants (for now :())

					movez.add(new GuiMove(move.getFrom().toGuiPlace(), move.getTo().toGuiPlace(true),
							move.getIdentifier()));

				} else {

					System.out.println("from is null");
				}
			}
		}
	}
}
