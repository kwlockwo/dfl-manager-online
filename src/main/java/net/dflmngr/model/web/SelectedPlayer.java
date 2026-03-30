package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedPlayer {

	private int playerId;
	private int teamPlayerId;
	private String name;
	private String position;
	private boolean hasPlayer;
	private boolean scoreUsed;
	private boolean isDnp;
	private String replacementInd;
	private int emgSort;
	private PlayerStats stats;
}
