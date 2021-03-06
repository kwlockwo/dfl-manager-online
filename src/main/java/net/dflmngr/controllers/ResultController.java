package net.dflmngr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.dflmngr.model.web.Results;
import net.dflmngr.model.web.RoundMenu;
import net.dflmngr.model.web.TeamResults;
import net.dflmngr.services.ResultService;

@Controller
public class ResultController {
	
	private final String RESULTS_VIEW = "results";

	private final ResultService resultService;
	
	@Autowired
	public ResultController(ResultService resultService) {
		this.resultService = resultService;
	}
	
    @ModelAttribute("module")
    public String module() {
        return "results";
    }
	
    
	@RequestMapping(value = "/results/{round}/{game}", method = RequestMethod.GET, produces = "text/html")
	public String results(@PathVariable int round, @PathVariable int game, Model model) {
		Results results = resultService.getResults(round, game);
		model.addAttribute("results", results);
		
		TeamResults team = results.getHomeTeam();
		if(team.getEmgInd() != null) {
			if(team.getEmgInd().equals("*")) {
				model.addAttribute("homeEmgMessage", "results.emg.star");
			} else if(team.getEmgInd().equals("**")) {
				model.addAttribute("homeEmgMessage", "results.emg.doublestar");
			} else if(team.getEmgInd().equals("*/**")) {
				model.addAttribute("homeEmgMessage", "results.emg.tripplestar");
			}
		}
		
		team = results.getAwayTeam();
		if(team.getEmgInd() != null) {
			if(team.getEmgInd().equals("*")) {
				model.addAttribute("awayEmgMessage", "results.emg.star");
			} else if(team.getEmgInd().equals("**")) {
				model.addAttribute("awayEmgMessage", "results.emg.doublestar");
			} else if(team.getEmgInd().equals("*/**")) {
				model.addAttribute("awayEmgMessage", "results.emg.tripplestar");
			}
		}
		
		List<RoundMenu> roundsMenu = resultService.getMenu(round, game);
		model.addAttribute("menu", roundsMenu);
		
		return RESULTS_VIEW;
	}
	
	@RequestMapping(value = "/results", method = RequestMethod.GET, produces = "text/html")
	public String currentResults(Model model) {
		Results results = resultService.getCurrentResults();
		model.addAttribute("results", results);
		
		TeamResults team = results.getHomeTeam();
		if(team.getEmgInd() != null) {
			if(team.getEmgInd().equals("*")) {
				model.addAttribute("homeEmgMessage", "results.emg.star");
			} else if(team.getEmgInd().equals("**")) {
				model.addAttribute("homeEmgMessage", "results.emg.doublestar");
			} else if(team.getEmgInd().equals("*/**")) {
				model.addAttribute("homeEmgMessage", "results.emg.tripplestar");
			}
		}
		
		team = results.getAwayTeam();
		if(team.getEmgInd() != null) {
			if(team.getEmgInd().equals("*")) {
				model.addAttribute("awayEmgMessage", "results.emg.star");
			} else if(team.getEmgInd().equals("**")) {
				model.addAttribute("awayEmgMessage", "results.emg.doublestar");
			} else if(team.getEmgInd().equals("*/**")) {
				model.addAttribute("awayEmgMessage", "results.emg.tripplestar");
			}
		}
		
		List<RoundMenu> roundsMenu = resultService.getMenu(results.getRound(), results.getGame());
		model.addAttribute("menu", roundsMenu);
		
		return RESULTS_VIEW;
	}

}
