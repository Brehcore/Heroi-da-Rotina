package br.com.coretech.hero_api.financial.repositories;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.enums.InterestFrequency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Permite buscar a carteira diretamente pelo ID do usuário menor.
     */
    Optional<Wallet> findByMinorId(Long minorId);

    /**
     * Busca todas as carteiras que têm o rendimento ativado
     */
    List<Wallet> findAllByInterestEnabledTrue();

    // Busca carteiras que estão ativas E com uma frequência específica
    List<Wallet> findAllByInterestEnabledTrueAndInterestFrequency(InterestFrequency frequency);
}
