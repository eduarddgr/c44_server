package org.bamboomy.c44.react.board;

import org.bamboomy.c44.react.player.Player;

public abstract class Judge {

	abstract void reset();

	abstract void addMatedPlayer(Player player);

	abstract void checkForMate();

	abstract void addResultToGame();
	
	public abstract String getResult();

	public boolean isDeciding() {

		return false;
	}

	public boolean isDecided() {

		return false;
	}

	public String getAdjucation() {

		return null;
	}
	
	public abstract String getTemporaryResult();
}
