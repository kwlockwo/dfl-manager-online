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

import net.dflmngr.model.entities.keys.DflPlayerPredictedScoresPK;

@Entity
@Table(name = "dfl_player_predicted_scores")
@IdClass(DflPlayerPredictedScoresPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class DflPlayerPredictedScores implements Comparator<DflPlayerPredictedScores>, Comparable<DflPlayerPredictedScores> {

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

	@Column(name = "predicted_score")
	private int predictedScore;

	@Override
	public int compareTo(DflPlayerPredictedScores o) {
		return Integer.compare(this.predictedScore, o.predictedScore);
	}

	@Override
	public int compare(DflPlayerPredictedScores o1, DflPlayerPredictedScores o2) {
		return Integer.compare(o1.predictedScore, o2.predictedScore);
	}
}
