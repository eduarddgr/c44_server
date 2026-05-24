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

import lombok.Getter;

public class Alliance {

	@Getter
	private Color one, two;

	public Alliance(Color one, Color two) {

		this.one = one;
		this.two = two;

		System.out.println("Alliance::" + one.getName() + ", " + two.getName());
	}

	public Color getOtherColor(Color color) {

		if (color.equals(one)) {

			return two;

		} else if (color.equals(two)) {

			return one;
		}

		throw new RuntimeException(color.getName() + " not part of this alliance!");
	}

	public boolean isInAlliance(Color color) {

		return color.equals(one) || color.equals(two);
	}
	
	public Alliance getOtherAlliance() {

		ArrayList<Color> otherColors = new ArrayList<Color>();

		for (Color color : Color.getAll()) {

			if (!isInAlliance(color)) {

				otherColors.add(color);
			}
		}

		return new Alliance(otherColors.get(0), otherColors.get(1));
	}

}
