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

import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.pieces.Pawn;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class EnPassant extends Move {

	private Pawn subject;

	protected Pawn taker;

	private Player player;

	private Place place;

	private static final boolean DEBUG = false;

	EnPassant(Pawn subject, Place to, String identifier) {

		super(null, to, null, identifier);

		this.subject = subject;
		this.place = to;
	}

	public EnPassant(Player player, Pawn subject, Place to, String identifier) {

		this(subject, to, identifier);

		this.player = player;
	}

	public EnPassant attach() {

		if (place.getEnPassant() != null) {

			DoubleEnpassant doubleEnpassant = new DoubleEnpassant(place.getEnPassant(), this, "de");

			place.setEnPassant(doubleEnpassant);

			if (DEBUG) {

				log.debug("attaching double enpassant");
			}

			return doubleEnpassant;

		} else {

			place.setEnPassant(this);

			return this;
		}
	}

	@Override
	public boolean execute(boolean unused) {

		removePawn();

		piece = taker;

		return taker.moveTo(to, true, false);
	}

	void removePawn() {

		setTakenPiece(subject);

		subject.getCurrentPlace().setPiece(null);

		subject.setRemoved(true);
	}

	public void destroy() {

		place.setEnPassant(null);
	}

	public EnPassant release() {

		if (place.getEnPassant() instanceof DoubleEnpassant) {

			EnPassant returnValue = ((DoubleEnpassant) place.getEnPassant()).getFirstEnpassant();

			place.setEnPassant(returnValue);

			return returnValue;

		} else {

			place.setEnPassant(null);

			return null;
		}
	}

	@Override
	public void rollback() {

		resetPiece();

		resetSubject();
	}

	protected void resetSubject() {

		subject.getCurrentPlace().setPiece(subject);

		subject.setRemoved(false);
	}
}
