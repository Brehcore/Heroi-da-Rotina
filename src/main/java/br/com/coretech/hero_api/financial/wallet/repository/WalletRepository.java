package br.com.coretech.hero_api.financial.wallet.repository;

import br.com.coretech.hero_api.financial.transaction.entity.TokenTransaction;
import br.com.coretech.hero_api.financial.wallet.entity.Wallet;
import br.com.coretech.hero_api.financial.wallet.enums.InterestFrequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Permite buscar a carteira diretamente pelo ID do usuário menor.
     */
    Optional<Wallet> findByMinorId(Long minorId);

    Page<TokenTransaction> findTransactionByMinorId(Long minorId, Pageable pageable);

    // Busca carteiras que estão ativas E com uma frequência específica
    List<Wallet> findAllByInterestEnabledTrueAndInterestFrequency(InterestFrequency frequency);
}
