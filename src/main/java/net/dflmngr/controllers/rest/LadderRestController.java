package net.dflmngr.controllers.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dflmngr.model.web.Ladder;
import net.dflmngr.services.LadderService;

@RestController
public class LadderRestController {

	private static final Logger logger = LoggerFactory.getLogger(LadderRestController.class);

	private final LadderService ladderService;

	public LadderRestController(LadderService ladderService) {
		this.ladderService = ladderService;
	}

	@GetMapping(value = "/ladder", produces = "application/json")
	public List<Ladder> ladder() {
		logger.info("Getting ladder");
		return ladderService.getLadder();
	}

	@GetMapping(value = "/ladder/live", produces = "application/json")
	public List<Ladder> liveLadder() {
		logger.info("Getting live ladder");
		return ladderService.getLiveLadder();
	}
}
