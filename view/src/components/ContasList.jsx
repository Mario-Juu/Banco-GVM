import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Plus,
  Search,
  Edit,
  Trash2,
  Eye,
  Building2,
  DollarSign,
  Calendar,
  Hash,
  Banknote
} from 'lucide-react';
import ApiService from '../services/api';

const ContasList = ({ onContaSelect, onCreateConta }) => {
  const [contas, setContas] = useState([]);
  const [filteredContas, setFilteredContas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchContas();
  }, []);

  useEffect(() => {
    const filtered = contas.filter(conta =>
      conta.numeroConta?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      conta.agencia?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      conta.statusConta?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredContas(filtered);
  }, [contas, searchTerm]);

  const fetchContas = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getContas();
      setContas(data || []);
    } catch (error) {
      console.error('Erro ao carregar contas:', error);
      setContas([]);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('pt-BR');
    } catch {
      return 'N/A';
    }
  };

  const formatCurrency = (value) => {
    if (value === undefined || value === null) return 'N/A';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Contas</h2>
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
        <h2 className="text-2xl font-bold text-gray-900">Contas</h2>
        <Button onClick={onCreateConta} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Nova Conta
        </Button>
      </div>

      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Buscar por número, agência ou status..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Badge variant="secondary">
          {filteredContas.length} conta(s)
        </Badge>
      </div>

      {filteredContas.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-gray-400 mb-4">
              <Search className="h-12 w-12" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm ? 'Nenhuma conta encontrada' : 'Nenhuma conta cadastrada'}
            </h3>
            <p className="text-gray-600 text-center mb-4">
              {searchTerm
                ? 'Tente ajustar os termos de busca'
                : 'Comece cadastrando a primeira conta do sistema'
              }
            </p>
            {!searchTerm && (
              <Button onClick={onCreateConta}>
                <Plus className="h-4 w-4 mr-2" />
                Cadastrar Conta
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredContas.map((conta) => (
            <Card key={conta.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-lg">Conta: {conta.numeroConta || 'N/A'}</CardTitle>
                    <p className="text-sm text-gray-600">Agência: {conta.agencia || 'N/A'}</p>
                  </div>
                  <Badge variant="outline">ID: {conta.id}</Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2">
                  <div className="flex items-center text-sm text-gray-600">
                    <DollarSign className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Saldo: {formatCurrency(conta.saldo)}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Abertura: {formatDate(conta.dataAbertura)}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Hash className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Status: {conta.statusConta || 'N/A'}</span>
                  </div>
                  {conta.tipoConta && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Banknote className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Tipo: {conta.tipoConta === 'CORRENTE' ? 'Corrente' : 'Poupança'}</span>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-end space-x-2 pt-3 border-t">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onContaSelect(conta)}
                  >
                    <Eye className="h-4 w-4" />
                  </Button>
                  {/* Edição e Exclusão de contas podem ser mais complexas devido a transações */}
                  {/* <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onContaSelect(conta, 'edit')}
                  >
                    <Edit className="h-4 w-4" />
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleDelete(conta.id)}
                    className="text-red-600 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button> */}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default ContasList;

