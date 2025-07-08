import { useState } from 'react';
import Layout from './components/Layout';
import Dashboard from './components/Dashboard';
import ClientesList from './components/ClientesList';
import ClienteForm from './components/ClienteForm';
import ClienteDetails from './components/ClienteDetails';
import ContasList from './components/ContasList';
import ContaForm from './components/ContaForm';
import ContaDetails from './components/ContaDetails';
import CartoesList from './components/CartoesList';
import CartaoForm from './components/CartaoForm';
import CartaoDetails from './components/CartaoDetails';
import TransacoesList from './components/TransacoesList';
import TransacaoForm from './components/TransacaoForm';
import TransacaoDetails from './components/TransacaoDetails';
import EmprestimosList from './components/EmprestimosList';
import EmprestimoForm from './components/EmprestimoForm';
import EmprestimoDetails from './components/EmprestimoDetails';
import './App.css';

function App() {
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedCliente, setSelectedCliente] = useState(null);
  const [clienteMode, setClienteMode] = useState('list'); // 'list', 'create', 'edit', 'view'
  const [selectedConta, setSelectedConta] = useState(null);
  const [contaMode, setContaMode] = useState('list'); // 'list', 'create', 'view'
  const [selectedCartao, setSelectedCartao] = useState(null);
  const [cartaoMode, setCartaoMode] = useState('list'); // 'list', 'create', 'view'
  const [selectedTransacao, setSelectedTransacao] = useState(null);
  const [transacaoMode, setTransacaoMode] = useState('list'); // 'list', 'create', 'view'
  const [selectedEmprestimo, setSelectedEmprestimo] = useState(null);
  const [emprestimoMode, setEmprestimoMode] = useState('list'); // 'list', 'create', 'view'

  const handlePageChange = (page) => {
    setCurrentPage(page);
    setSelectedCliente(null);
    setClienteMode('list');
    setSelectedConta(null);
    setContaMode('list');
    setSelectedCartao(null);
    setCartaoMode('list');
    setSelectedTransacao(null);
    setTransacaoMode('list');
    setSelectedEmprestimo(null);
    setEmprestimoMode('list');
  };

  // Cliente Handlers
  const handleClienteSelect = (cliente, mode = 'view') => {
    setSelectedCliente(cliente);
    setClienteMode(mode);
  };

  const handleCreateCliente = () => {
    setSelectedCliente(null);
    setClienteMode('create');
  };

  const handleClienteBack = () => {
    setSelectedCliente(null);
    setClienteMode('list');
  };

  const handleClienteSave = (cliente) => {
    setSelectedCliente(cliente);
    setClienteMode('view');
  };

  const handleClienteEdit = (cliente) => {
    setSelectedCliente(cliente);
    setClienteMode('edit');
  };

  // Conta Handlers
  const handleContaSelect = (conta, mode = 'view') => {
    setSelectedConta(conta);
    setContaMode(mode);
  };

  const handleCreateConta = () => {
    setSelectedConta(null);
    setContaMode('create');
  };

  const handleContaBack = () => {
    setSelectedConta(null);
    setContaMode('list');
  };

  const handleContaSave = (conta) => {
    setSelectedConta(conta);
    setContaMode('view');
  };

  // Cartão Handlers
  const handleCartaoSelect = (cartao, mode = 'view') => {
    setSelectedCartao(cartao);
    setCartaoMode(mode);
  };

  const handleCreateCartao = () => {
    setSelectedCartao(null);
    setCartaoMode('create');
  };

  const handleCartaoBack = () => {
    setSelectedCartao(null);
    setCartaoMode('list');
  };

  const handleCartaoSave = (cartao) => {
    setSelectedCartao(cartao);
    setCartaoMode('view');
  };

  // Transação Handlers
  const handleTransacaoSelect = (transacao, mode = 'view') => {
    setSelectedTransacao(transacao);
    setTransacaoMode(mode);
  };

  const handleCreateTransacao = () => {
    setSelectedTransacao(null);
    setTransacaoMode('create');
  };

  const handleTransacaoBack = () => {
    setSelectedTransacao(null);
    setTransacaoMode('list');
  };

  const handleTransacaoSave = (transacao) => {
    setSelectedTransacao(transacao);
    setTransacaoMode('view');
  };

  // Empréstimo Handlers
  const handleEmprestimoSelect = (emprestimo, mode = 'view') => {
    setSelectedEmprestimo(emprestimo);
    setEmprestimoMode(mode);
  };

  const handleCreateEmprestimo = () => {
    setSelectedEmprestimo(null);
    setEmprestimoMode('create');
  };

  const handleEmprestimoBack = () => {
    setSelectedEmprestimo(null);
    setEmprestimoMode('list');
  };

  const handleEmprestimoSave = (emprestimo) => {
    setSelectedEmprestimo(emprestimo);
    setEmprestimoMode('view');
  };

  const renderContent = () => {
    switch (currentPage) {
      case 'dashboard':
        return <Dashboard />;
      
      case 'clientes':
        switch (clienteMode) {
          case 'create':
            return (
              <ClienteForm
                onBack={handleClienteBack}
                onSave={handleClienteSave}
              />
            );
          case 'edit':
            return (
              <ClienteForm
                cliente={selectedCliente}
                onBack={handleClienteBack}
                onSave={handleClienteSave}
              />
            );
          case 'view':
            return (
              <ClienteDetails
                cliente={selectedCliente}
                onBack={handleClienteBack}
                onEdit={handleClienteEdit}
              />
            );
          default:
            return (
              <ClientesList
                onClienteSelect={handleClienteSelect}
                onCreateCliente={handleCreateCliente}
              />
            );
        }
      
      case 'contas':
        switch (contaMode) {
          case 'create':
            return (
              <ContaForm
                onBack={handleContaBack}
                onSave={handleContaSave}
              />
            );
          case 'view':
            return (
              <ContaDetails
                conta={selectedConta}
                onBack={handleContaBack}
              />
            );
          default:
            return (
              <ContasList
                onContaSelect={handleContaSelect}
                onCreateConta={handleCreateConta}
              />
            );
        }
      
      case 'cartoes':
        switch (cartaoMode) {
          case 'create':
            return (
              <CartaoForm
                onBack={handleCartaoBack}
                onSave={handleCartaoSave}
              />
            );
          case 'view':
            return (
              <CartaoDetails
                cartao={selectedCartao}
                onBack={handleCartaoBack}
              />
            );
          default:
            return (
              <CartoesList
                onCartaoSelect={handleCartaoSelect}
                onCreateCartao={handleCreateCartao}
              />
            );
        }
      
      case 'transacoes':
        switch (transacaoMode) {
          case 'create':
            return (
              <TransacaoForm
                onBack={handleTransacaoBack}
                onSave={handleTransacaoSave}
              />
            );
          case 'view':
            return (
              <TransacaoDetails
                transacao={selectedTransacao}
                onBack={handleTransacaoBack}
              />
            );
          default:
            return (
              <TransacoesList
                onTransacaoSelect={handleTransacaoSelect}
                onCreateTransacao={handleCreateTransacao}
              />
            );
        }
      
      case 'emprestimos':
        switch (emprestimoMode) {
          case 'create':
            return (
              <EmprestimoForm
                onBack={handleEmprestimoBack}
                onSave={handleEmprestimoSave}
              />
            );
          case 'view':
            return (
              <EmprestimoDetails
                emprestimo={selectedEmprestimo}
                onBack={handleEmprestimoBack}
              />
            );
          default:
            return (
              <EmprestimosList
                onEmprestimoSelect={handleEmprestimoSelect}
                onCreateEmprestimo={handleCreateEmprestimo}
              />
            );
        }
      
      default:
        return <Dashboard />;
    }
  };

  return (
    <Layout currentPage={currentPage} onPageChange={handlePageChange}>
      {renderContent()}
    </Layout>
  );
}

export default App;

