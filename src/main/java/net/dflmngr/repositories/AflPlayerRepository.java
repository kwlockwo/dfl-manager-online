package net.dflmngr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dflmngr.model.entities.AflPlayer;

public interface AflPlayerRepository extends JpaRepository<AflPlayer, String>{
	List<AflPlayer> findByDflPlayerIdIn(List<Integer> playerIds);
}
