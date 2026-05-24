
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

import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.player.Player;

public class Bisshop extends LinePiece {

	public Bisshop(Place place, int color, Player player, String identifier) {

		super(place, color, player, identifier, PieceValue.BISSHOP);
	}

	public Bisshop(Bisshop otherBisshop, Player player, Board board) {

		super(board.getPlacez()[otherBisshop.getCurrentPlace().getX()][otherBisshop.getCurrentPlace().getY()],
				otherBisshop.color, player, otherBisshop.getIdentifier(), PieceValue.BISSHOP, otherBisshop.removed);
	}
	
	public Bisshop(Place place, int color, Player player, String identifier, boolean moved, Board board, String md5, int oldColor) {

		super(place, color, player, identifier, PieceValue.BISSHOP, moved, board, md5, oldColor);
	}

	@Override
	public void calculateMovez() {

		movez = new ArrayList<>();

		addBisshopMovez();

		inited = true;

		setChecks();
	}

	@Override
	protected void createKingLinez() {

		createKingLinezForBissop();
	}
}
