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
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.pieces.Pawn;

@Getter
public class DoubleEnpassant extends EnPassant {

    private EnPassant firstEnpassant;
    private EnPassant lastEnpassant;

    public DoubleEnpassant(Pawn subject, Place to, String identifier) {
        super(subject, to, identifier);
    }

    public DoubleEnpassant(EnPassant firstEnpassant, EnPassant lastEnpassant, String identifier) {
        this(firstEnpassant.getSubject(), firstEnpassant.getPlace(), identifier);

        this.firstEnpassant = firstEnpassant;
        this.lastEnpassant = lastEnpassant;
    }

    @Override
    public boolean execute(boolean unused) {
    	
    	piece = taker;

        firstEnpassant.removePawn();

        lastEnpassant.removePawn();

        return taker.moveTo(to, true, false);
    }

    @Override
    public void destroy() {

        lastEnpassant.getPlace().setEnPassant(lastEnpassant);
    }
    
	@Override
	public void rollback() {

		resetPiece();
		
        firstEnpassant.resetSubject();

        lastEnpassant.resetSubject();
	}

}
