package net.dflmngr.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dfl_team")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class DflTeam {

	@Id
	@Column(name = "team_code")
	private String teamCode;

	private String name;

	@Column(name = "short_name")
	private String shortName;

	@Column(name = "coach_name")
	private String coachName;

	@Column(name = "home_ground")
	private String homeGround;

	private String colours;

	@Column(name = "coach_email")
	private String coachEmail;
}
