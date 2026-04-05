package br.com.coretech.hero_api.screentime.entities;

import br.com.coretech.hero_api.screentime.enums.ScreenStatus;
import br.com.coretech.hero_api.users.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_screen_time_requests")
@Getter
@Setter
public class ScreenTimeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User minor;

    private Integer requestedMinutes;
    private Integer tokenCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreenStatus screenStatus = ScreenStatus.PENDING;

    private LocalDateTime requestDate = LocalDateTime.now();

    @ManyToOne
    private User approvedBy;
}
