package br.com.coretech.hero_api.users.repositories;

import br.com.coretech.hero_api.users.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {
}
