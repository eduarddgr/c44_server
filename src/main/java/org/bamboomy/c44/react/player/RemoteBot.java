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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteBot extends Player {

	private ArrayList<Move> movez;

	private static final boolean DEBUG = false;

	@Setter
	@Getter
	private boolean registered = false;
	
	@Setter
	@Getter
	private String playerHash;

	public RemoteBot(ColorsTaken userColor, GameMaster gameMaster, Alliance alliance) {

		super(userColor, gameMaster, alliance);

		this.gameMaster = gameMaster;

		log.debug("I'm a bot, my color is: " + userColor.getColor());
	}

	public void executeMove() {

		log.debug("Going to move: my color is: " + getColor().getName());

		calculateMovez();

		calculateKingMovez();

		movez = new ArrayList<>();

		for (Piece piece : piecez) {

			movez.addAll(piece.getMovez());
		}
	}

}
