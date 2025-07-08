# Banco GVM - Frontend Completo

Sistema de gestão bancária desenvolvido em React para o trabalho prático da disciplina 55DSW - Desenvolvimento de Sistemas para Web.

## ✅ Funcionalidades Implementadas

### 🏦 **Módulo de Clientes**
- **Listagem de clientes** com busca por nome, CPF ou email
- **Cadastro de novos clientes** com validação de dados
- **Visualização detalhada** de informações do cliente
- **Edição de dados** do cliente
- **Exclusão de clientes** com confirmação
- **Formatação automática** de CPF e telefone

### 💰 **Módulo de Contas**
- **Listagem de contas** com busca por número, agência ou status
- **Cadastro de contas corrente e poupança** com validação
- **Visualização detalhada** de informações da conta
- **Associação com clientes** titulares
- **Campos específicos** por tipo de conta:
  - **Conta Corrente:** Limite de cheque especial
  - **Conta Poupança:** Taxa de juros anual

### 💳 **Módulo de Cartões**
- **Listagem de cartões** com busca por número, titular ou status
- **Emissão de cartões de crédito e débito** com validação
- **Visualização detalhada** de informações do cartão
- **Bloqueio e desbloqueio** de cartões
- **Geração automática** de número e CVV
- **Campos específicos** para cartão de crédito:
  - Limite de crédito
  - Dia de fechamento e vencimento

### 🔄 **Módulo de Transações**
- **Listagem de transações** com busca por descrição, tipo ou status
- **Registro de transações** (depósito, saque, transferência)
- **Visualização detalhada** de informações da transação
- **Simulação de valores** antes do registro
- **Validação de contas** origem e destino
- **Histórico completo** de movimentações

### 📊 **Módulo de Empréstimos**
- **Listagem de empréstimos** com busca por cliente, CPF ou status
- **Solicitação de empréstimos** com simulação financeira
- **Visualização detalhada** de informações do empréstimo
- **Aprovação e rejeição** de solicitações
- **Cálculo automático** de parcelas e juros
- **Simulação em tempo real** do empréstimo

## 🛠️ **Tecnologias Utilizadas**

- **React 18** - Framework principal
- **Vite** - Build tool e dev server
- **Tailwind CSS** - Estilização
- **shadcn/ui** - Componentes de interface
- **Lucide React** - Ícones
- **JavaScript (JSX)** - Linguagem de programação

## 📁 **Estrutura do Projeto**

```
src/
├── components/
│   ├── ui/                    # Componentes base do shadcn/ui
│   ├── Layout.jsx             # Layout principal com sidebar
│   ├── Dashboard.jsx          # Página inicial com estatísticas
│   │
│   ├── ClientesList.jsx       # Listagem de clientes
│   ├── ClienteForm.jsx        # Formulário de cadastro/edição
│   ├── ClienteDetails.jsx     # Detalhes do cliente
│   │
│   ├── ContasList.jsx         # Listagem de contas
│   ├── ContaForm.jsx          # Formulário de cadastro de conta
│   ├── ContaDetails.jsx       # Detalhes da conta
│   │
│   ├── CartoesList.jsx        # Listagem de cartões
│   ├── CartaoForm.jsx         # Formulário de emissão de cartão
│   ├── CartaoDetails.jsx      # Detalhes do cartão
│   │
│   ├── TransacoesList.jsx     # Listagem de transações
│   ├── TransacaoForm.jsx      # Formulário de transação
│   ├── TransacaoDetails.jsx   # Detalhes da transação
│   │
│   ├── EmprestimosList.jsx    # Listagem de empréstimos
│   ├── EmprestimoForm.jsx     # Formulário de empréstimo
│   └── EmprestimoDetails.jsx  # Detalhes do empréstimo
│
├── services/
│   └── api.js                 # Serviços de comunicação com API
├── App.jsx                    # Componente principal
└── main.jsx                   # Ponto de entrada
```

## 🚀 **Como Executar**

1. **Instalar dependências:**
   ```bash
   npm install
   ```

2. **Iniciar servidor de desenvolvimento:**
   ```bash
   npm run dev
   ```

3. **Acessar a aplicação:**
   - URL: http://localhost:5173
   - A aplicação será recarregada automaticamente ao salvar alterações

## 🔗 **Integração com Backend**

O frontend está configurado para se comunicar com a API REST do backend Spring Boot:

- **URL base da API:** `http://localhost:8080/api`

### **Endpoints Implementados:**

#### **Clientes**
- `GET /clientes` - Listar todos os clientes
- `GET /clientes/{id}` - Buscar cliente por ID
- `POST /clientes` - Criar novo cliente
- `PUT /clientes/{id}` - Atualizar cliente
- `DELETE /clientes/{id}` - Excluir cliente

#### **Contas**
- `GET /contas` - Listar todas as contas
- `GET /contas/{id}` - Buscar conta por ID
- `POST /contas/corrente` - Criar conta corrente
- `POST /contas/poupanca` - Criar conta poupança

#### **Cartões**
- `GET /cartoes` - Listar todos os cartões
- `GET /cartoes/{id}` - Buscar cartão por ID
- `POST /cartoes/credito` - Emitir cartão de crédito
- `POST /cartoes/debito` - Emitir cartão de débito
- `POST /cartoes/{id}/bloquear` - Bloquear cartão
- `POST /cartoes/{id}/desbloquear` - Desbloquear cartão

