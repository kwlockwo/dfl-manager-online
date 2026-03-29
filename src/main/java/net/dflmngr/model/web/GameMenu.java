package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMenu {

	private int game;
	private String homeTeam;
	private String awayTeam;
	private boolean active;
	private String resultsUri;
}
