package br.com.coretech.hero_api.mappers;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.dtos.TransactionDTO;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.MoneyTransaction;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HeroMapper {

    public UserResponseDTO toUserDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        if (user.getFamily() != null) {
            dto.setFamilyId(user.getFamily().getId());
            dto.setFamilyName(user.getFamily().getFamilyName());
        }
        return dto;
    }

    public WalletResponseDTO toWalletDTO(Wallet wallet) {
        if (wallet == null) return null;

        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setTokensBalance(wallet.getTokenBalances());
        dto.setMoneyBalance(wallet.getMoneyBalances());

        if (wallet.getMinor() != null) {
            dto.setMinorId(wallet.getMinor().getId());
            dto.setMinorName(wallet.getMinor().getName());
        }
        return dto;
    }

    // Mapper genérico para histórico (Fichas)
    public TransactionDTO toTransactionDTO(TokenTransaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setType(tx.getType());
        dto.setMotive(tx.getMotive());
        dto.setDate(tx.getDate());
        dto.setFormatedValue(tx.getValue() + " Fichas"); // Formata para leitura humana
        return dto;
    }

    // Mapper genérico para histórico (Dinheiro)
    public TransactionDTO toTransactionDTO(MoneyTransaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setType(tx.getType());
        dto.setMotive(tx.getMotive());
        dto.setDate(tx.getDate());
        dto.setFormatedValue("R$ " + String.format("%.2f", tx.getValue())); // Formata dinheiro
        return dto;
    }

    public FamilyResponseDTO toFamilyDTO(Family family) {
        if (family == null) return null;
        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setId(family.getId());
        dto.setFamilyName(family.getFamilyName());

        if (family.getMembers() != null) {
            List<UserResponseDTO> convertedListMembers = family.getMembers().stream() //Lista temporária para guardar os usuários na família
                    .map(this::toUserDTO)
                    .toList();
            dto.setMembers(convertedListMembers);
        }
        return dto;
    }
}