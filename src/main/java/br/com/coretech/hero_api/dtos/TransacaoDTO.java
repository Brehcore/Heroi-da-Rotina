package br.com.coretech.hero_api.dtos;

import br.com.coretech.hero_api.enums.TipoTransacao;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransacaoDTO {
    private Long id;
    private TipoTransacao tipo; // CREDITO ou DEBITO
    private String motivo;
    private String valorFormatado; // Ex: "10 Fichas" ou "R$ 5,00"
    private LocalDateTime data;
}