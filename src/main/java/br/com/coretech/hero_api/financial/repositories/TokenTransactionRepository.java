package br.com.coretech.hero_api.financial.repositories;

import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {

    Page<TokenTransaction> findByWalletMinorId(Long minorId, Pageable pageable);
}