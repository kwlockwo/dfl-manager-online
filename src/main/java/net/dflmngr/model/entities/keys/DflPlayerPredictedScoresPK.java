package net.dflmngr.model.entities.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class DflPlayerPredictedScoresPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int playerId;
	private int round;
}
