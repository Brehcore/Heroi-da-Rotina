package br.com.coretech.hero_api.users.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_family")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String familyName; // Ex: "Família Soares" [cite: 7]

    // Uma família tem vários usuários, e um usuário pode estar em várias famílias
    @ManyToMany(mappedBy = "families")
    @JsonIgnore
    private List<User> members;
}