package net.dflmngr.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
import net.dflmngr.model.web.GameMenu;
import net.dflmngr.model.web.PlayerStats;
import net.dflmngr.model.web.Results;
import net.dflmngr.model.web.RoundMenu;
import net.dflmngr.model.web.SelectedPlayer;
import net.dflmngr.model.web.TeamResults;
import net.dflmngr.repositories.AflPlayerRepository;
import net.dflmngr.repositories.DflFixtureRepository;
import net.dflmngr.repositories.DflPlayerPredictedScoresRepository;
import net.dflmngr.repositories.DflPlayerRepository;
import net.dflmngr.repositories.DflPlayerScoresRepository;
import net.dflmngr.repositories.DflSelectedPlayerRepository;
import net.dflmngr.repositories.DflTeamPredictedScoresRepository;
import net.dflmngr.repositories.DflTeamRepository;
import net.dflmngr.repositories.DflTeamScoresRepository;
import net.dflmngr.repositories.GlobalsRepository;
import net.dflmngr.repositories.RawPlayerStatsRepository;

@Service
public class ResultService {

	private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

	private final DflFixtureRepository dflFixtureRepository;
	private final DflTeamRepository dflTeamRepository;
	private final DflPlayerRepository dflPlayerRepository;
	private final AflPlayerRepository aflPlayerRepository;
	private final DflSelectedPlayerRepository dflSelectedPlayerRepository;
	private final RawPlayerStatsRepository rawPlayerStatsRepository;
	private final DflPlayerScoresRepository dflPlayerScoresRepository;
	private final DflPlayerPredictedScoresRepository dflPlayerPredictedScoresRepository;
	private final DflTeamScoresRepository dflTeamScoresRepository;
	private final DflTeamPredictedScoresRepository dflTeamPredictedScoresRepository;
	private final GlobalsRepository globalsRespository;
	
	public ResultService(DflFixtureRepository dflFixtureRepository, DflTeamRepository dflTeamRepository, DflPlayerRepository dflPlayerRepository, AflPlayerRepository aflPlayerRepository,
			             DflSelectedPlayerRepository dflSelectedPlayerRepository, RawPlayerStatsRepository rawPlayerStatsRepository, DflPlayerScoresRepository dflPlayerScoresRepository,
			             DflPlayerPredictedScoresRepository dflPlayerPredictedScoresRepository, DflTeamScoresRepository dflTeamScoresRepository,
			             DflTeamPredictedScoresRepository dflTeamPredictedScoresRepository, GlobalsRepository globalsRespository) {
		this.dflFixtureRepository = dflFixtureRepository;
		this.dflTeamRepository = dflTeamRepository;
		this.dflPlayerRepository = dflPlayerRepository;
		this.aflPlayerRepository = aflPlayerRepository;
		this.dflSelectedPlayerRepository = dflSelectedPlayerRepository;
		this.rawPlayerStatsRepository = rawPlayerStatsRepository;
		this.dflPlayerScoresRepository = dflPlayerScoresRepository;
		this.dflPlayerPredictedScoresRepository = dflPlayerPredictedScoresRepository;
		this.dflTeamScoresRepository = dflTeamScoresRepository;
		this.dflTeamPredictedScoresRepository = dflTeamPredictedScoresRepository;
		this.globalsRespository = globalsRespository;
	}
	
	public Results getResults(int round, int game) {

		Results results = new Results();
		results.setRound(round);
		results.setGame(game);

		try {
			DflFixture dflFixture = getFixture(round, game);
			String homeTeamCode = dflFixture.getHomeTeam();
			String awayTeamCode = dflFixture.getAwayTeam();

			List<DflSelectedPlayer> homeSelected = dflSelectedPlayerRepository.findByRoundAndTeamCode(round, homeTeamCode);
			List<DflSelectedPlayer> awaySelected = dflSelectedPlayerRepository.findByRoundAndTeamCode(round, awayTeamCode);

			List<DflSelectedPlayer> allSelected = new ArrayList<>();
			allSelected.addAll(homeSelected);
			allSelected.addAll(awaySelected);

			RoundData roundData = loadRoundData(round, allSelected);

			results.setHomeTeam(getTeamResults(round, homeTeamCode, homeSelected, roundData));
			results.setAwayTeam(getTeamResults(round, awayTeamCode, awaySelected, roundData));
		} catch (NoSuchElementException ex) {
			logger.error("Fixture not found for round={} game={}", round, game, ex);
		}

		return results;
	}

