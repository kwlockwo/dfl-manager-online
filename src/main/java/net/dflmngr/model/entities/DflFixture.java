package net.dflmngr.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import net.dflmngr.model.entities.keys.DflFixturePK;

@Entity
@Table(name = "dfl_fixture")
@IdClass(DflFixturePK.class)
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class DflFixture {

	@Id
	private int round;

	@Id
	private int game;

	@Column(name = "home_team")
	private String homeTeam;

	@Column(name = "away_team")
	private String awayTeam;
}
