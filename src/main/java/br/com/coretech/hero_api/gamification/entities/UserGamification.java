package br.com.coretech.hero_api.gamification.entities;

import br.com.coretech.hero_api.users.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_user_gamification")
public class UserGamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer currentLevel = 1;

    @Column(nullable = false)
    private Integer currentXp = 0;

    @Column(nullable = false)
    private Integer targetXp = 100;
}