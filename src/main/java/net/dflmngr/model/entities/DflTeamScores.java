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

import net.dflmngr.model.entities.keys.DflTeamScoresPK;

@Entity
@Table(name = "dfl_team_scores")
@IdClass(DflTeamScoresPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class DflTeamScores {

	@Id
	@Column(name = "team_code")
	private String teamCode;

	@Id
	private int round;

	private int score;
}
