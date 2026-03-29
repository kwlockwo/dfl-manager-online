package net.dflmngr.model.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import net.dflmngr.model.entities.keys.GlobalsPK;

@Entity
@Table(name = "globals")
@IdClass(GlobalsPK.class)
@Getter @Setter @ToString @EqualsAndHashCode
public class Globals implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String code;

	@Id
	@Column(name = "group_code")
	private String groupCode;

	private String params;
	private String value;
}
