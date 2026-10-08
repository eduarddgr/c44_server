package org.bamboomy.c44.react.jsp;

import java.util.HashMap;

import org.bamboomy.c44.domain.ColorsTaken;
import org.bamboomy.c44.react.player.Color;

import lombok.Data;
import lombok.Getter;

@Data
public class Game {

	private String md5;

	private String image;

	private String status;

	private String result;

	private int nbOfMovez;

	private ColorsTaken green, red, blue, yellow;

	@Getter
	private HashMap<String, Color> colorMap = new HashMap<String, Color>();

	@Getter
	private HashMap<String, ColorsTaken> colorsTakenMap = new HashMap<>();

	public Game(String md5, String image, String status, String result, int i, ColorsTaken green, ColorsTaken red,
			ColorsTaken blue, ColorsTaken yellow) {
		this.md5 = md5;
		this.image = image;
		this.status = status;
		this.result = result;
		this.nbOfMovez = i;

		this.green = green;
		this.red = red;
		this.blue = blue;
		this.yellow = yellow;

		colorMap.put(green.getJavaHash(), Color.GREEN);
		colorMap.put(red.getJavaHash(), Color.RED);
		colorMap.put(blue.getJavaHash(), Color.BLUE);
		colorMap.put(yellow.getJavaHash(), Color.YELLOW);

		colorsTakenMap.put(green.getJavaHash(), green);
		colorsTakenMap.put(red.getJavaHash(), red);
		colorsTakenMap.put(blue.getJavaHash(), blue);
		colorsTakenMap.put(yellow.getJavaHash(), yellow);
	}

	public void begin() {

		image = "done.gif";
		status = "In progress...";
	}

	public void incrementMove() {

		nbOfMovez++;
	}

	public String getGreenHash() {

		return green.getJavaHash();
	}
}
