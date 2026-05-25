package br.com.coretech.hero_api.users.repositories;

import br.com.coretech.hero_api.users.entities.User;
import br.com.coretech.hero_api.users.enums.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<User> findAllByFamiliesId(Long familiesId);

    // Buscar 'families' E os 'members' de cada uma delas
    @EntityGraph(attributePaths = {"families", "families.members"})
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithFamilies(@Param("email") String email);

    // Busca todos os usuários de uma família específica que possuem um determinado papel (Role)
    List<User> findByFamilies_IdAndRole(Long familyId, UserRole role);
}
