package br.com.coretech.hero_api.repositories;

import br.com.coretech.hero_api.entities.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    // O ID da Carteira é o mesmo ID do Usuário (menor)

    /**
     * Permite buscar a carteira diretamente pelo ID do usuário menor.
     * Muito mais prático do que buscar o usuário e depois pegar a carteira.
     */
    Optional<Carteira> findByMenorId(Long menorId);
}