	private record RoundData(
		Map<Integer, DflPlayer> dflPlayers,
		Map<Integer, AflPlayer> aflPlayers,
		Map<String, RawPlayerStats> rawStats,
		Map<Integer, DflPlayerScores> playerScores,
		Map<Integer, DflPlayerPredictedScores> predictedScores
	) {}

	private RoundData loadRoundData(int round, List<DflSelectedPlayer> allSelected) {
		List<Integer> playerIds = allSelected.stream()
			.map(DflSelectedPlayer::getPlayerId)
			.collect(Collectors.toList());

		Map<Integer, DflPlayer> dflPlayers = dflPlayerRepository.findByPlayerIdIn(playerIds).stream()
			.collect(Collectors.toMap(DflPlayer::getPlayerId, p -> p));

		List<AflPlayer> aflPlayerList = aflPlayerRepository.findByDflPlayerIdIn(playerIds);
		Map<Integer, AflPlayer> aflPlayers = aflPlayerList.stream()
			.collect(Collectors.toMap(AflPlayer::getDflPlayerId, p -> p));

		List<String> aflTeamIds = aflPlayerList.stream()
			.map(AflPlayer::getTeamId)
			.distinct()
			.collect(Collectors.toList());
		Map<String, RawPlayerStats> rawStats = rawPlayerStatsRepository.findByRoundAndTeamIn(round, aflTeamIds).stream()
			.collect(Collectors.toMap(s -> s.getTeam() + ":" + s.getJumperNo(), s -> s, (existing, replacement) -> existing));

		Map<Integer, DflPlayerScores> playerScores = dflPlayerScoresRepository.findByRoundAndPlayerIdIn(round, playerIds).stream()
			.collect(Collectors.toMap(DflPlayerScores::getPlayerId, s -> s));

		Map<Integer, DflPlayerPredictedScores> predictedScores = dflPlayerPredictedScoresRepository.findByRoundAndPlayerIdIn(round, playerIds).stream()
			.collect(Collectors.toMap(DflPlayerPredictedScores::getPlayerId, s -> s));

		return new RoundData(dflPlayers, aflPlayers, rawStats, playerScores, predictedScores);
	}
	
	public Results getCurrentResults() {
		GlobalsPK globalsPK = new GlobalsPK();
		globalsPK.setCode("currentRound");
		globalsPK.setGroupCode("dflRef");
		
		Globals currentRoundGlobal = globalsRespository.findById(globalsPK)
				.orElseThrow(() -> new NoSuchElementException("Global config not found: code=currentRound groupCode=dflRef"));
		int currentRound = Integer.parseInt(currentRoundGlobal.getValue());
		return getResults(currentRound, 1);

	}
	
	public List<RoundMenu> getMenu(int round, int game) {
		
		List<RoundMenu> menu = new ArrayList<>();
		
		List<DflFixture> dflFixtures = dflFixtureRepository.findAll();
		
		Comparator<DflFixture> dflFixtureComparator = Comparator.comparingInt(DflFixture::getRound).thenComparingInt(DflFixture::getGame);
		dflFixtures.sort(dflFixtureComparator);
		
		int currentRound = 1;
		RoundMenu roundMenu = new RoundMenu();
		List<GameMenu> games = new ArrayList<>();
		Comparator<GameMenu> gamesComparator = Comparator.comparingInt(GameMenu::getGame);
		
		for(DflFixture dflFixture : dflFixtures) {
			boolean homeTeamSelected = dflSelectedPlayerRepository.selectedTeamExists(dflFixture.getHomeTeam(), dflFixture.getRound());
			boolean awayTeamSelected = dflSelectedPlayerRepository.selectedTeamExists(dflFixture.getAwayTeam(), dflFixture.getRound());
			
			if(homeTeamSelected || awayTeamSelected) {
				GameMenu gameMenu = new GameMenu();
				
				gameMenu.setGame(dflFixture.getGame());
				gameMenu.setHomeTeam(dflFixture.getHomeTeam());
				gameMenu.setAwayTeam(dflFixture.getAwayTeam());
				
				String resultsUri = "/results/" + dflFixture.getRound() + "/" + dflFixture.getGame();
				gameMenu.setResultsUri(resultsUri);
				
				gameMenu.setActive(dflFixture.getRound() == round && dflFixture.getGame() == game);
				
				if(currentRound == dflFixture.getRound()) {
					games.add(gameMenu);
				} else {
					games.sort(gamesComparator);
					
					roundMenu.setRound(currentRound);
					roundMenu.setGames(games);					
					roundMenu.setActive(currentRound == round);
					
					menu.add(roundMenu);
					
					roundMenu = new RoundMenu();
					games = new ArrayList<>();
					games.add(gameMenu);
					
					currentRound = dflFixture.getRound();				
				}
			}
		}
		
		if(!games.isEmpty()) {
			games.sort(gamesComparator);
			
			roundMenu.setRound(currentRound);
			roundMenu.setGames(games);			
			roundMenu.setActive(currentRound == round);

			menu.add(roundMenu);
		}
		
		Comparator<RoundMenu> roundsComparator = Comparator.comparingInt(RoundMenu::getRound);
		menu.sort(roundsComparator);
		
		return menu;
	}
	