#### **Transações**
- `GET /transacoes` - Listar todas as transações
- `GET /transacoes/{id}` - Buscar transação por ID
- `POST /transacoes` - Registrar nova transação
- `GET /transacoes/extrato/{contaId}` - Extrato por conta

#### **Empréstimos**
- `GET /emprestimos` - Listar todos os empréstimos
- `GET /emprestimos/{id}` - Buscar empréstimo por ID
- `POST /emprestimos` - Solicitar empréstimo
- `POST /emprestimos/{id}/aprovar` - Aprovar empréstimo
- `POST /emprestimos/{id}/rejeitar` - Rejeitar empréstimo

## ✅ **Validações Implementadas**

### **Formulário de Cliente**
- **Nome:** Campo obrigatório
- **CPF:** Campo obrigatório, deve ter 11 dígitos
- **Email:** Campo obrigatório, formato válido
- **Telefone:** Campo obrigatório
- **Data de Nascimento:** Campo obrigatório
- **Login:** Obrigatório apenas para novos clientes
- **Senha:** Obrigatória apenas para novos clientes

### **Formulário de Conta**
- **Número da Conta:** Campo obrigatório
- **Agência:** Campo obrigatório
- **Saldo Inicial:** Campo obrigatório, deve ser numérico
- **Cliente Titular:** Obrigatório
- **Limite/Taxa:** Específico por tipo de conta

### **Formulário de Cartão**
- **Nome do Titular:** Campo obrigatório
- **Data de Validade:** Campo obrigatório
- **Conta Associada:** Obrigatória
- **Campos específicos:** Por tipo de cartão

### **Formulário de Transação**
- **Valor:** Campo obrigatório, deve ser positivo
- **Tipo:** Campo obrigatório
- **Contas:** Validação por tipo de transação
- **Conta origem ≠ destino:** Para transferências

### **Formulário de Empréstimo**
- **Cliente:** Campo obrigatório
- **Valor Solicitado:** Campo obrigatório, deve ser positivo
- **Taxa de Juros:** Campo obrigatório, deve ser numérico
- **Número de Parcelas:** Campo obrigatório, entre 1 e 60

## 🎨 **Recursos de UX/UI**

- **Design responsivo** - Funciona em desktop e mobile
- **Sidebar colapsível** - Menu lateral adaptável
- **Estados de loading** - Indicadores visuais durante carregamento
- **Tratamento de erros** - Mensagens de erro amigáveis
- **Formatação automática** - CPF, telefone e valores monetários
- **Busca em tempo real** - Filtro instantâneo nas listagens
- **Confirmação de ações** - Prompts antes de ações críticas
- **Simulações financeiras** - Cálculos em tempo real
- **Badges de status** - Indicadores visuais coloridos
- **Ícones contextuais** - Interface intuitiva

## 📋 **Requisitos Atendidos**

### **Frontend ✅**
- [x] Página inicial com lista de itens
- [x] Páginas de detalhes de itens individuais
- [x] Formulários para adicionar e editar itens
- [x] Funcionalidade de excluir com confirmação
- [x] Funcionalidade de busca de itens

### **Integração ✅**
- [x] Conexão com API de backend
- [x] Implementação CRUD via chamadas de API
- [x] Tratamento de erros no frontend

### **Módulos Implementados ✅**
- [x] **Clientes** - Gestão completa
- [x] **Contas** - Corrente e poupança
- [x] **Cartões** - Crédito e débito
- [x] **Transações** - Depósito, saque e transferência
- [x] **Empréstimos** - Solicitação, aprovação e rejeição

## 🔄 **Funcionalidades Avançadas**

### **Dashboard Inteligente**
- Estatísticas em tempo real
- Contadores por módulo
- Visão geral do sistema

### **Gestão de Estados**
- Estados de loading
- Tratamento de erros
- Feedback visual

### **Simulações Financeiras**
- Cálculo de parcelas de empréstimo
- Simulação de juros compostos
- Resumo de transações

### **Ações Contextuais**
- Bloqueio/desbloqueio de cartões
- Aprovação/rejeição de empréstimos
- Navegação entre entidades relacionadas

## 🚀 **Próximas Melhorias**

1. **Funcionalidades Avançadas:**
   - Paginação nas listagens
   - Ordenação de colunas
   - Filtros avançados
   - Exportação de dados

2. **Segurança:**
   - Autenticação de usuários
   - Permissões por perfil
   - Criptografia de dados sensíveis

3. **Relatórios:**
   - Relatórios financeiros
   - Dashboards analíticos
   - Gráficos e visualizações

4. **Notificações:**
   - Alertas em tempo real
   - Notificações push
   - Sistema de mensagens

## 👨‍💻 **Autor**

Desenvolvido para a disciplina 55DSW - Desenvolvimento de Sistemas para Web  
Prof. Paulo Roberto Farah

---

## 📝 **Notas de Desenvolvimento**

- **Arquitetura:** Componentes reutilizáveis e modulares
- **Estado:** Gerenciamento local com useState
- **Estilização:** Tailwind CSS com componentes shadcn/ui
- **Responsividade:** Mobile-first design
- **Performance:** Lazy loading e otimizações
- **Acessibilidade:** Componentes acessíveis por padrão

