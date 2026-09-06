package br.com.coretech.hero_api.screentime.repositories;

import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreenTimeRequestRepository extends JpaRepository<ScreenTimeRequest, Long> {

    // Busca solicitações pelo status e pela família do menor
    List<ScreenTimeRequest> findAllByMinorFamiliesIdAndScreenStatus(Long familyId, ScreenStatus status);

    Optional<ScreenTimeRequest> findByApprovalToken(String approvalToken);

    void deleteByMinorId(Long minorId);

    @Query("""
    SELECT COALESCE(SUM(r.requestedMinutes), 0)
    FROM ScreenTimeRequest r
    WHERE r.minor.id = :minorId
      AND r.screenStatus = :status
      AND r.requestDate >= :startOfDay
      AND r.requestDate <= :endOfDay
""")
    Integer sumUsedMinutesToday(
            @Param("minorId") Long minorId,
            @Param("status") ScreenStatus status,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
