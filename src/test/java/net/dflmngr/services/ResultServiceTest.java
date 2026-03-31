package net.dflmngr.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.dflmngr.model.entities.AflPlayer;
import net.dflmngr.model.entities.DflFixture;
import net.dflmngr.model.entities.DflPlayer;
import net.dflmngr.model.entities.DflPlayerPredictedScores;
import net.dflmngr.model.entities.DflPlayerScores;
import net.dflmngr.model.entities.DflSelectedPlayer;
import net.dflmngr.model.entities.DflTeam;
import net.dflmngr.model.entities.DflTeamPredictedScores;
import net.dflmngr.model.entities.DflTeamScores;
import net.dflmngr.model.entities.Globals;
import net.dflmngr.model.entities.RawPlayerStats;
import net.dflmngr.model.entities.keys.DflFixturePK;
import net.dflmngr.model.entities.keys.DflTeamPredictedScoresPK;
import net.dflmngr.model.entities.keys.DflTeamScoresPK;
import net.dflmngr.model.entities.keys.GlobalsPK;
import net.dflmngr.model.web.Results;
import net.dflmngr.repositories.AflPlayerRepository;
import net.dflmngr.repositories.DflFixtureRepository;
import net.dflmngr.repositories.DflPlayerPredictedScoresRepository;
import net.dflmngr.repositories.DflPlayerRepository;
import net.dflmngr.repositories.DflPlayerScoresRepository;
import net.dflmngr.repositories.DflSelectedPlayerRepository;
import net.dflmngr.repositories.DflTeamPredictedScoresRepository;
import net.dflmngr.repositories.DflTeamRepository;
import net.dflmngr.repositories.DflTeamScoresRepository;
import net.dflmngr.repositories.GlobalsRespository;
import net.dflmngr.repositories.RawPlayerStatsRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ResultServiceTest {

    @Mock private DflFixtureRepository dflFixtureRepository;
    @Mock private DflTeamRepository dflTeamRepository;
    @Mock private DflPlayerRepository dflPlayerRepository;
    @Mock private AflPlayerRepository aflPlayerRepository;
    @Mock private DflSelectedPlayerRepository dflSelectedPlayerRepository;
    @Mock private RawPlayerStatsRepository rawPlayerStatsRepository;
    @Mock private DflPlayerScoresRepository dflPlayerScoresRepository;
    @Mock private DflPlayerPredictedScoresRepository dflPlayerPredictedScoresRepository;
    @Mock private DflTeamScoresRepository dflTeamScoresRepository;
    @Mock private DflTeamPredictedScoresRepository dflTeamPredictedScoresRepository;
    @Mock private GlobalsRespository globalsRespository;

    @InjectMocks
    private ResultService resultService;

    private DflTeam homeTeam;
    private DflTeam awayTeam;
    private DflFixture fixture;
    private DflSelectedPlayer selectedPlayer;
    private DflPlayer player;
    private AflPlayer aflPlayer;
    private RawPlayerStats rawStats;
    private DflPlayerScores playerScores;
    private DflPlayerPredictedScores playerPredictedScores;
    private DflTeamScores teamScores;
    private DflTeamPredictedScores teamPredictedScores;

    @BeforeEach
    void setUp() {
        homeTeam = new DflTeam("AAA", "Team Alpha", "Alpha", "Coach A", "Ground A", "Red", "a@test.com");
        awayTeam = new DflTeam("BBB", "Team Beta", "Beta", "Coach B", "Ground B", "Blue", "b@test.com");

        fixture = new DflFixture(1, 1, "AAA", "BBB");

        selectedPlayer = new DflSelectedPlayer();
        selectedPlayer.setRound(1);
        selectedPlayer.setPlayerId(101);
        selectedPlayer.setTeamPlayerId(1);
        selectedPlayer.setTeamCode("AAA");
        selectedPlayer.setScoreUsed(true);
        selectedPlayer.setDnp(false);
        selectedPlayer.setHasPlayed(true);
        selectedPlayer.setEmergency(0);

        player = new DflPlayer();
        player.setPlayerId(101);
        player.setFirstName("John");
        player.setLastName("Smith");
        player.setInitial("");
        player.setPosition("MID");

        aflPlayer = new AflPlayer();
        aflPlayer.setPlayerId("afl-101");
        aflPlayer.setDflPlayerId(101);
        aflPlayer.setTeamId("RICH");
        aflPlayer.setJumperNo(5);

        rawStats = new RawPlayerStats();
        rawStats.setKicks(10);
        rawStats.setHandballs(8);
        rawStats.setDisposals(18);
        rawStats.setMarks(5);
        rawStats.setHitouts(0);
        rawStats.setFreesFor(1);
        rawStats.setFreesAgainst(2);
        rawStats.setTackles(4);
        rawStats.setGoals(2);
        rawStats.setBehinds(1);
        rawStats.setScrapingStatus("done");

        playerScores = new DflPlayerScores();
        playerScores.setPlayerId(101);
        playerScores.setRound(1);
        playerScores.setScore(85);

        playerPredictedScores = new DflPlayerPredictedScores();
        playerPredictedScores.setPlayerId(101);
        playerPredictedScores.setRound(1);
        playerPredictedScores.setPredictedScore(70);

        teamScores = new DflTeamScores();
        teamScores.setTeamCode("AAA");
        teamScores.setRound(1);
        teamScores.setScore(450);

        teamPredictedScores = new DflTeamPredictedScores();
        teamPredictedScores.setTeamCode("AAA");
        teamPredictedScores.setRound(1);
        teamPredictedScores.setPredictedScore(400);
    }

    private void stubFixtureAndTeams() {
        when(dflFixtureRepository.findById(any(DflFixturePK.class))).thenReturn(Optional.of(fixture));
        when(dflTeamRepository.findById("AAA")).thenReturn(Optional.of(homeTeam));
        when(dflTeamRepository.findById("BBB")).thenReturn(Optional.of(awayTeam));
    }

    private void stubEmptyTeam(String teamCode) {
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, teamCode)).thenReturn(List.of());
        DflTeamPredictedScores predicted = new DflTeamPredictedScores();
        predicted.setTeamCode(teamCode);
        predicted.setRound(1);
        predicted.setPredictedScore(0);
        lenient().when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        lenient().when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(predicted));
    }

    private void stubPlayerForTeam(String teamCode) {
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, teamCode)).thenReturn(List.of(selectedPlayer));
        when(dflPlayerRepository.findByPlayerIdIn(anyList())).thenReturn(List.of(player));
        when(aflPlayerRepository.findByDflPlayerIdIn(anyList())).thenReturn(List.of(aflPlayer));
        when(rawPlayerStatsRepository.findByRoundAndTeamIn(anyInt(), anyList())).thenReturn(List.of(rawStats));
        rawStats.setTeam("RICH");
        rawStats.setJumperNo(5);
        when(dflPlayerScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of(playerScores));
        when(dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of(playerPredictedScores));
    }

    @Test
    void getResults_setsRoundAndGame() {
        stubFixtureAndTeams();
        stubEmptyTeam("AAA");
        stubEmptyTeam("BBB");

        Results result = resultService.getResults(1, 1);

        assertThat(result.getRound()).isEqualTo(1);
        assertThat(result.getGame()).isEqualTo(1);
    }

    @Test
    void getResults_setsTeamNames() {
        stubFixtureAndTeams();
        stubEmptyTeam("AAA");
        stubEmptyTeam("BBB");

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getTeamName()).isEqualTo("Team Alpha");
        assertThat(result.getAwayTeam().getTeamName()).isEqualTo("Team Beta");
    }

    @Test
    void getResults_setsTeamScoreWhenPresent() {
        stubFixtureAndTeams();

        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "AAA")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class)))
            .thenReturn(Optional.of(teamScores))
            .thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class)))
            .thenReturn(Optional.of(teamPredictedScores))
            .thenReturn(Optional.of(new DflTeamPredictedScores()));

        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getScore()).isEqualTo(450);
    }

    @Test
    void getResults_setsScoreZeroWhenNoTeamScore() {
        stubFixtureAndTeams();
        stubEmptyTeam("AAA");
        stubEmptyTeam("BBB");

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getScore()).isZero();
    }

    @Test
    void getResults_returnsEmptyResultWhenFixtureNotFound() {
        when(dflFixtureRepository.findById(any(DflFixturePK.class))).thenReturn(Optional.empty());

        Results result = resultService.getResults(1, 1);

        assertThat(result.getRound()).isEqualTo(1);
        assertThat(result.getGame()).isEqualTo(1);
        assertThat(result.getHomeTeam()).isNull();
        assertThat(result.getAwayTeam()).isNull();
    }

    @Test
    void getResults_playerNameWithoutInitial() {
        stubFixtureAndTeams();
        stubPlayerForTeam("AAA");

        DflTeamPredictedScores awayPredicted = new DflTeamPredictedScores();
        awayPredicted.setTeamCode("BBB");
        awayPredicted.setRound(1);
        awayPredicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(awayPredicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getPlayers()).hasSize(1);
        assertThat(result.getHomeTeam().getPlayers().get(0).getName()).isEqualTo("John Smith");
    }

    @Test
    void getResults_playerNameWithInitial() {
        player.setInitial("A");
        stubFixtureAndTeams();
        stubPlayerForTeam("AAA");

        DflTeamPredictedScores awayPredicted = new DflTeamPredictedScores();
        awayPredicted.setRound(1);
        awayPredicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(awayPredicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getPlayers().get(0).getName()).isEqualTo("John A. Smith");
    }

    @Test
    void getResults_playerStatsPopulatedFromRawStats() {
        stubFixtureAndTeams();
        stubPlayerForTeam("AAA");

        DflTeamPredictedScores awayPredicted = new DflTeamPredictedScores();
        awayPredicted.setRound(1);
        awayPredicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(awayPredicted));

        Results result = resultService.getResults(1, 1);

        var stats = result.getHomeTeam().getPlayers().get(0).getStats();
        assertThat(stats.getKicks()).isEqualTo(10);
        assertThat(stats.getHandballs()).isEqualTo(8);
        assertThat(stats.getDisposals()).isEqualTo(18);
        assertThat(stats.getGoals()).isEqualTo(2);
        assertThat(stats.getScore()).isEqualTo(85);
        assertThat(stats.getPredictedScore()).isEqualTo(70);
        assertThat(stats.getTrend()).isEqualTo(15);
    }

    @Test
    void getResults_playerPredictedScoreDefaultsTo25WhenMissing() {
        stubFixtureAndTeams();

        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "AAA")).thenReturn(List.of(selectedPlayer));
        when(dflPlayerRepository.findByPlayerIdIn(anyList())).thenReturn(List.of(player));
        when(aflPlayerRepository.findByDflPlayerIdIn(anyList())).thenReturn(List.of(aflPlayer));
        when(rawPlayerStatsRepository.findByRoundAndTeamIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());

        DflTeamPredictedScores awayPredicted = new DflTeamPredictedScores();
        awayPredicted.setRound(1);
        awayPredicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(awayPredicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getPlayers().get(0).getStats().getPredictedScore()).isEqualTo(25);
    }

    @Test
    void getResults_emergencyNotInPlayersList() {
        DflSelectedPlayer emg = new DflSelectedPlayer();
        emg.setRound(1);
        emg.setPlayerId(202);
        emg.setTeamPlayerId(2);
        emg.setTeamCode("AAA");
        emg.setScoreUsed(false);
        emg.setDnp(false);
        emg.setEmergency(1);

        DflPlayer emgPlayer = new DflPlayer();
        emgPlayer.setPlayerId(202);
        emgPlayer.setFirstName("Jane");
        emgPlayer.setLastName("Doe");
        emgPlayer.setInitial("");
        emgPlayer.setPosition("FWD");

        AflPlayer emgAflPlayer = new AflPlayer();
        emgAflPlayer.setDflPlayerId(202);
        emgAflPlayer.setTeamId("MELB");
        emgAflPlayer.setJumperNo(7);

        stubFixtureAndTeams();
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "AAA")).thenReturn(List.of(emg));
        when(dflPlayerRepository.findByPlayerIdIn(anyList())).thenReturn(List.of(emgPlayer));
        when(aflPlayerRepository.findByDflPlayerIdIn(anyList())).thenReturn(List.of(emgAflPlayer));
        when(rawPlayerStatsRepository.findByRoundAndTeamIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());

        DflTeamPredictedScores predicted = new DflTeamPredictedScores();
        predicted.setRound(1);
        predicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(predicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getPlayers()).isEmpty();
        assertThat(result.getHomeTeam().getEmergencies()).hasSize(1);
    }

    @Test
    void getResults_emgIndStarWhenSingleEmgUsed() {
        DflSelectedPlayer emg = new DflSelectedPlayer();
        emg.setRound(1);
        emg.setPlayerId(202);
        emg.setTeamCode("AAA");
        emg.setScoreUsed(false);
        emg.setReplacementInd("*");
        emg.setEmergency(1);

        DflPlayer emgPlayer = new DflPlayer();
        emgPlayer.setPlayerId(202);
        emgPlayer.setFirstName("Jane");
        emgPlayer.setLastName("Doe");
        emgPlayer.setInitial("");

        AflPlayer emgAflPlayer = new AflPlayer();
        emgAflPlayer.setDflPlayerId(202);
        emgAflPlayer.setTeamId("MELB");
        emgAflPlayer.setJumperNo(7);

        stubFixtureAndTeams();
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "AAA")).thenReturn(List.of(emg));
        when(dflPlayerRepository.findByPlayerIdIn(anyList())).thenReturn(List.of(emgPlayer));
        when(aflPlayerRepository.findByDflPlayerIdIn(anyList())).thenReturn(List.of(emgAflPlayer));
        when(rawPlayerStatsRepository.findByRoundAndTeamIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());

        DflTeamPredictedScores predicted = new DflTeamPredictedScores();
        predicted.setRound(1);
        predicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(predicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getEmgInd()).isEqualTo("*");
    }

    @Test
    void getResults_emgIndDoubleStarWhenDoubleEmgUsed() {
        DflSelectedPlayer emg = new DflSelectedPlayer();
        emg.setRound(1);
        emg.setPlayerId(202);
        emg.setTeamCode("AAA");
        emg.setScoreUsed(false);
        emg.setReplacementInd("**");
        emg.setEmergency(1);

        DflPlayer emgPlayer = new DflPlayer();
        emgPlayer.setPlayerId(202);
        emgPlayer.setFirstName("Jane");
        emgPlayer.setLastName("Doe");
        emgPlayer.setInitial("");

        AflPlayer emgAflPlayer = new AflPlayer();
        emgAflPlayer.setDflPlayerId(202);
        emgAflPlayer.setTeamId("MELB");
        emgAflPlayer.setJumperNo(7);

        stubFixtureAndTeams();
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "AAA")).thenReturn(List.of(emg));
        when(dflPlayerRepository.findByPlayerIdIn(anyList())).thenReturn(List.of(emgPlayer));
        when(aflPlayerRepository.findByDflPlayerIdIn(anyList())).thenReturn(List.of(emgAflPlayer));
        when(rawPlayerStatsRepository.findByRoundAndTeamIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());
        when(dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(anyInt(), anyList())).thenReturn(List.of());

        DflTeamPredictedScores predicted = new DflTeamPredictedScores();
        predicted.setRound(1);
        predicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(predicted));

        Results result = resultService.getResults(1, 1);

        assertThat(result.getHomeTeam().getEmgInd()).isEqualTo("**");
    }

    @Test
    void getCurrentResults_usesCurrentRoundFromGlobals() {
        Globals globals = new Globals();
        globals.setCode("currentRound");
        globals.setGroupCode("dflRef");
        globals.setValue("3");

        when(globalsRespository.findById(any(GlobalsPK.class))).thenReturn(Optional.of(globals));

        DflFixture round3Fixture = new DflFixture(3, 1, "AAA", "BBB");
        when(dflFixtureRepository.findById(any(DflFixturePK.class))).thenReturn(Optional.of(round3Fixture));
        when(dflTeamRepository.findById("AAA")).thenReturn(Optional.of(homeTeam));
        when(dflTeamRepository.findById("BBB")).thenReturn(Optional.of(awayTeam));
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(3, "AAA")).thenReturn(List.of());
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(3, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());

        DflTeamPredictedScores predicted = new DflTeamPredictedScores();
        predicted.setRound(3);
        predicted.setPredictedScore(0);
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(predicted));

        Results result = resultService.getCurrentResults();

        assertThat(result.getRound()).isEqualTo(3);
        assertThat(result.getGame()).isEqualTo(1);
    }

    @Test
    void getResults_trendIsScoreMinusPredictedWhenScorePresent() {
        stubFixtureAndTeams();
        stubPlayerForTeam("AAA");

        DflTeamPredictedScores awayPredicted = new DflTeamPredictedScores();
        awayPredicted.setRound(1);
        awayPredicted.setPredictedScore(0);
        when(dflSelectedPlayerRepository.findByRoundAndTeamCode(1, "BBB")).thenReturn(List.of());
        when(dflTeamScoresRepository.findById(any(DflTeamScoresPK.class))).thenReturn(Optional.empty());
        when(dflTeamPredictedScoresRepository.findById(any(DflTeamPredictedScoresPK.class))).thenReturn(Optional.of(awayPredicted));

        Results result = resultService.getResults(1, 1);

        // score=85, predictedScore=70 → trend = 85 - 70 = 15
        assertThat(result.getHomeTeam().getPlayers().get(0).getStats().getTrend()).isEqualTo(15);
    }
}
