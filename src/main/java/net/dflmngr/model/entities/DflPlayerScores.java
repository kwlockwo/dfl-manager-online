package net.dflmngr.model.entities;

import java.util.Comparator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import net.dflmngr.model.entities.keys.DflPlayerScoresPK;

@Entity
@Table(name = "dfl_player_scores")
@IdClass(DflPlayerScoresPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class DflPlayerScores implements Comparator<DflPlayerScores>, Comparable<DflPlayerScores> {

	@Id
	@Column(name = "player_id")
	private int playerId;

	@Id
	private int round;

	@Column(name = "afl_player_id")
	private String aflPlayerId;

	@Column(name = "team_code")
	private String teamCode;

	@Column(name = "team_player_id")
	private int teamPlayerId;

	private int score;

	@Override
	public int compareTo(DflPlayerScores o) {
		return Integer.compare(this.score, o.score);
	}

	@Override
	public int compare(DflPlayerScores o1, DflPlayerScores o2) {
		return Integer.compare(o1.score, o2.score);
	}
}
