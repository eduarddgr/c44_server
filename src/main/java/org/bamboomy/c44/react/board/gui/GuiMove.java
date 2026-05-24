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

import org.bamboomy.c44.react.board.move.Move;
import org.bamboomy.c44.react.player.Color;

@Getter
public class GuiMove {

	private GuiPlace from, to;

	private Color color;

	private String identifier;

	private GuiMove enPassant;

	GuiMove(GuiPlace from, GuiPlace to, String identifier) {

		this.from = from;
		this.to = to;

		this.identifier = identifier;
	}

	public GuiMove(Color color) {

		this.color = color;
	}

	public GuiMove(GuiPlace from, GuiPlace to, Color color, String identifier) {

		this(to, from, identifier);
		this.color = color;
	}

	public GuiMove(Move move, Color color) {

		this(move.getFrom().toGuiPlace(), move.getTo().toGuiPlace(true), color, move.getIdentifier());
	}
}
