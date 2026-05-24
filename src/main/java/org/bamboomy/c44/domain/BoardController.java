
/**
	Copyright 2020 Sander Theetaert

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

package org.bamboomy.c44.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bamboomy.c44.react.board.GameMaster;

import lombok.Getter;

public class BoardController {

	private static BoardController single_instance = null;

	@Getter
	public static final Map<String, GameMaster> GAMEZ;

	@Getter
	public static final Map<String, GameMaster> GAMEZ_BY_ROBOTHASH;

	private static final int MAX_ENTRIES = 7 * 1000;

	static {

		Map<String, GameMaster> innerCache = new LinkedHashMap<String, GameMaster>(MAX_ENTRIES + 1, .75F, true) {

			private static final long serialVersionUID = 1L;

			// This method is called just after a new entry has been added
			public boolean removeEldestEntry(Map.Entry eldest) {

				boolean remove = size() > MAX_ENTRIES;

				System.out.println("cache removal? -> " + remove);

				return remove;
			}
		};

		GAMEZ = (Map<String, GameMaster>) Collections.synchronizedMap(innerCache);

		innerCache = new LinkedHashMap<String, GameMaster>(MAX_ENTRIES + 1, .75F, true) {

			private static final long serialVersionUID = 1L;

			// This method is called just after a new entry has been added
			public boolean removeEldestEntry(Map.Entry eldest) {

				boolean remove = size() > MAX_ENTRIES;

				System.out.println("cache removal? -> " + remove);

				return remove;
			}
		};

		GAMEZ_BY_ROBOTHASH = (Map<String, GameMaster>) Collections.synchronizedMap(innerCache);
	}

	private BoardController() {
	}

	public static synchronized BoardController getInstance() {

		if (single_instance == null) {
			single_instance = new BoardController();
		}

		return single_instance;
	}

	public synchronized GameMaster getGameMaster(String hash) {

		GameMaster result = GAMEZ.get(hash);

		if (GAMEZ.get(hash) == null) {

			result = new GameMaster(hash);

			GAMEZ.put(hash, result);
		}

		return result;
	}

	public GameMaster getGameMasterByPlayerHash(String hash) {

		Iterator<GameMaster> boardz = GAMEZ.values().iterator();

		while (boardz.hasNext()) {

			GameMaster board = boardz.next();

			/*
			 * if (board.hasHash(hash)) {
			 * 
			 * return board; }
			 * 
			 */
		}

		return null;
	}

	public void putGameMaster(String gameHash, String robotHash) {

		GAMEZ_BY_ROBOTHASH.put(robotHash, GAMEZ.get(gameHash));
	}

	public GameMaster getGameMasterFromRobotHash(String robotHash) {

		return GAMEZ_BY_ROBOTHASH.get(robotHash);
	}

	public String[] getInitinRobotHashes() {

		ArrayList<String> initingRobotHashes = new ArrayList<String>();

		for (String key : GAMEZ_BY_ROBOTHASH.keySet()) {

			if (GAMEZ_BY_ROBOTHASH.get(key).isIniting()) {

				initingRobotHashes.add(key);
			}
		}

		return (String[]) initingRobotHashes.toArray(new String[0]);
	}
}
