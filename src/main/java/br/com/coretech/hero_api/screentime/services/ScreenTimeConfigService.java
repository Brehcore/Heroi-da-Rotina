package br.com.coretech.hero_api.screentime.services;

import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.repositories.WalletRepository;
import br.com.coretech.hero_api.screentime.dtos.ScreenTimeConfigDTO;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeConfig;
import br.com.coretech.hero_api.screentime.repositories.ScreenTimeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScreenTimeConfigService {

    private final ScreenTimeConfigRepository configRepository;
    private final WalletRepository walletRepository;

    // (Busca a configuração atual do menor)
    @Transactional(readOnly = true)
    public ScreenTimeConfigDTO getConfig(Long minorId) {
        // Busca a config ou retorna uma nova com os valores padrão (como 30 min) se não existir
        ScreenTimeConfig config = configRepository.findByWalletMinorId(minorId)
                .orElse(new ScreenTimeConfig());

        return toDTO(config);
    }

    @Transactional
    public ScreenTimeConfigDTO saveOrUpdateConfig(Long minorId, ScreenTimeConfigDTO dto) {
        Wallet wallet = walletRepository.findByMinorId(minorId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada para o menor"));

        ScreenTimeConfig config = configRepository.findByWalletMinorId(minorId)
                .orElse(new ScreenTimeConfig());

        config.setWallet(wallet);

        // Copia todos os limites do DTO para a Entidade de uma vez!
        BeanUtils.copyProperties(dto, config);

        ScreenTimeConfig savedConfig = configRepository.save(config);
        return toDTO(savedConfig);
    }

    private ScreenTimeConfigDTO toDTO(ScreenTimeConfig config) {
        ScreenTimeConfigDTO dto = new ScreenTimeConfigDTO();

        // Copiar da Entidade para o DTO!
        BeanUtils.copyProperties(config, dto);

        return dto;
    }
}