package br.com.coretech.hero_api.screentime.repositories;

import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenTimeRequestRepository extends JpaRepository<ScreenTimeRequest, Long> {
}
