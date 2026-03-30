package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ladder {

	private int round;
	private String teamCode;
	private int wins;
	private int losses;
	private int draws;
	private int pointsFor;
	private int pointsAgainst;
	private float averageFor;
	private float averageAgainst;
	private int pts;
	private float percentage;
	private String displayName;
	private String teamUri;
}
