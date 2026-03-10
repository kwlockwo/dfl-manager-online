package net.dflmngr.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.dflmngr.model.entities.DflLadder;
import net.dflmngr.model.entities.DflTeam;
import net.dflmngr.model.web.Ladder;
import net.dflmngr.repositories.DflLadderRepository;
import net.dflmngr.repositories.DflTeamRepository;

@ExtendWith(MockitoExtension.class)
class LadderServiceTest {

    @Mock
    private DflLadderRepository dflLadderRepository;

    @Mock
    private DflTeamRepository dflTeamRepository;

    @InjectMocks
    private LadderService ladderService;

    private DflTeam teamA;
    private DflTeam teamB;
    private DflTeam teamC;

    @BeforeEach
    void setUp() {
        teamA = new DflTeam("AAA", "Team Alpha", "Alpha", "Coach A", "Ground A", "Red", "a@test.com");
        teamB = new DflTeam("BBB", "Team Beta", "Beta", "Coach B", "Ground B", "Blue", "b@test.com");
        teamC = new DflTeam("CCC", "Team Gamma", "Gamma", "Coach C", "Ground C", "Green", "c@test.com");
    }

    private DflLadder makeLadder(int round, String teamCode, int pts, float percentage, int pointsFor) {
        DflLadder l = new DflLadder();
        l.setRound(round);
        l.setTeamCode(teamCode);
        l.setPts(pts);
        l.setPercentage(percentage);
        l.setPointsFor(pointsFor);
        l.setWins(pts / 4);
        l.setLosses(0);
        l.setDraws(0);
        l.setPointsAgainst(100);
        l.setAverageFor(90.0f);
        l.setAverageAgainst(80.0f);
        return l;
    }

    @Test
    void getLadder_returnsLadderWithTeamNames() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(makeLadder(1, "AAA", 4, 100.0f, 500)));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA));

        List<Ladder> result = ladderService.getLadder();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisplayName()).isEqualTo("Team Alpha");
        assertThat(result.get(0).getTeamCode()).isEqualTo("AAA");
        assertThat(result.get(0).getTeamUri()).isEqualTo("/teams/aaa");
    }

    @Test
    void getLadder_mapsAllFields() {
        DflLadder entity = makeLadder(3, "BBB", 8, 120.5f, 620);
        entity.setLosses(1);
        entity.setDraws(0);
        entity.setPointsAgainst(450);
        entity.setAverageFor(95.0f);
        entity.setAverageAgainst(75.0f);

        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(entity));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamB));

        Ladder result = ladderService.getLadder().get(0);

        assertThat(result.getRound()).isEqualTo(3);
        assertThat(result.getWins()).isEqualTo(2);
        assertThat(result.getLosses()).isEqualTo(1);
        assertThat(result.getDraws()).isEqualTo(0);
        assertThat(result.getPointsFor()).isEqualTo(620);
        assertThat(result.getPointsAgainst()).isEqualTo(450);
        assertThat(result.getPts()).isEqualTo(8);
        assertThat((double) result.getPercentage()).isCloseTo(120.5, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void getLadder_sortsByPtsDescending() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(
            makeLadder(1, "BBB", 4, 100.0f, 500),
            makeLadder(1, "AAA", 12, 100.0f, 500),
            makeLadder(1, "CCC", 8, 100.0f, 500)
        ));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB, teamC));

        List<Ladder> result = ladderService.getLadder();

        assertThat(result).extracting(Ladder::getTeamCode)
            .containsExactly("AAA", "CCC", "BBB");
    }

    @Test
    void getLadder_tiebreakByPercentage() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(
            makeLadder(1, "BBB", 8, 95.0f, 500),
            makeLadder(1, "AAA", 8, 110.0f, 500)
        ));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));

        List<Ladder> result = ladderService.getLadder();

        assertThat(result.get(0).getTeamCode()).isEqualTo("AAA");
        assertThat(result.get(1).getTeamCode()).isEqualTo("BBB");
    }

    @Test
    void getLadder_tiebreakByPointsForWhenPtsAndPercentageEqual() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(
            makeLadder(1, "BBB", 8, 100.0f, 480),
            makeLadder(1, "AAA", 8, 100.0f, 520)
        ));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));

        List<Ladder> result = ladderService.getLadder();

        assertThat(result.get(0).getTeamCode()).isEqualTo("AAA");
        assertThat(result.get(1).getTeamCode()).isEqualTo("BBB");
    }

    @Test
    void getLadder_returnsEmptyListWhenNoData() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of());

        assertThat(ladderService.getLadder()).isEmpty();
    }

    @Test
    void getLiveLadder_usesLiveRepository() {
        when(dflLadderRepository.findLiveDflLadder()).thenReturn(List.of(makeLadder(1, "AAA", 4, 100.0f, 500)));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA));

        List<Ladder> result = ladderService.getLiveLadder();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeamCode()).isEqualTo("AAA");
    }

    @Test
    void getLadder_teamUriIsLowercase() {
        when(dflLadderRepository.findCurrentDflLadder()).thenReturn(List.of(makeLadder(1, "AAA", 4, 100.0f, 500)));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA));

        Ladder result = ladderService.getLadder().get(0);

        assertThat(result.getTeamUri()).isEqualTo("/teams/aaa");
    }
}
