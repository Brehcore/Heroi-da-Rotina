package br.com.coretech.hero_api.screentime.repositories;

import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenTimeRequestRepository extends JpaRepository<ScreenTimeRequest, Long> {

    // Busca solicitações pelo status e pela família do menor
    List<ScreenTimeRequest> findAllByMinorFamiliesIdAndScreenStatus(Long familyId, ScreenStatus status);
}
