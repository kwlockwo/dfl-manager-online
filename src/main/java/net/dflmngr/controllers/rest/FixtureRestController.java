package net.dflmngr.controllers.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dflmngr.model.web.RoundFixtures;
import net.dflmngr.services.FixtureService;

@RestController
public class FixtureRestController {

	private static final Logger logger = LoggerFactory.getLogger(FixtureRestController.class);

	private final FixtureService fixtureService;

	public FixtureRestController(FixtureService fixtureService) {
		this.fixtureService = fixtureService;
	}

	@GetMapping(value = "/fixtures", produces = "application/json")
	public List<RoundFixtures> fixtures() {
		logger.info("Getting fixtures");
		return fixtureService.getFixtures();
	}
}
