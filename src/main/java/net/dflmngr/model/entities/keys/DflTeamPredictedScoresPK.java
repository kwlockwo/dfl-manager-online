package net.dflmngr.model.entities.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class DflTeamPredictedScoresPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private String teamCode;
	private int round;
}
