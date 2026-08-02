package br.com.coretech.hero_api.auth.repositories;

import br.com.coretech.hero_api.auth.entities.PasswordResetToken;
import br.com.coretech.hero_api.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);
}
