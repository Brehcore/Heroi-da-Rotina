package br.com.coretech.hero_api.repositories;

import br.com.coretech.hero_api.entities.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamiliaRepository extends JpaRepository<Familia, Long> {
}
