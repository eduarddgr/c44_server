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

package org.bamboomy.c44.react.board.move;

import lombok.Getter;
import lombok.Setter;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.pieces.Piece;
import org.bamboomy.c44.react.board.pieces.Tower;

@Getter
public class Rocade extends Move {

	@Setter
	private Tower tower;

	private final Place towerToPlace, towerFromPlace;

	public Rocade(Place from, Place to, Piece piece, Tower tower, Place towerToPlace) {

		super(from, to, piece, "r");

		this.tower = tower;
		this.towerToPlace = towerToPlace;

		this.towerFromPlace = tower.getCurrentPlace();
	}

	@Override
	public boolean execute(boolean unused) {

		if (to.getPiece() != null) {

			throw new RuntimeException("there shouldn't be a piece here!!!");
		}

		piece.moveTo(to, false, false);

		tower.moveTo(towerToPlace, true, false);

		return true;
	}

	public boolean contains(Place otherPlace) {

		return otherPlace == towerToPlace || otherPlace == to;
	}

	@Override
	public void rollback() {

		resetPiece();

		tower.moveTo(towerFromPlace, true, false);

		tower.rollBackMoved();
	}

	public void destroy() {

		to.setRocade(null);
	}

}