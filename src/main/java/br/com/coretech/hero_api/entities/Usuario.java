package br.com.coretech.hero_api.entities;

import br.com.coretech.hero_api.enums.RoleUsuario;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // Ex: "Lucas Heitor Soares Gomes de Vasconcelos" [cite: 7]

    @Column(nullable = false, unique = true)
    private String email; // Será o login

    @Column(nullable = false)
    private String senha; // Será armazenada com hash (BCrypt)

    // Diz se é MONITOR ou MENOR
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUsuario role;

    // A qual família este usuário pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familia_id", nullable = false)
    private Familia familia;

    // A carteira do usuário (só será preenchida se role = ROLE_MENOR)
    @OneToOne(mappedBy = "menor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Carteira carteira;
}