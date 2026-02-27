package br.com.coretech.hero_api.tasks.enums;

public enum TaskStatus {

    PENDING, // Task criada pelo monitor, não iniciada
    COMPLETED, // Menor marcou a tarefa como concluída
    APPROVED, // Task aprovada pelo monitor, menor recompensado (se houver)
    REJECTED // Monitor rejeitou a tarefa (despontuar ou refazer a tarefa)
}
