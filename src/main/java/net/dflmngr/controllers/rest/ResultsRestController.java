package net.dflmngr.controllers.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.dflmngr.model.web.Results;
import net.dflmngr.model.web.RoundMenu;
import net.dflmngr.services.ResultService;

@RestController
public class ResultsRestController {

	private final ResultService resultService;

	public ResultsRestController(ResultService resultService) {
		this.resultService = resultService;
	}

	@GetMapping(value = "/results/{round}/{game}", produces = "application/json")
	public Results getResults(@PathVariable int round, @PathVariable int game) {
		return resultService.getResults(round, game);
	}

	@GetMapping(value = "/results", produces = "application/json")
	public Results getCurrentResults() {
		return resultService.getCurrentResults();
	}

	@GetMapping(value = "/results/menu", produces = "application/json")
	public List<RoundMenu> getMenu(@RequestParam(required = false) Integer round,
	                               @RequestParam(required = false) Integer game) {
		if (round == null || game == null) {
			Results current = resultService.getCurrentResults();
			return resultService.getMenu(current.getRound(), current.getGame());
		}
		return resultService.getMenu(round, game);
	}
}
