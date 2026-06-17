package br.com.coretech.hero_api.mappers;

import br.com.coretech.hero_api.financial.dtos.WalletResponseDTO;
import br.com.coretech.hero_api.financial.dtos.TransactionDTO;
import br.com.coretech.hero_api.screentime.dtos.ScreenTimeResponseDTO;
import br.com.coretech.hero_api.screentime.entities.ScreenTimeRequest;
import br.com.coretech.hero_api.tasks.dtos.TaskResponseDTO;
import br.com.coretech.hero_api.tasks.entities.Task;
import br.com.coretech.hero_api.users.dtos.FamilyResponseDTO;
import br.com.coretech.hero_api.financial.entities.Wallet;
import br.com.coretech.hero_api.financial.entities.MoneyTransaction;
import br.com.coretech.hero_api.financial.entities.TokenTransaction;
import br.com.coretech.hero_api.users.dtos.UserResponseDTO;
import br.com.coretech.hero_api.users.entities.Family;
import br.com.coretech.hero_api.users.entities.User;
import org.springframework.stereotype.Component;

@Component
public class HeroMapper {

    public UserResponseDTO toUserDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());

        return dto;
    }

    public FamilyResponseDTO toFamilyDTO(Family family) {
        if (family == null) return null;

        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setId(family.getId());
        dto.setFamilyName(family.getFamilyName());
        dto.setProfilePictureUrl(family.getProfilePictureUrl());

        if (family.getMembers() != null) {
            dto.setMembers(family.getMembers().stream()
                    .map(member -> {
                        UserResponseDTO mDto = new UserResponseDTO();
                        mDto.setId(member.getId());
                        mDto.setName(member.getName());
                        mDto.setEmail(member.getEmail());
                        mDto.setRole(member.getRole());
                        mDto.setFamilyId(family.getId());
                        mDto.setFamilyName(family.getFamilyName());
                        mDto.setProfilePictureUrl(member.getProfilePictureUrl());
                        return mDto;
                    })
                    .toList());
        }
        return dto;
    }

    public WalletResponseDTO toWalletDTO(Wallet wallet) {
        if (wallet == null) return null;

        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setTokensBalance(wallet.getTokenBalances());
        dto.setMoneyBalance(wallet.getMoneyBalances());
        dto.setTokenQuotation(wallet.getTokenQuotation());
        dto.setInterestRate(wallet.getInterestRate());
        dto.setInterestEnabled(wallet.getInterestEnabled());
        dto.setInterestFrequency(wallet.getInterestFrequency());

        if (wallet.getMinor() != null) {
            dto.setMinorId(wallet.getMinor().getId());
            dto.setMinorName(wallet.getMinor().getName());
        }
        return dto;
    }

    public ScreenTimeResponseDTO toScreenTimeResponseDTO(ScreenTimeRequest request) {
        if (request == null) return null;

        return new ScreenTimeResponseDTO(
                request.getId(),
                request.getMinor().getId(),
                request.getMinor().getName(),
                request.getScreenStatus() != null ? request.getScreenStatus().name() : "PENDENTE",
                request.getRequestedMinutes(),
                0
        );
    }

    public TaskResponseDTO toTaskDTO(Task task) {
        if (task == null) return null;

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setRewardTask(task.getTokenReward());
        dto.setStatus(task.getStatus());
        dto.setRejectionReason(task.getRejectionReason());

        // Evita NullPointerException caso o menor não venha preenchido
        if (task.getMinor() != null) {
            dto.setMinorId(task.getMinor().getId());
            dto.setMinorName(task.getMinor().getName());
        }

        dto.setCreationDate(task.getCreationDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setApprovalDate(task.getApprovalDate());

        return dto;
    }

    // 1. Mapeador para Transações de FICHAS (Tokens)
    public TransactionDTO toTokenTransactionDTO(TokenTransaction transaction) {
        if (transaction == null) return null;

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setMotive(transaction.getMotive());
        dto.setDate(transaction.getDate());

        // Monta o valor formatado amigável para o Front-end
        // Ex: "5 Fichas" ou "1 Ficha"
        String label = (transaction.getValue() != null && transaction.getValue() == 1) ? "Ficha" : "Fichas";
        dto.setFormattedValue(transaction.getValue() + " " + label);

        return dto;
    }

    // 2. Mapeador para Transações de DINHEIRO (Money) - (Opcional)
    public TransactionDTO toMoneyTransactionDTO(MoneyTransaction transaction) {
        if (transaction == null) return null;

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setMotive(transaction.getMotive());
        dto.setDate(transaction.getDate());

        // Monta o valor formatado. Ex: "R$ 15.50"
        if (transaction.getValue() != null) {
            dto.setFormattedValue(String.format("R$ %.2f", transaction.getValue()).replace(".", ","));
        } else {
            dto.setFormattedValue("R$ 0,00");
        }

        return dto;
    }
}