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

import net.dflmngr.model.web.Ladder;
import net.dflmngr.services.LadderService;

@WebMvcTest(LadderRestController.class)
class LadderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LadderService ladderService;

    private Ladder makeLadder(String teamCode, String displayName, int pts) {
        Ladder l = new Ladder();
        l.setTeamCode(teamCode);
        l.setDisplayName(displayName);
        l.setPts(pts);
        l.setWins(pts / 4);
        l.setLosses(0);
        l.setDraws(0);
        l.setPointsFor(500);
        l.setPointsAgainst(400);
        l.setAverageFor(90.0f);
        l.setAverageAgainst(80.0f);
        l.setPercentage(125.0f);
        l.setRound(1);
        l.setTeamUri("/teams/" + teamCode.toLowerCase());
        return l;
    }

    @Test
    void getLadder_returns200WithJsonContentType() throws Exception {
        when(ladderService.getLadder()).thenReturn(List.of());

        mockMvc.perform(get("/ladder").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getLadder_returnsLadderEntries() throws Exception {
        when(ladderService.getLadder()).thenReturn(List.of(
            makeLadder("AAA", "Team Alpha", 12),
            makeLadder("BBB", "Team Beta", 8)
        ));

        mockMvc.perform(get("/ladder").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].teamCode").value("AAA"))
            .andExpect(jsonPath("$[0].displayName").value("Team Alpha"))
            .andExpect(jsonPath("$[0].pts").value(12))
            .andExpect(jsonPath("$[1].teamCode").value("BBB"))
            .andExpect(jsonPath("$[1].pts").value(8));
    }

    @Test
    void getLadder_returnsEmptyArray() throws Exception {
        when(ladderService.getLadder()).thenReturn(List.of());

        mockMvc.perform(get("/ladder").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
