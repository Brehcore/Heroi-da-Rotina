package br.com.coretech.hero_api.screentime.repositories;

import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScreenTimeConfigRepository extends JpaRepository<ScreenTimeConfig, Long> {
    Optional<ScreenTimeConfig> findByWalletMinorId(Long minorId);
}