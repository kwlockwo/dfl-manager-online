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

import net.dflmngr.model.entities.keys.DflLadderPK;

@Entity
@Table(name = "dfl_ladder")
@IdClass(DflLadderPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class DflLadder implements Comparator<DflLadder>, Comparable<DflLadder> {

	@Id
	private int round;

	@Id
	@Column(name = "team_code")
	private String teamCode;

	private int wins;
	private int losses;
	private int draws;

	@Column(name = "points_for")
	private int pointsFor;

	@Column(name = "points_against")
	private int pointsAgainst;

	@Column(name = "average_for")
	private float averageFor;

	@Column(name = "average_against")
	private float averageAgainst;

	private int pts;
	private float percentage;
	private boolean live;

	@Override
	public int compareTo(DflLadder o) {
		if (this.pts != o.pts) return Integer.compare(this.pts, o.pts);
		if (this.percentage != o.percentage) return Float.compare(this.percentage, o.percentage);
		return Integer.compare(this.pointsFor, o.pointsFor);
	}

	@Override
	public int compare(DflLadder o1, DflLadder o2) {
		if (o1.pts != o2.pts) return Integer.compare(o1.pts, o2.pts);
		if (o1.percentage != o2.percentage) return Float.compare(o1.percentage, o2.percentage);
		return Integer.compare(o1.pointsFor, o2.pointsFor);
	}
}
