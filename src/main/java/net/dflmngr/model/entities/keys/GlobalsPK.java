package net.dflmngr.model.entities.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class GlobalsPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String groupCode;
}
