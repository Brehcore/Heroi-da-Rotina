package br.com.coretech.hero_api.gamification.repositories;

import br.com.coretech.hero_api.gamification.entities.UserGamification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGamificationRepository extends JpaRepository<UserGamification, Long> {
    Optional<UserGamification> findByUserId(Long userId);
}