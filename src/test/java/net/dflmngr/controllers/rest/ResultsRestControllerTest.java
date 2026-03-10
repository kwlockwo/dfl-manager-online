package net.dflmngr.controllers.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import net.dflmngr.model.web.Results;
import net.dflmngr.model.web.TeamResults;
import net.dflmngr.services.ResultService;

@WebMvcTest(ResultsRestController.class)
class ResultsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultService resultService;

    private Results makeResults(int round, int game, String homeCode, String awayCode) {
        TeamResults home = new TeamResults();
        home.setTeamCode(homeCode);
        home.setTeamName("Home Team");
        home.setScore(450);

        TeamResults away = new TeamResults();
        away.setTeamCode(awayCode);
        away.setTeamName("Away Team");
        away.setScore(380);

        Results results = new Results();
        results.setRound(round);
        results.setGame(game);
        results.setHomeTeam(home);
        results.setAwayTeam(away);
        return results;
    }

    @Test
    void getResults_returns200WithJsonContentType() throws Exception {
        when(resultService.getResults(1, 1)).thenReturn(makeResults(1, 1, "AAA", "BBB"));

        mockMvc.perform(get("/results/1/1").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getResults_returnsRoundAndGame() throws Exception {
        when(resultService.getResults(2, 3)).thenReturn(makeResults(2, 3, "CCC", "DDD"));

        mockMvc.perform(get("/results/2/3").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.round").value(2))
            .andExpect(jsonPath("$.game").value(3));
    }

    @Test
    void getResults_returnsTeamDetails() throws Exception {
        when(resultService.getResults(1, 1)).thenReturn(makeResults(1, 1, "AAA", "BBB"));

        mockMvc.perform(get("/results/1/1").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.homeTeam.teamCode").value("AAA"))
            .andExpect(jsonPath("$.homeTeam.score").value(450))
            .andExpect(jsonPath("$.awayTeam.teamCode").value("BBB"))
            .andExpect(jsonPath("$.awayTeam.score").value(380));
    }

    @Test
    void getResults_pathVariablesPassedToService() throws Exception {
        when(resultService.getResults(5, 2)).thenReturn(makeResults(5, 2, "AAA", "BBB"));

        mockMvc.perform(get("/results/5/2").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.round").value(5))
            .andExpect(jsonPath("$.game").value(2));
    }
}
