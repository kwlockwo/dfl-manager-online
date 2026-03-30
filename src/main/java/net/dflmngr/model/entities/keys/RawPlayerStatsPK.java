package net.dflmngr.model.entities.keys;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RawPlayerStatsPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int round;
	private String name;
	private String team;
}
