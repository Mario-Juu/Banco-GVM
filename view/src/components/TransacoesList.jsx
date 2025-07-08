import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Plus,
  Search,
  Eye,
  ArrowUpRight,
  ArrowDownLeft,
  DollarSign,
  Calendar,
  Hash,
  Building2
} from 'lucide-react';
import ApiService from '../services/api';

const TransacoesList = ({ onTransacaoSelect, onCreateTransacao }) => {
  const [transacoes, setTransacoes] = useState([]);
  const [filteredTransacoes, setFilteredTransacoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchTransacoes();
  }, []);

  useEffect(() => {
    const filtered = transacoes.filter(transacao =>
      transacao.descricao?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transacao.tipoTransacao?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transacao.statusTransacao?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredTransacoes(filtered);
  }, [transacoes, searchTerm]);

  const fetchTransacoes = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getTransacoes();
      setTransacoes(data || []);
    } catch (error) {
      console.error('Erro ao carregar transações:', error);
      setTransacoes([]);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleString('pt-BR');
    } catch {
      return 'N/A';
    }
  };

  const formatCurrency = (value) => {
    if (value === undefined || value === null) return 'N/A';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONCLUIDA':
        return 'bg-green-100 text-green-800';
      case 'PENDENTE':
        return 'bg-yellow-100 text-yellow-800';
      case 'FALHOU':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTipoIcon = (tipo) => {
    switch (tipo) {
      case 'DEPOSITO':
        return <ArrowDownLeft className="h-4 w-4 text-green-600" />;
      case 'SAQUE':
        return <ArrowUpRight className="h-4 w-4 text-red-600" />;
      case 'TRANSFERENCIA':
        return <ArrowUpRight className="h-4 w-4 text-blue-600" />;
      default:
        return <DollarSign className="h-4 w-4 text-gray-600" />;
    }
  };

  const getTipoLabel = (tipo) => {
    switch (tipo) {
      case 'DEPOSITO':
        return 'Depósito';
      case 'SAQUE':
        return 'Saque';
      case 'TRANSFERENCIA':
        return 'Transferência';
      default:
        return tipo || 'N/A';
    }
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Transações</h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Card key={i} className="animate-pulse">
              <CardHeader>
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2"></div>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="h-3 bg-gray-200 rounded"></div>
                  <div className="h-3 bg-gray-200 rounded w-2/3"></div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Transações</h2>
        <Button onClick={onCreateTransacao} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Nova Transação
        </Button>
      </div>

      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Buscar por descrição, tipo ou status..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Badge variant="secondary">
          {filteredTransacoes.length} transação(ões)
        </Badge>
      </div>

      {filteredTransacoes.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-gray-400 mb-4">
              <Search className="h-12 w-12" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm ? 'Nenhuma transação encontrada' : 'Nenhuma transação registrada'}
            </h3>
            <p className="text-gray-600 text-center mb-4">
              {searchTerm
                ? 'Tente ajustar os termos de busca'
                : 'Comece registrando a primeira transação do sistema'
              }
            </p>
            {!searchTerm && (
              <Button onClick={onCreateTransacao}>
                <Plus className="h-4 w-4 mr-2" />
                Registrar Transação
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredTransacoes.map((transacao) => (
            <Card key={transacao.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex items-center space-x-2">
                    {getTipoIcon(transacao.tipoTransacao)}
                    <div>
                      <CardTitle className="text-lg">{getTipoLabel(transacao.tipoTransacao)}</CardTitle>
                      <p className="text-sm text-gray-600">{formatCurrency(transacao.valor)}</p>
                    </div>
                  </div>
                  <Badge variant="outline">ID: {transacao.id}</Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2">
                  <div className="flex items-center text-sm text-gray-600">
                    <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>{formatDate(transacao.dataHora)}</span>
                  </div>
                  {transacao.descricao && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Hash className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span className="truncate">{transacao.descricao}</span>
                    </div>
                  )}
                  {transacao.contaOrigem && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Building2 className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Origem: {transacao.contaOrigem.numeroConta}</span>
                    </div>
                  )}
                  {transacao.contaDestino && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Building2 className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Destino: {transacao.contaDestino.numeroConta}</span>
                    </div>
                  )}
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-600">Status:</span>
                    <Badge className={getStatusColor(transacao.statusTransacao)}>
                      {transacao.statusTransacao || 'N/A'}
                    </Badge>
                  </div>
                </div>

                <div className="flex items-center justify-end pt-3 border-t">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onTransacaoSelect(transacao)}
                  >
                    <Eye className="h-4 w-4" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default TransacoesList;

