package br.com.coretech.hero_api.financial.dtos;

import br.com.coretech.hero_api.financial.enums.TransactionType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private TransactionType type; // CREDITO ou DEBITO
    private String motive;
    private String FormatedValue; // Ex: "10 Fichas" ou "R$ 5,00"
    private LocalDateTime date;
}