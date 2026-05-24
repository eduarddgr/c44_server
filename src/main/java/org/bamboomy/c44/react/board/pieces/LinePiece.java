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
import org.bamboomy.c44.react.board.move.LinePieceLine;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LinePiece extends Piece {

	private static final boolean DEBUG = false;

	@Getter
	protected ArrayList<LinePieceLine> kingLinez = new ArrayList<>();

	public LinePiece(Place place, int color, Player player, String identifier, PieceValue value) {

		super(place, color, player, identifier, value);
	}

	public LinePiece(Place place, int color, Player player, String identifier, PieceValue value, boolean removed) {

		super(place, color, player, identifier, value, removed);
	}

	public LinePiece(Place place, int color, Player player, String identifier, PieceValue value, boolean moved,
			Board board, String md5, int oldColor) {

		super(place, color, player, identifier, value, moved, board, md5, oldColor);
	}

	protected abstract void createKingLinez();

	@Override
	public void setChecks() {

		cleanKingeLinez();

		kingLinez = new ArrayList<>();

		if (!isRemoved()) {

			createKingLinez();
		}
	}

	protected boolean handleKingLine(Place otherPlace, ArrayList<Place> possibleKingLineList) {

		for (Player kingPlayer : player.getGameMaster().getPlayerz()) {

			if (!kingPlayer.getAlliance().isInAlliance(player.getColor())
					&& kingPlayer.getKing().shouldBeAttacked(otherPlace)) {

				LinePieceLine linePieceLine = new LinePieceLine(this, otherPlace, possibleKingLineList);

				kingLinez.add(linePieceLine);

				otherPlace.getKingLinez().add(linePieceLine);
			}
		}

		return otherPlace == null;
	}

	public void cleanKingeLinez() {

		for (LinePieceLine kingLine : kingLinez) {

			kingLine.destroy();
		}
	}

	protected void addBisshopMovez() {

		int counter = 1;

		for (int i = currentPlace.getX() + 1; i < 12; i++) {

			if (currentPlace.getY() - counter < 0) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY() - counter];

			if (handleOtherPlaceNE(otherPlace)) {

				break;
			}

			counter++;
		}

		counter = 1;

		for (int i = currentPlace.getY() + 1; i < 12; i++) {

			if (currentPlace.getX() + counter > 11) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + counter][i];

			if (handleOtherPlaceNE(otherPlace)) {

				break;
			}

			counter++;
		}

		counter = 1;

		for (int i = currentPlace.getX() - 1; i >= 0; i--) {

			if (currentPlace.getY() + counter > 11) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY() + counter];

			if (handleOtherPlaceNE(otherPlace)) {

				break;
			}

			counter++;
		}

		counter = 1;

		for (int i = currentPlace.getY() - 1; i >= 0; i--) {

			if (currentPlace.getX() - counter < 0) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - counter][i];

			if (handleOtherPlaceNE(otherPlace)) {

				break;
			}

			counter++;
		}
	}

	protected void addTowerMovez() {

		for (int i = currentPlace.getX() + 1; i < 12; i++) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY()];

			if (DEBUG) {

				log.debug("i: " + i + ", y: " + currentPlace.getY() + " null?" + otherPlace);
			}

			if (handleOtherPlaceNE(otherPlace)) {

				if (DEBUG) {

					log.debug("i: " + i + ", y: " + currentPlace.getY() + " null?" + otherPlace + "(true)");
				}

				break;
			}
		}

		for (int i = currentPlace.getY() + 1; i < 12; i++) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()][i];

			if (DEBUG) {

				log.debug("x: " + currentPlace.getX() + " + i: " + i + " null?" + otherPlace);
			}

			if (handleOtherPlaceNE(otherPlace)) {

				if (DEBUG) {

					log.debug("x: " + currentPlace.getX() + " + i: " + i + " null?" + otherPlace + "(true)");
				}

				break;
			}
		}

		for (int i = currentPlace.getX() - 1; i >= 0; i--) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY()];

			if (DEBUG) {

				log.debug("i: " + i + ", y: " + currentPlace.getY() + " null?" + otherPlace);
			}

			if (handleOtherPlaceNE(otherPlace)) {

				if (DEBUG) {

					log.debug("i: " + i + ", y: " + currentPlace.getY() + " null?" + otherPlace + "(true)");
				}

				break;
			}
		}

		for (int i = currentPlace.getY() - 1; i >= 0; i--) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()][i];

			if (DEBUG) {

				log.debug("x: " + currentPlace.getX() + " + i: " + i + " null?" + otherPlace);
			}

			if (handleOtherPlaceNE(otherPlace)) {

				if (DEBUG) {

					log.debug("x: " + currentPlace.getX() + " + i: " + i + " null?" + otherPlace + "(true)");
				}

				break;
			}
		}
	}

	protected void createKingLinezForBissop() {

		int counter = 1;

		ArrayList<Place> currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getX() + 1; i < 12; i++) {

			if (currentPlace.getY() - counter < 0) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY() - counter];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);

			counter++;
		}

		counter = 1;

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getY() + 1; i < 12; i++) {

			if (currentPlace.getX() + counter > 11) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() + counter][i];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);

			counter++;
		}

		counter = 1;

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getX() - 1; i >= 0; i--) {

			if (currentPlace.getY() + counter > 11) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY() + counter];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);

			counter++;
		}

		counter = 1;

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getY() - 1; i >= 0; i--) {

			if (currentPlace.getX() - counter < 0) {

				break;
			}

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX() - counter][i];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);

			counter++;
		}
	}

	protected void createKingLinezForRook() {

		ArrayList<Place> currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getX() + 1; i < 12; i++) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY()];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);

		}

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getY() + 1; i < 12; i++) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()][i];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);
		}

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getX() - 1; i >= 0; i--) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[i][currentPlace.getY()];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);
		}

		currentKingLineList = new ArrayList<Place>();

		for (int i = currentPlace.getY() - 1; i >= 0; i--) {

			Place otherPlace = currentPlace.getGameMaster().getBoard().getPlacez()[currentPlace.getX()][i];

			ArrayList<Place> possibleKingLineList = new ArrayList<Place>(currentKingLineList);

			if (handleKingLine(otherPlace, possibleKingLineList)) {

				break;
			}

			currentKingLineList.add(otherPlace);
		}
	}

	@Override
	public boolean moveTo(Place placeTo, boolean createKingLinez, boolean unused) {

		boolean result = super.moveTo(placeTo, false, false);

		if (createKingLinez) {

			calculateMovez();
		}

		return result;
	}
}
