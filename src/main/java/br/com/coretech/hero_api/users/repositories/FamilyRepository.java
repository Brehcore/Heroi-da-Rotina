package br.com.coretech.hero_api.users.repositories;

import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {

}
