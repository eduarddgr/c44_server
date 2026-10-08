package org.bamboomy.c44.react.jsp;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.Setter;

@Controller
@RequestMapping("/book")
public class BookController {

	public static ArrayList<Game> books = new ArrayList<>();

	@Setter
	public static String challenger = "FirstBot", challengee = "SecondBot";

	public static MetaJudge metaJudge;

	@GetMapping("/viewBooks")
	public String viewBooks(Model model) {
		model.addAttribute("books", books);
		model.addAttribute("challenger", challenger);
		model.addAttribute("challengee", challengee);
		model.addAttribute("metaJudge", metaJudge);
		return "view-books";
	}
}
