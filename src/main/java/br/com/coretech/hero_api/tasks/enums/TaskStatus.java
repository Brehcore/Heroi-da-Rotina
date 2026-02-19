package br.com.coretech.hero_api.tasks.enums;

public enum TaskStatus {

    PENDENTE, // Task criada pelo monitor, não iniciada
    CONCLUIDA, // Menor marcou a tarefa como concluída
    APROVADA, // Task aprovada pelo monitor, menor recompensado (se houver)
    REJEITADA // Monitor rejeitou a tarefa (despontuar ou refazer a tarefa)
}
