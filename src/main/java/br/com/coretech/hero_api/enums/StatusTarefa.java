package br.com.coretech.hero_api.enums;

public enum StatusTarefa {

    PENDENTE, // Tarefa criada pelo monitor, não iniciada
    CONCLUIDA, // Menor marcou a tarefa como concluída
    APROVADA, // Tarefa aprovada pelo monitor, menor recompensado (se houver)
    REJEITADA // Monitor rejeitou a tarefa (despontuar ou refazer a tarefa)
}
