package br.com.coretech.hero_api.financial.dtos;

import br.com.coretech.hero_api.financial.enums.InterestFrequency;
import lombok.Data;

@Data
public class InterestConfigDTO {
    private Double rate;
    private Boolean enabled;
    private InterestFrequency frequency;
}