package br.com.coretech.hero_api.users.entities;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.users.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // A quais famílias este usuário pertence (Um monitor pode ter várias)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_users_families",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "family_id")
    )
    @JsonIgnore
    private Set<Family> families = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "minor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wallet wallet;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}