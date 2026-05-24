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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bamboomy.c44.react.board.Place;
import org.bamboomy.c44.react.board.pieces.King;

@AllArgsConstructor
public class Attack {

    private King king;

    @Getter
    private Place place;

    public void destroy(){

        king.getAttackedPlaces().remove(this);
    }
}
