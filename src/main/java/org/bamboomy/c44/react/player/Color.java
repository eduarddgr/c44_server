
/**
 * Copyright 2020 Sander Theetaert
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.bamboomy.c44.react.player;

import lombok.Getter;

import java.util.function.BiFunction;

@Getter
public enum Color {

    GREEN(0, "Green", "green", 'g', 1, 0, (x, y) -> 11 - x, (x, y) -> 11 - y),
    BLUE(1, "Blue", "blue", 'b', 0, -1, (x, y) -> 11 - y, (x, y) -> x),
    RED(2, "Red", "red", 'r', -1, 0, (x, y) -> x, (x, y) -> y),
    YELLOW(3, "Yellow", "yellow", 'y', 0, 1, (x, y) -> y, (x, y) -> 11 - x),
    DEAD(4, "Dead", "grey", 'd', 0, 0, (x, y) -> x, (x, y) -> y),
    NO_COLOR(-1, "", "", 'n', 0, 0, (x, y) -> x, (x, y) -> y);

    private final int seq, xDirection, yDirection;
    private final String name;
    private final String cssColor;
    private final char shortDebug;
    private final BiFunction<Integer, Integer, Integer> xMapping;
    private final BiFunction<Integer, Integer, Integer> yMapping;

    Color(int seq, String name, String cssColor, char shortDebug,
          int xDirection, int yDirection,
          BiFunction<Integer, Integer, Integer> xMapping, BiFunction<Integer, Integer, Integer> yMapping) {
        this.seq = seq;
        this.name = name;
        this.cssColor = cssColor;
        this.shortDebug = shortDebug;
        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.xMapping = xMapping;
        this.yMapping = yMapping;
    }

    public static Color getByName(String name) {

        for (Color p : Color.values()) {

            if (p.name.equalsIgnoreCase(name)) {

                return p;
            }
        }

        return null;
    }

    public static Color getBySeq(int seq) {

        for (Color p : Color.values()) {

            if (p.seq == seq) {

                return p;
            }
        }

        return null;
    }

    public static Color[] getAll() {

        Color[] colorz = new Color[4];

        for (int i = 0; i < 4; i++) {

            colorz[i] = Color.getBySeq(i);
        }

        return colorz;
    }

}
