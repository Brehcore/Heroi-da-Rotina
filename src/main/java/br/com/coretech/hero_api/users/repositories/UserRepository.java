package br.com.coretech.hero_api.users.repositories;

import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{

    /**
     * Busca um usuário pelo seu email (login).
     * Essencial para o Spring Security fazer a autenticação.
     */
    Optional<User> findByEmail(String email);

    /**
     * Encontra todos os membros de uma família específica.
     */
    List<User> findAllByFamilyId(Long familyId);

    /**
     * Encontra todos os usuários de uma família que têm um papel específico.
     * Ex: "Me dê todos os MONITORES da família X"
     */
    List<User> findAllByFamilyIdAndRole(Long familyId, UserRole role);
}
