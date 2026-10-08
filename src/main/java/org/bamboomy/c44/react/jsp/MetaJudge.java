package org.bamboomy.c44.react.jsp;

import java.util.HashMap;

import org.bamboomy.c44.rest.ColorController;

import lombok.Getter;

public class MetaJudge {

	private String[] botGameHashes;

	private HashMap<String, Game> gamez = new HashMap<String, Game>();

	private int currentIndex = 0;

	@Getter
	private int wins = 0, losses = 0, staleMate = 0;

	@Getter
	private boolean apt = false, fail = false;

	public MetaJudge(String[] botGameHashes) {

		this.botGameHashes = botGameHashes;
	}

	public void add(String md5, Game game) {

		gamez.put(md5, game);
	}

	public void nextGame(String result) {

		if (result.contains("Blue")) {

			wins++;

			if (wins + staleMate == 7) {

				apt = true;
			}

		} else {

			losses++;

			if (losses == 4) {

				fail = true;
			}
		}

		if (!fail && !apt) {

			currentIndex++;

			ColorController.goToNextGame();

			gamez.get(botGameHashes[currentIndex]).begin();

		} else if (fail) {

			System.out.println(
					"Failed: you'll need to work harder (or smarter) to succeed, further play is not required");

		} else {

			System.out.println("You did it, if this is the 5th time you are certainly apt!!!");
		}

	}
}
