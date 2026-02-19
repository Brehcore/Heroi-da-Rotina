package br.com.coretech.hero_api.financial.repositories;

import br.com.coretech.hero_api.financial.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // O ID da Wallet é o mesmo ID do Usuário (menor)

    /**
     * Permite buscar a carteira diretamente pelo ID do usuário menor.
     * Muito mais prático do que buscar o usuário e depois pegar a carteira.
     */
    Optional<Wallet> findByMenorId(Long menorId);
}
