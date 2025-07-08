const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      console.log('🔄 Fazendo requisição para:', url);
      console.log('📋 Config:', config);
      
      const response = await fetch(url, config);
      
      console.log('📊 Status da resposta:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      // 🔍 ADICIONADO: Pega o texto bruto primeiro para debug
      const rawText = await response.text();
      console.log('📄 Resposta bruta da API:', rawText);
      
      // Verifica se está vazio
      if (!rawText || rawText.trim() === '') {
        console.log('⚠️ Resposta vazia');
        return [];
      }
      
      // Tenta parsear o JSON
      try {
        const jsonData = JSON.parse(rawText);
        console.log('✅ JSON parseado com sucesso:', jsonData);
        return jsonData;
      } catch (parseError) {
        console.error('❌ ERRO JSON:', parseError.message);
        console.error('📄 Texto problemático:', rawText);
        
        // Mostra onde está o erro no JSON
        const errorPosition = parseError.message.match(/position (\d+)/);
        if (errorPosition) {
          const pos = parseInt(errorPosition[1]);
          const start = Math.max(0, pos - 50);
          const end = Math.min(rawText.length, pos + 50);
          console.error('🎯 Área problemática:', rawText.substring(start, end));
          console.error('    '.repeat(pos - start) + '^^^');
        }
        
        throw new Error(`JSON inválido da API: ${parseError.message}`);
      }
      
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // Métodos para Clientes
  async getClientes() {
    return this.request('/clientes');
  }

  async getCliente(id) {
    return this.request(`/clientes/${id}`);
  }

  async createCliente(cliente) {
    return this.request('/clientes', {
      method: 'POST',
      body: JSON.stringify(cliente),
    });
  }

  async updateCliente(id, cliente) {
    return this.request(`/clientes/${id}`, {
      method: 'PUT',
      body: JSON.stringify(cliente),
    });
  }

  async deleteCliente(id) {
    return this.request(`/clientes/${id}`, {
      method: 'DELETE',
    });
  }

  // Métodos para Contas
  async getContas() {
    return this.request('/contas');
  }

  async getConta(id) {
    return this.request(`/contas/${id}`);
  }

  async createContaCorrente(conta) {
    return this.request('/contas/corrente', {
      method: 'POST',
      body: JSON.stringify(conta),
    });
  }

  async createContaPoupanca(conta) {
    return this.request('/contas/poupanca', {
      method: 'POST',
      body: JSON.stringify(conta),
    });
  }

  // Métodos para Cartões
  async getCartoes() {
    return this.request('/cartoes');
  }

  async getCartao(id) {
    return this.request(`/cartoes/${id}`);
  }

  async createCartaoCredito(cartao) {
    return this.request('/cartoes/credito', {
      method: 'POST',
      body: JSON.stringify(cartao),
    });
  }

  async createCartaoDebito(cartao) {
    return this.request('/cartoes/debito', {
      method: 'POST',
      body: JSON.stringify(cartao),
    });
  }

  async bloquearCartao(id) {
    return this.request(`/cartoes/${id}/bloquear`, {
      method: 'POST',
    });
  }

  async desbloquearCartao(id) {
    return this.request(`/cartoes/${id}/desbloquear`, {
      method: 'POST',
    });
  }

  // Métodos para Transações
  async getTransacoes() {
    return this.request('/transacoes');
  }

  async getTransacao(id) {
    return this.request(`/transacoes/${id}`);
  }

  async createTransacao(transacao) {
    return this.request('/transacoes', {
      method: 'POST',
      body: JSON.stringify(transacao),
    });
  }

  async getExtratoConta(contaId) {
    return this.request(`/transacoes/extrato/${contaId}`);
  }

  // Métodos para Empréstimos
  async getEmprestimos() {
    return this.request('/emprestimos');
  }

  async getEmprestimo(id) {
    return this.request(`/emprestimos/${id}`);
  }

  async createEmprestimo(emprestimo) {
    return this.request('/emprestimos', {
      method: 'POST',
      body: JSON.stringify(emprestimo),
    });
  }

  async aprovarEmprestimo(id, valorAprovado) {
    return this.request(`/emprestimos/${id}/aprovar`, {
      method: 'POST',
      body: JSON.stringify({ valorAprovado }),
    });
  }

  async rejeitarEmprestimo(id, motivo) {
    return this.request(`/emprestimos/${id}/rejeitar`, {
      method: 'POST',
      body: JSON.stringify({ motivo }),
    });
  }
}

export default new ApiService();