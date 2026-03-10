package net.dflmngr.controllers.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import net.dflmngr.model.web.GameFixture;
import net.dflmngr.model.web.RoundFixtures;
import net.dflmngr.services.FixtureService;

@WebMvcTest(FixtureRestController.class)
class FixtureRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FixtureService fixtureService;

    private RoundFixtures makeRound(int round, String homeTeam, String awayTeam) {
        GameFixture game = new GameFixture();
        game.setGame(1);
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);
        game.setHomeTeamDisplayName(homeTeam + " FC");
        game.setAwayTeamDisplayName(awayTeam + " FC");

        RoundFixtures rf = new RoundFixtures();
        rf.setRound(round);
        rf.setGames(List.of(game));
        return rf;
    }

    @Test
    void getFixtures_returns200WithJsonContentType() throws Exception {
        when(fixtureService.getFixtures()).thenReturn(List.of());

        mockMvc.perform(get("/fixtures").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getFixtures_returnsRoundFixtures() throws Exception {
        when(fixtureService.getFixtures()).thenReturn(List.of(
            makeRound(1, "AAA", "BBB"),
            makeRound(2, "CCC", "DDD")
        ));

        mockMvc.perform(get("/fixtures").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].round").value(1))
            .andExpect(jsonPath("$[0].games[0].homeTeam").value("AAA"))
            .andExpect(jsonPath("$[0].games[0].awayTeam").value("BBB"))
            .andExpect(jsonPath("$[1].round").value(2));
    }

    @Test
    void getFixtures_returnsEmptyArray() throws Exception {
        when(fixtureService.getFixtures()).thenReturn(List.of());

        mockMvc.perform(get("/fixtures").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
