# 🦸‍♂️ Herói da Rotina

**Transformando tarefas diárias em uma grande aventura!**

O **Herói da Rotina** é uma plataforma de gamificação para gerenciamento familiar. Ele transforma um "Contrato de Rotina e Responsabilidades" em um aplicativo interativo (Web e Mobile).

O objetivo é ajudar "Monitores" (pais/responsáveis) a gerenciar tarefas, rotinas financeiras e o tempo de tela de um "Menor" (criança/adolescente), ensinando responsabilidade, organização e noções de finanças de uma forma divertida.

---

## ✨ Funcionalidades Principais

* 👨‍👩‍👧‍👦 **Gestão Familiar:** Crie famílias e adicione membros com papéis definidos (`MONITOR` e `MINOR`).
* ✅ **Quadro de Tarefas Dinâmico:** Monitores criam e gerenciam tarefas diárias, semanais ou bônus (ex: "Arrumar o quarto", "Leitura diária").
* 💪 **Jornada do Herói:** O "Menor" visualiza suas tarefas e as marca como `CONCLUÍDA`.
* 👍 **Auditoria Compartilhada:** Monitores validam as tarefas, podendo `APROVAR` (pagando bônus) ou `REJEITAR` (solicitando refazer ou aplicando multa).
* 🎟️ **Banco Central de Fichas:** Um sistema financeiro completo para gerenciar "Fichas" (a moeda do app), com taxas de câmbio customizáveis para tempo e dinheiro.
* 📱 **Controle Inteligente de Tela:** O menor solicita minutos de tela que são auditados contra limites diários e descontados automaticamente do saldo após aprovação.
* 💰 **Cofrinho Digital:** Ensina educação financeira, convertendo fichas não usadas em saldo monetário real e registrando o histórico de transações.

---

## 📜 Regras do Jogo (O Contrato Digital)

O sistema opera com base em configurações dinâmicas que representam o contrato familiar. O aplicativo oferece flexibilidade total para que as regras acompanhem a realidade e o desenvolvimento do menor.

### 🎟️ O Sistema de Fichas (A Moeda)

* **Distribuição de Fichas [⚙️ 100% Editável]:** O sistema não entrega fichas automaticamente. O Monitor avalia o comportamento e realiza o depósito manual inicial da semana na carteira do menor.
  * 💡 *Sugestão de Controle:* Combine uma "mesada base" (ex: 14 fichas às segundas-feiras) condicionada ao bom comportamento da semana anterior. Se o comportamento foi ruim, inicie a semana com menos fichas.
* **Câmbio de Tempo de Tela [⚙️ 100% Editável]:** O sistema permite definir o "poder de compra" da ficha em minutos através do painel de configurações.
  * 💡 *Sugestão de Controle:* O padrão recomendado é **1 Ficha = 30 minutos** para facilitar a matemática para a criança. Em semanas de castigo, você pode alterar a configuração para "1 Ficha = 15 minutos", encarecendo o tempo de tela.
* **Limites Diários de Uso [⚙️ 100% Editável]:** A API bloqueia pedidos abusivos com base no limite específico configurado para cada dia da semana.
  * 💡 *Sugestão de Controle:* Ajuste o CRUD de limites para refletir a rotina:
    * *Dias Letivos (Seg-Sex):* Limite de 90min/dia.
    * *Finais de Semana (Sáb-Dom):* Limite de 240min/dia.
    * *Período de Provas 📚:* Reduza manualmente os limites de Seg-Sex para 60min/dia no aplicativo, forçando o menor a focar nos estudos.

### 🕹️ Solicitação e Uso de Tempo de Tela

* **O Pedido:** O menor acessa o app e solicita o tempo desejado (Ex: "1 hora de videogame"). O sistema calcula o custo exato em fichas.
* **Trava de Segurança:** Se o tempo solicitado ultrapassar o Limite Diário daquele dia da semana, ou se o menor não tiver saldo em fichas, a API bloqueia a solicitação imediatamente.
* **Aprovação Concorrente:** Todos os Monitores da família recebem o pedido com status `PENDING`. O primeiro a `APROVAR` finaliza o processo, deduzindo as fichas do histórico do menor com total transparência (ex: "Uso de tempo de tela: 60min").

### 🏆 Ganhando Fichas Extras (Missões Bônus)

O "Menor" pode ganhar fichas além da sua mesada base completando tarefas aprovadas pelos monitores.
* 💡 *Sugestão de Controle:* Crie tarefas recorrentes no sistema com recompensas claras, como:
  * **Leitura 📖:** Ler por 30 minutos = **+1 Ficha**.
  * **Caligrafia ✍️:** Praticar por 30 minutos = **+1 Ficha**.
  * *Defina um acordo verbal de limite de tarefas bônus por dia para evitar que o menor "farme" fichas excessivamente.*

### 💰 O Cofrinho (Conversão e Juros Compostos)

Esta mecânica ensina o poder de fazer o dinheiro trabalhar sozinho:

* **Conversão de Fichas:** O monitor transforma fichas em dinheiro real no final da semana.
* **Sistema de Rendimento Dinâmico [⚙️ 100% Editável]:** Por um "Switch" no painel do monitor, é possível ativar a aplicação de juros sobre o saldo acumulado.
  * 💡 *Sugestão de Controle:* Defina uma taxa (ex: 1%, 1.5% ou 2%). Toda segunda-feira, após a conversão, o monitor clica em "Aplicar Rendimento".
  * *Exemplo:* Se o menor acumulou R$ 100,00 e a taxa é de 2%, o sistema adiciona R$ 2,00 automaticamente. Na semana seguinte, os 2% serão sobre os R$ 102,00, ensinando o conceito de juros sobre juros.

### 💀 Multas (Perda de Fichas)

* Através do endpoint de `withdraw` (Saque de Fichas), o Monitor pode extrair fichas da carteira justificando o motivo no histórico.
  * 💡 *Sugestão de Controle:* Aplique multas para comportamentos como: reclamações constantes, desrespeito ou deixar tarefas essenciais incompletas. Punir com a perda de fichas (tempo de tela) costuma ser muito mais efetivo do que punir com a perda de dinheiro do cofrinho.

---

## 🛠️ Tecnologias Utilizadas

* **Backend (API):** ☕ Java 17+
* **Framework:** 🌱 Spring Boot 3
* **Segurança:** 🔒 Spring Security (com JWT)
* **Banco de Dados:** 🐘 PostgreSQL (via 🐳 Docker / Migrations com Flyway)
* **Frontend (Web):** 🅰️ Angular
* **Mobile (App do Menor):** 📱 Java / Kotlin (Android)