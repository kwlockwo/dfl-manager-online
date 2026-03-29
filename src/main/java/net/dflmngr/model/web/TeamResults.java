package net.dflmngr.model.web;

import java.util.List;
import lombok.Data;

@Data
public class TeamResults {

	private String teamCode;
	private String teamName;
	private List<SelectedPlayer> players;
	private List<SelectedPlayer> emergencies;
	private int score;
	private int currentPredictedScore;
	private int predictedScore;
	private int trend;
	private String emgInd;
}
