package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFixture {

	private int game;
	private String homeTeam;
	private String awayTeam;
	private int homeTeamScore;
	private int awayTeamScore;
	private String homeTeamDisplayName;
	private String awayTeamDisplayName;
	private String resultsUri;
}
