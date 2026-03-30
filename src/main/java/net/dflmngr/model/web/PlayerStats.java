package net.dflmngr.model.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {

	private int kicks;
	private int handballs;
	private int disposals;
	private int marks;
	private int hitouts;
	private int freesFor;
	private int freesAgainst;
	private int tackles;
	private int goals;
	private int behinds;
	private int score;
	private int predictedScore;
	private int trend;
	private String scrapingStatus;
}
