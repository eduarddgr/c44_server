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

import java.util.ArrayList;

import org.bamboomy.c44.react.board.Board;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.move.Attack;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;

public abstract class PlaceAttackingPiece extends Piece {

	@Getter
	protected ArrayList<Attack> attackingPlaces = new ArrayList<>();

	public PlaceAttackingPiece(Place place, int color, Player player, String identifier, PieceValue value) {

		super(place, color, player, identifier, value);
	}

	public PlaceAttackingPiece(Place place, int color, Player player, String identifier, PieceValue value,
			boolean removed) {

		super(place, color, player, identifier, value, removed);
	}

	public PlaceAttackingPiece(Place place, int color, Player player, String identifier, PieceValue value,
			boolean moved, Board board, String md5, int oldColor) {

		super(place, color, player, identifier, value, moved, board, md5, oldColor);
	}

	protected void attack(Place otherPlace) {

		for (Player kingPlayer : player.getGameMaster().getPlayerz()) {

			if (otherPlace != null && currentPlace.getPiece() != null
					&& kingPlayer.getColor() != currentPlace.getPiece().getPlayer().getColor()
					&& kingPlayer.getAlliance().getOtherColor(kingPlayer.getColor()) != currentPlace.getPiece()
							.getPlayer().getColor()
					&& kingPlayer.getKing().shouldBeAttacked(otherPlace)) {

				attackingPlaces.add(kingPlayer.getKing().addAttack(otherPlace));
			}
		}
	}

	@Override
	public void calculateMovez() {

		super.calculateMovez();
	}

	@Override
	public boolean moveTo(Place placeTo, boolean createAttackingPlaces, boolean calculateMovez) {

		boolean result = super.moveTo(placeTo, false, false);

		if (createAttackingPlaces) {

			for (Attack attackingPlace : attackingPlaces) {

				attackingPlace.destroy();
			}

			attackingPlaces = new ArrayList<>();

			if (calculateMovez) {

				calculateMovez();
			}
		}

		return result;
	}

}
