package br.com.coretech.hero_api.screentime.entities;

import br.com.coretech.hero_api.financial.entities.Wallet;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_screen_time_config")
@Getter
@Setter
public class ScreenTimeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    private Integer minutesPerToken;
    private Integer mondayLimit;
    private Integer tuesdayLimit;
    private Integer wednesdayLimit;
    private Integer thursdayLimit;
    private Integer fridayLimit;
    private Integer saturdayLimit;
    private Integer sundayLimit;
}
