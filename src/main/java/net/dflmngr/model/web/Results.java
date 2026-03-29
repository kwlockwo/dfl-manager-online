package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Results {

	private int round;
	private int game;
	private TeamResults homeTeam;
	private TeamResults awayTeam;
}
