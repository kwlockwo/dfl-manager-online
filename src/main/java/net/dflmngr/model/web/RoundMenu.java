package net.dflmngr.model.web;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundMenu {

	private int round;
	private List<GameMenu> games;
	private boolean active;
}
