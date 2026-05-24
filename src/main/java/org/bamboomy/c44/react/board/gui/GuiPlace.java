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
import org.bamboomy.c44.react.board.pieces.Piece;

@Getter
public class GuiPlace {

    private GuiPiece guiPiece = null;

    private int x, y;

    private String md5 = "md5";

    public GuiPlace(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public GuiPlace(Piece piece, String color,
                    boolean currentPlayer, int x, int y) {

        this(x, y);

        if (piece != null) {

            guiPiece = new GuiPiece(piece, color, currentPlayer);
        }
    }

    public GuiPlace(int x, int y, String md5) {

        this.x = x;
        this.y = y;

        this.md5 = md5;
    }
}
