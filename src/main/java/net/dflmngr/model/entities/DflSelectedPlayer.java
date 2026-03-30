package net.dflmngr.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import net.dflmngr.model.entities.keys.DflSelectedPlayerPK;

@Entity
@Table(name = "dfl_selected_player")
@IdClass(DflSelectedPlayerPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class DflSelectedPlayer {

	@Id
	private int round;

	@Id
	@Column(name = "player_id")
	private int playerId;

	@Column(name = "team_player_id")
	private int teamPlayerId;

	@Column(name = "team_code")
	private String teamCode;

	@Column(name = "is_emergency")
	private int isEmergency;

	@Column(name = "is_dnp")
	private boolean isDnp;

	@Column(name = "score_used")
	private boolean scoreUsed;

	@Column(name = "has_played")
	private boolean hasPlayed;

	@Column(name = "replacement_ind")
	private String replacementInd;

	public int isEmergency() {
		return isEmergency;
	}

	public void setEmergency(int isEmergency) {
		this.isEmergency = isEmergency;
	}

	public boolean hasPlayed() {
		return hasPlayed;
	}
}
