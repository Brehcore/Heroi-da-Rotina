package br.com.coretech.hero_api.financial.dtos;

import br.com.coretech.hero_api.financial.enums.InterestFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Resumo do saldo e informações da carteira do usuário")
public class WalletResponseDTO {

    @Schema(description = "ID da carteira", example = "10")
    private Long id;

    @Schema(description = "ID do usuário (menor de idade/dependente) dono da carteira", example = "5")
    private Long minorId;

    @Schema(description = "Nome do usuário dono da carteira", example = "Peter Parker")
    private String minorName;

    @Schema(description = "Saldo atual em fichas/tokens", example = "150")
    private Integer tokensBalance;

    @Schema(description = "Saldo atual em moeda real (R$)", example = "75.50")
    private Double moneyBalance;

    @Schema(description = "Exibe a cotação atual do token", example = "R$ 1,50")
    private Double tokenQuotation;

    @Schema(description = "Taxa de juros mensal", example = "0.05")
    private Double interestRate;

    @Schema(description = "Ativa/Desativa taxa de juros automática")
    private Boolean interestEnabled;

    @Schema(description = "Frequência do rendimento (DAILY, WEEKLY, MONTHLY)", example = "WEEKLY")
    private InterestFrequency interestFrequency;
}