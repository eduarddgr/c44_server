package org.bamboomy.c44.react.jsp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bamboomy.c44.domain.BoardController;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

	public static final Map<String, Game> GAMEZ_BY_PLAYER;

	private static final int MAX_ENTRIES = 7 * 1000;

	private static GameController single_instance = null;

	static {

		Map<String, Game> innerCache = new LinkedHashMap<String, Game>(MAX_ENTRIES + 1, .75F, true) {

			private static final long serialVersionUID = 1L;

			// This method is called just after a new entry has been added
			public boolean removeEldestEntry(Map.Entry eldest) {

				boolean remove = size() > MAX_ENTRIES;

				System.out.println("cache removal? -> " + remove);

				return remove;
			}
		};

		GAMEZ_BY_PLAYER = (Map<String, Game>) Collections.synchronizedMap(innerCache);
	}

	public static synchronized GameController getInstance() {

		if (single_instance == null) {
			single_instance = new GameController();
		}

		return single_instance;
	}

	public void putGameByPlayerHash(String playerHash, Game game) {

		GAMEZ_BY_PLAYER.put(playerHash, game);
	}

	public Game getGameFromPlayerHash(String playerHash) {

		return GAMEZ_BY_PLAYER.get(playerHash);
	}

}
