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

package org.bamboomy.c44.react.board.pieces;

import lombok.Getter;

@Getter
public enum PieceValue {

	KING(0, "king", "k", 1000, King.class), QUEEN(1, "queen", "q", 10, Queen.class),
	TOWER(2, "tower", "t", 5, Tower.class), BISSHOP(3, "bisshop", "b", 3, Bisshop.class),
	HORSE(4, "horse", "h", 3, Horse.class), PAWN(5, "pawn", "p", 1, Pawn.class);

	private final int ordinal, value;
	private final String name, letter;
	private final Class pieceClass;

	private PieceValue(int ordinal, String name, String letter, int value, Class pieceClass) {
		this.ordinal = ordinal;
		this.name = name;
		this.letter = letter;
		this.value = value;
		this.pieceClass = pieceClass;
	}

	public static PieceValue getByName(String name) {

		for (PieceValue pv : PieceValue.values()) {

			if (pv.getName().equalsIgnoreCase(name)) {

				return pv;
			}
		}

		return null;
	}
}
