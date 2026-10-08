package org.bamboomy.c44.react.board;

import org.bamboomy.c44.react.jsp.Game;
import org.bamboomy.c44.react.jsp.MetaJudge;
import org.bamboomy.c44.react.player.Color;
import org.bamboomy.c44.react.player.Player;

import lombok.Getter;
import lombok.Setter;

public class Judge4Robots extends Judge {

	private static final String ADJUCATING = "ADJUCATING", CONTINUE = "CONTINUE!!!", WAITING = "WAITING",
			STOP = "STOP!!!", LAST_MAN_STANDING = "LAST_MAN_STANDING", FINISHED = "FINISHED";

	private GameMaster gameMaster;

	@Getter
	private String adjucation = WAITING;

	@Getter
	private boolean deciding = false, decided = false;

	private String matedPlayers = "";

	@Getter
	private String result = "";

	@Setter
	private Game game;

	@Setter
	private MetaJudge metaJudge;

	private String resultAllicance = "The Blue-Yellow Alliance<br/>has won!!!";

	public Judge4Robots(GameMaster gameMaster) {

		this.gameMaster = gameMaster;
	}

	public void reset() {

		deciding = decided = false;

		adjucation = WAITING;
	}

	public void addMatedPlayer(Player player) {

		if (!matedPlayers.equalsIgnoreCase("")) {

			matedPlayers += ",";
		}

		matedPlayers += player.getColor().getName() + " is mate";

		if (player.getColor().equals(Color.BLUE) || player.getColor().equals(Color.YELLOW)) {

			resultAllicance = "The Green-Red Alliance<br/>has won!!!";
		}
	}

	private void finish() {

		gameMaster.setFinished(true);

		gameMaster.setResult(resultAllicance);
		game.setResult(resultAllicance);

		if (resultAllicance.contains("Blue")) {

			game.setImage("checkmark.jpg");

		} else {

			game.setImage("cross.jpg");
		}

		game.setStatus("decided.");

		metaJudge.nextGame(resultAllicance);
	}

	public void checkForMate() {

		if (!deciding && gameMaster.currentPlayerIsMate()) {

			addMatedPlayer(gameMaster.getCurrentPlayer());

			if (gameMaster.sameAllianceMatedAndCurrentPlayer()) {

				finish();

			} else {

				adjucation = ADJUCATING;

				deciding = true;

				(new Thread(new Decision())).start();
			}
		}
	}

	@Override
	public void addResultToGame() {

		result += matedPlayers + " -> the game stops...";

		game.setResult(result);
	}

	private class Decision implements Runnable {

		@Override
		public void run() {

			try {
				Thread.sleep(5_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			adjucation = STOP;

			addResultToGame();

			// gameMaster.kamikaze();

			finish();

			decided = true;
		}
	}

	@Override
	public String getTemporaryResult() {

		return null;
	}
}
