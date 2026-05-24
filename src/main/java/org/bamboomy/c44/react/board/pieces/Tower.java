
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
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Tower extends LinePiece {

	private static final boolean DEBUG = false;

	public Tower(Place place, int color, Player player, String identifier) {

		super(place, color, player, identifier, PieceValue.TOWER);
	}

	public Tower(Tower otherTower, Player player, Board board) {

		super(board.getPlacez()[otherTower.getCurrentPlace().getX()][otherTower.getCurrentPlace().getY()],
				otherTower.color, player, otherTower.getIdentifier(), PieceValue.TOWER, otherTower.removed);

		if (DEBUG) {

			log.debug("(" + Color.getBySeq(getColor()) + ") (" + getIdentifier() + "): x: "
					+ otherTower.getCurrentPlace().getX() + ", y: " + otherTower.getCurrentPlace().getY());
		}

		moved = (Stack<Boolean>) moved.clone();
	}

	public Tower(Place place, int color, Player player, String identifier, boolean moved, Board board, String md5, int oldColor) {

		super(place, color, player, identifier, PieceValue.TOWER, moved, board, md5, oldColor);
	}

	@Override
	protected void createKingLinez() {

		createKingLinezForRook();
	}

	@Override
	public void calculateMovez() {

		super.calculateMovez();

		addTowerMovez();

		inited = true;

		setChecks();
	}
}