	private TeamResults getTeamResults(int round, String teamCode, List<DflSelectedPlayer> selectedTeam, RoundData roundData) {

		TeamResults teamResults = new TeamResults();

		if(teamCode != null) {
			DflTeam team = dflTeamRepository.findById(teamCode)
					.orElseThrow(() -> new NoSuchElementException("Team not found: teamCode=" + teamCode));
			teamResults.setTeamCode(teamCode);
			teamResults.setTeamName(team.getName());

			List<SelectedPlayer> players = new ArrayList<>();
			List<SelectedPlayer> emergencies = new ArrayList<>();

			int currentPredictedScore = calculateTeamScore(selectedTeam, players, emergencies, roundData);

			teamResults.setPlayers(players);

			setEmgsInd(emergencies, teamResults);

			teamResults.setEmergencies(emergencies);

			DflTeamScoresPK dflTeamScoresPK = new DflTeamScoresPK();
			dflTeamScoresPK.setRound(round);
			dflTeamScoresPK.setTeamCode(teamCode);
			DflTeamScores dflTeamScore = dflTeamScoresRepository.findById(dflTeamScoresPK).orElse(null);

			DflTeamPredictedScoresPK dflTeamPredictedScoresPK = new DflTeamPredictedScoresPK();
			dflTeamPredictedScoresPK.setRound(round);
			dflTeamPredictedScoresPK.setTeamCode(teamCode);
			DflTeamPredictedScores dflTeamPredictedScore = dflTeamPredictedScoresRepository.findById(dflTeamPredictedScoresPK)
					.orElseThrow(() -> new NoSuchElementException("Predicted scores not found: round=" + round + " teamCode=" + teamCode));

			if(dflTeamScore != null) {
				teamResults.setScore(dflTeamScore.getScore());
			} else {
				teamResults.setScore(0);
			}

			teamResults.setCurrentPredictedScore(currentPredictedScore);
			teamResults.setPredictedScore(dflTeamPredictedScore.getPredictedScore());

			int trend = 0;
			if(currentPredictedScore == dflTeamPredictedScore.getPredictedScore()) {
				trend = teamResults.getScore() - dflTeamPredictedScore.getPredictedScore();
			} else {
				trend = currentPredictedScore - dflTeamPredictedScore.getPredictedScore();
			}
			teamResults.setTrend(trend);
		}

		return teamResults;
	}

	private int calculateTeamScore(List<DflSelectedPlayer> selectedTeam, List<SelectedPlayer> players, List<SelectedPlayer> emergencies, RoundData roundData) {
		int currentPredictedScore = 0;
		for(DflSelectedPlayer selectedPlayer : selectedTeam) {
			if(selectedPlayer.isScoreUsed()) {
				SelectedPlayer sp = getSelectedPlayer(selectedPlayer, roundData);
				players.add(sp);
				if(sp.isDnp()) {
					currentPredictedScore = currentPredictedScore + 0;
				} else {
					if(sp.getStats().getScore() == 0) {
						currentPredictedScore = currentPredictedScore + sp.getStats().getPredictedScore();
					} else {
						currentPredictedScore = currentPredictedScore + sp.getStats().getScore();
					}
				}
			} else {
				emergencies.add(getSelectedPlayer(selectedPlayer, roundData));
			}
		}

		return currentPredictedScore;
	}

