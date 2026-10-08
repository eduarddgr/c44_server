package org.bamboomy.c44.react.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portal")
public class PortalController {

	public static Game game;

	@GetMapping("/viewBooks")
	public String viewBooks(Model model) {
		model.addAttribute("game", game);
		return "portal";
	}
}
