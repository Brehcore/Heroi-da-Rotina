package br.com.coretech.hero_api.users.entities;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Ex: "Lucas Heitor Soares Gomes de Vasconcelos" [cite: 7]

    @Column(nullable = false, unique = true)
    private String email; // Será o login

    @Column(nullable = false)
    private String password; // Será armazenada com hash (BCrypt)

    // Diz se é MONITOR ou MENOR
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // A qual família este usuário pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    // A wallet do usuário (só será preenchida se role = ROLE_MENOR)
    @OneToOne(mappedBy = "minor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}