	private SelectedPlayer getSelectedPlayer(DflSelectedPlayer selectedPlayer, RoundData roundData) {
		SelectedPlayer sp = new SelectedPlayer();

		sp.setPlayerId(selectedPlayer.getPlayerId());
		sp.setTeamPlayerId(selectedPlayer.getTeamPlayerId());

		DflPlayer player = roundData.dflPlayers().get(selectedPlayer.getPlayerId());

		if(player.getInitial() == null || player.getInitial().isEmpty()) {
			sp.setName(player.getFirstName() + " " + player.getLastName());
		} else {
			sp.setName(player.getFirstName() + " " + player.getInitial() + ". " + player.getLastName());
		}

		sp.setPosition(player.getPosition());
		sp.setHasPlayer(selectedPlayer.hasPlayed());
		sp.setScoreUsed(selectedPlayer.isScoreUsed());
		sp.setDnp(selectedPlayer.isDnp());
		sp.setReplacementInd(selectedPlayer.getReplacementInd());

		if(selectedPlayer.getReplacementInd() != null && selectedPlayer.getReplacementInd().equals("*")) {
			sp.setEmgSort(1);
		} else if(selectedPlayer.getReplacementInd() != null && selectedPlayer.getReplacementInd().equals("**")) {
			sp.setEmgSort(2);
		} else {
			sp.setEmgSort(selectedPlayer.isEmergency());
		}

		sp.setStats(getPlayerStats(selectedPlayer.getPlayerId(), roundData));

		return sp;
	}

	private void setEmgsInd(List<SelectedPlayer> emergencies, TeamResults teamResults) {
		boolean star = false;
		boolean doubleStar = false;

		for(SelectedPlayer emg : emergencies) {
			if(emg.getReplacementInd() != null) {
				if(emg.getReplacementInd().equals("*")) {
					star = true;
				} else if(emg.getReplacementInd().equals("**")) {
					doubleStar = true;
				}
			}
		}
		
		if(star && !doubleStar) {
			teamResults.setEmgInd("*");
		} 
		if(!star && doubleStar) {
			teamResults.setEmgInd("**");
		}
		if(star && doubleStar) {
			teamResults.setEmgInd("*/**");
		}
	}
	
	private PlayerStats getPlayerStats(int playerId, RoundData roundData) {
		PlayerStats playerStats = new PlayerStats();

		AflPlayer aflPlayer = roundData.aflPlayers().get(playerId);
		if(aflPlayer != null) {
			RawPlayerStats rawPlayerStats = roundData.rawStats().get(aflPlayer.getTeamId() + ":" + aflPlayer.getJumperNo());
			if(rawPlayerStats != null) {
				playerStats.setKicks(rawPlayerStats.getKicks());
				playerStats.setHandballs(rawPlayerStats.getHandballs());
				playerStats.setDisposals(rawPlayerStats.getDisposals());
				playerStats.setMarks(rawPlayerStats.getMarks());
				playerStats.setHitouts(rawPlayerStats.getHitouts());
				playerStats.setFreesFor(rawPlayerStats.getFreesFor());
				playerStats.setFreesAgainst(rawPlayerStats.getFreesAgainst());
				playerStats.setTackles(rawPlayerStats.getTackles());
				playerStats.setGoals(rawPlayerStats.getGoals());
				playerStats.setBehinds(rawPlayerStats.getBehinds());
				playerStats.setScrapingStatus(rawPlayerStats.getScrapingStatus());
			}
		}

		DflPlayerPredictedScores predictedScores = roundData.predictedScores().get(playerId);
		if(predictedScores != null) {
			playerStats.setPredictedScore(predictedScores.getPredictedScore());
		} else {
			playerStats.setPredictedScore(25);
		}

		DflPlayerScores dflPlayerScores = roundData.playerScores().get(playerId);
		int trend = 0;
		if(dflPlayerScores != null) {
			playerStats.setScore(dflPlayerScores.getScore());
			trend = dflPlayerScores.getScore() - playerStats.getPredictedScore();
		} else {
			trend = trend - playerStats.getPredictedScore();
		}

		playerStats.setTrend(trend);

		return playerStats;
	}
		
	private DflFixture getFixture(int round, int game) {
		DflFixturePK pk = new DflFixturePK();
		pk.setRound(round);
		pk.setGame(game);
		
		return dflFixtureRepository.findById(pk).orElseThrow();
	}
	
	

}
