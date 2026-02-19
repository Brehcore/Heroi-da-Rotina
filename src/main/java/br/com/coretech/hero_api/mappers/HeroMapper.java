package br.com.coretech.hero_api.mappers;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.dtos.TransactionDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.MoneyTransaction;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.users.entities.Usuario;
import org.springframework.stereotype.Component;

@Component
public class HeroMapper {

    public UserResponseDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setRole(usuario.getRole());

        if (usuario.getFamilia() != null) {
            dto.setFamiliaId(usuario.getFamilia().getId());
            dto.setFamiliaNome(usuario.getFamilia().getNomeFamilia());
        }
        return dto;
    }

    public WalletResponseDTO toCarteiraDTO(Wallet wallet) {
        if (wallet == null) return null;

        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setSaldoFichas(wallet.getSaldoFichas());
        dto.setSaldoDinheiro(wallet.getSaldoDinheiro());

        if (wallet.getMenor() != null) {
            dto.setMenorId(wallet.getMenor().getId());
            dto.setMenorNome(wallet.getMenor().getNome());
        }
        return dto;
    }

    // Mapper genérico para histórico (Fichas)
    public TransactionDTO toTransacaoDTO(TokenTransaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTipo(tx.getTipo());
        dto.setMotivo(tx.getMotivo());
        dto.setData(tx.getData());
        dto.setValorFormatado(tx.getValor() + " Fichas"); // Formata para leitura humana
        return dto;
    }

    // Mapper genérico para histórico (Dinheiro)
    public TransactionDTO toTransacaoDTO(MoneyTransaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTipo(tx.getTipo());
        dto.setMotivo(tx.getMotivo());
        dto.setData(tx.getData());
        dto.setValorFormatado("R$ " + String.format("%.2f", tx.getValor())); // Formata dinheiro
        return dto;
    }
}