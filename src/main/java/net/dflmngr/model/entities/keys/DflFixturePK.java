package net.dflmngr.model.entities.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class DflFixturePK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int round;
	private int game;
}
