package org.bamboomy.c44.react.board;

import java.util.HashSet;

import org.bamboomy.c44.react.player.Player;

import lombok.Getter;

public class Judge4Human extends Judge {

	static final String BLUE = "The Blue-Yellow Alliance has won.", GREEN = "The Green-Red Alliance has won.",
			TIE = "It's a tie...";

	private GameMaster gameMaster;

	private HashSet<Player> matedPlayers = new HashSet<Player>();

	@Getter
	private String temporaryResult;

	@Getter
	private String result = "";

	public Judge4Human(GameMaster gameMaster) {

		this.gameMaster = gameMaster;
	}

	public void reset() {

	}

	public void addMatedPlayer(Player player) {

		matedPlayers.add(player);
	}

	@Override
	public void addResultToGame() {

		// we don't do any of that here...
	}

	@Override
	public void checkForMate() {

		if (gameMaster.currentPlayerIsMate()) {

			addMatedPlayer(gameMaster.getCurrentPlayer());

			if (matedPlayers.size() == 1) {

				colorResult();

				result += "_1";

			} else {

				if (gameMaster.sameAllianceMatedAndCurrentPlayer()) {

					colorResult();

					if (matedPlayers.size() == 2) {

						result += "_1.3";

					} else if (matedPlayers.size() == 3) {

						result += "_1.47";
					}

				} else if (matedPlayers.size() > 1) {

					temporaryResult = """
							Well, it's the two of you now...\n
							\n
							The last (wo)man (or bot) standing\n
							will decide the victory...
													""";

					result = TIE + "_0";
				}
			}
		}
	}

	void colorResult() {

		if ((!matedPlayers.contains(gameMaster.getPlayerz()[1]) && !matedPlayers.contains(gameMaster.getPlayerz()[3]))
				|| (matedPlayers.size() == 3 && (!matedPlayers.contains(gameMaster.getPlayerz()[1])
						|| !matedPlayers.contains(gameMaster.getPlayerz()[3])))) {

			result = BLUE;

		} else {

			result = GREEN;
		}
	}

}
