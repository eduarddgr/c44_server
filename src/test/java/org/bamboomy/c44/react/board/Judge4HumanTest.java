package org.bamboomy.c44.react.board;

import static org.junit.Assert.assertEquals;

import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.player.Player;
import org.junit.Test;

public class Judge4HumanTest {

	private GameMaster gameMaster;

	private Player[] players = new Player[4];

	private Judge4Human toTest;

	{
		gameMaster = new GameMaster("md5", false);

		players[0] = new Player(new ColorsTaken(null, null, null, null, null, null), gameMaster, null);
		players[1] = new Player(new ColorsTaken(null, null, null, null, null, null), gameMaster, null);
		players[2] = new Player(new ColorsTaken(null, null, null, null, null, null), gameMaster, null);
		players[3] = new Player(new ColorsTaken(null, null, null, null, null, null), gameMaster, null);

		gameMaster.setPlayerz(players);

		gameMaster.setMate(true);
	}

	@Test
	public void testOnlyRed() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[2]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.BLUE);
	}

	@Test
	public void testOnlyGreen() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[0]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.BLUE);
	}

	@Test
	public void testOnlyBlue() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[1]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.GREEN);
	}

	@Test
	public void testOnlyYellow() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[3]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.GREEN);
	}

	@Test
	public void testRedAndGreen() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[2]);
		toTest.addMatedPlayer(players[0]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.BLUE);
	}

	@Test
	public void testBlueAndYellow() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[1]);
		toTest.addMatedPlayer(players[3]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.GREEN);
	}

	@Test
	public void testGreenBlueAndRed() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[0]);
		toTest.addMatedPlayer(players[1]);
		toTest.addMatedPlayer(players[2]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.BLUE);
	}

	@Test
	public void testGreenBlueAndYellow() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[0]);
		toTest.addMatedPlayer(players[1]);
		toTest.addMatedPlayer(players[3]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.GREEN);
	}

	@Test
	public void testGreenRedAndYellow() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[0]);
		toTest.addMatedPlayer(players[2]);
		toTest.addMatedPlayer(players[3]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.BLUE);
	}

	@Test
	public void testBlueRedAndYellow() {

		toTest = new Judge4Human(gameMaster);

		toTest.addMatedPlayer(players[1]);
		toTest.addMatedPlayer(players[2]);
		toTest.addMatedPlayer(players[3]);

		toTest.colorResult();

		assertEquals(toTest.getResult(), toTest.GREEN);
	}

}
