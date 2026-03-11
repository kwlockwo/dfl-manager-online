package net.dflmngr.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.dflmngr.model.entities.DflFixture;
import net.dflmngr.model.entities.DflTeam;
import net.dflmngr.model.entities.DflTeamScores;
import net.dflmngr.model.entities.keys.DflTeamScoresPK;
import net.dflmngr.model.web.GameFixture;
import net.dflmngr.model.web.RoundFixtures;
import net.dflmngr.repositories.DflFixtureRepository;
import net.dflmngr.repositories.DflSelectedPlayerRepository;
import net.dflmngr.repositories.DflTeamRepository;
import net.dflmngr.repositories.DflTeamScoresRepository;

@ExtendWith(MockitoExtension.class)
class FixtureServiceTest {

    @Mock
    private DflFixtureRepository dflFixtureRepository;

    @Mock
    private DflTeamScoresRepository dflTeamScoresRepository;

    @Mock
    private DflTeamRepository dflTeamRepository;

    @Mock
    private DflSelectedPlayerRepository dflSelectedPlayerRepository;

    @InjectMocks
    private FixtureService fixtureService;

    private DflTeam teamA;
    private DflTeam teamB;
    private DflTeam teamC;
    private DflTeam teamD;

    @BeforeEach
    void setUp() {
        teamA = new DflTeam("AAA", "Team Alpha", "Alpha", "Coach A", "Ground A", "Red", "a@test.com");
        teamB = new DflTeam("BBB", "Team Beta", "Beta", "Coach B", "Ground B", "Blue", "b@test.com");
        teamC = new DflTeam("CCC", "Team Gamma", "Gamma", "Coach C", "Ground C", "Green", "c@test.com");
        teamD = new DflTeam("DDD", "Team Delta", "Delta", "Coach D", "Ground D", "Yellow", "d@test.com");
    }

    private DflTeamScores makeTeamScore(String teamCode, int round, int score) {
        DflTeamScores ts = new DflTeamScores();
        DflTeamScoresPK pk = new DflTeamScoresPK();
        pk.setTeamCode(teamCode);
        pk.setRound(round);
        ts.setTeamCode(teamCode);
        ts.setRound(round);
        ts.setScore(score);
        return ts;
    }

    @Test
    void getFixtures_groupsGamesByRound() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(
            new DflFixture(1, 1, "AAA", "BBB"),
            new DflFixture(1, 2, "CCC", "DDD"),
            new DflFixture(2, 1, "AAA", "CCC")
        )));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB, teamC, teamD));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("CCC", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("DDD", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 2)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("CCC", 2)).thenReturn(false);

        List<RoundFixtures> result = fixtureService.getFixtures();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRound()).isEqualTo(1);
        assertThat(result.get(0).getGames()).hasSize(2);
        assertThat(result.get(1).getRound()).isEqualTo(2);
        assertThat(result.get(1).getGames()).hasSize(1);
    }

    @Test
    void getFixtures_sortedByRoundThenGame() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(
            new DflFixture(1, 2, "CCC", "DDD"),
            new DflFixture(1, 1, "AAA", "BBB")
        )));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB, teamC, teamD));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("CCC", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("DDD", 1)).thenReturn(false);

        List<GameFixture> games = fixtureService.getFixtures().get(0).getGames();

        assertThat(games.get(0).getGame()).isEqualTo(1);
        assertThat(games.get(1).getGame()).isEqualTo(2);
    }

    @Test
    void getFixtures_setsScoresWhenBothTeamsHaveScores() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of(
            makeTeamScore("AAA", 1, 450),
            makeTeamScore("BBB", 1, 380)
        ));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getHomeTeamScore()).isEqualTo(450);
        assertThat(game.getAwayTeamScore()).isEqualTo(380);
    }

    @Test
    void getFixtures_noScoresWhenOnlyOneTeamHasScore() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of(makeTeamScore("AAA", 1, 450)));
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getHomeTeamScore()).isEqualTo(0);
        assertThat(game.getAwayTeamScore()).isEqualTo(0);
    }

    @Test
    void getFixtures_setsResultsUriWhenBothTeamsSelected() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(true);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(true);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getResultsUri()).isEqualTo("/results/1/1");
    }

    @Test
    void getFixtures_noResultsUriWhenOnlyHomeTeamSelected() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(true);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getResultsUri()).isNull();
    }

    @Test
    void getFixtures_setsDisplayNamesFromTeamShortName() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getHomeTeamDisplayName()).isEqualTo("Alpha");
        assertThat(game.getAwayTeamDisplayName()).isEqualTo("Beta");
    }

    @Test
    void getFixtures_returnsEmptyWhenNoFixtures() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>());
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of());

        assertThat(fixtureService.getFixtures()).isEmpty();
    }

    @Test
    void getFixtures_setsHomeAndAwayTeamCodes() {
        when(dflFixtureRepository.findAll()).thenReturn(new ArrayList<>(List.of(new DflFixture(1, 1, "AAA", "BBB"))));
        when(dflTeamScoresRepository.findAll()).thenReturn(List.of());
        when(dflTeamRepository.findAll()).thenReturn(List.of(teamA, teamB));
        when(dflSelectedPlayerRepository.selectedTeamExists("AAA", 1)).thenReturn(false);
        when(dflSelectedPlayerRepository.selectedTeamExists("BBB", 1)).thenReturn(false);

        GameFixture game = fixtureService.getFixtures().get(0).getGames().get(0);

        assertThat(game.getHomeTeam()).isEqualTo("AAA");
        assertThat(game.getAwayTeam()).isEqualTo("BBB");
        assertThat(game.getGame()).isEqualTo(1);
    }
}
