import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Plus,
  Search,
  Eye,
  DollarSign,
  Calendar,
  Hash,
  User,
  CheckCircle,
  XCircle,
  Clock
} from 'lucide-react';
import ApiService from '../services/api';

const EmprestimosList = ({ onEmprestimoSelect, onCreateEmprestimo }) => {
  const [emprestimos, setEmprestimos] = useState([]);
  const [filteredEmprestimos, setFilteredEmprestimos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchEmprestimos();
  }, []);

  useEffect(() => {
    const filtered = emprestimos.filter(emprestimo =>
      emprestimo.cliente?.nome?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      emprestimo.cliente?.cpf?.includes(searchTerm) ||
      emprestimo.statusEmprestimo?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredEmprestimos(filtered);
  }, [emprestimos, searchTerm]);

  const fetchEmprestimos = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getEmprestimos();
      setEmprestimos(data || []);
    } catch (error) {
      console.error('Erro ao carregar empréstimos:', error);
      setEmprestimos([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAprovar = async (id) => {
    const valorAprovado = prompt('Digite o valor aprovado:');
    if (valorAprovado && !isNaN(parseFloat(valorAprovado))) {
      try {
        await ApiService.aprovarEmprestimo(id, parseFloat(valorAprovado));
        await fetchEmprestimos();
      } catch (error) {
        console.error('Erro ao aprovar empréstimo:', error);
        alert('Erro ao aprovar empréstimo. Tente novamente.');
      }
    }
  };

  const handleRejeitar = async (id) => {
    const motivo = prompt('Digite o motivo da rejeição:');
    if (motivo) {
      try {
        await ApiService.rejeitarEmprestimo(id, motivo);
        await fetchEmprestimos();
      } catch (error) {
        console.error('Erro ao rejeitar empréstimo:', error);
        alert('Erro ao rejeitar empréstimo. Tente novamente.');
      }
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

  const getStatusColor = (status) => {
    switch (status) {
      case 'APROVADO':
        return 'bg-green-100 text-green-800';
      case 'PENDENTE':
        return 'bg-yellow-100 text-yellow-800';
      case 'REJEITADO':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'APROVADO':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      case 'PENDENTE':
        return <Clock className="h-4 w-4 text-yellow-600" />;
      case 'REJEITADO':
        return <XCircle className="h-4 w-4 text-red-600" />;
      default:
        return <Hash className="h-4 w-4 text-gray-600" />;
    }
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Empréstimos</h2>
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
        <h2 className="text-2xl font-bold text-gray-900">Empréstimos</h2>
        <Button onClick={onCreateEmprestimo} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Novo Empréstimo
        </Button>
      </div>

      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Buscar por cliente, CPF ou status..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Badge variant="secondary">
          {filteredEmprestimos.length} empréstimo(s)
        </Badge>
      </div>

      {filteredEmprestimos.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-gray-400 mb-4">
              <Search className="h-12 w-12" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm ? 'Nenhum empréstimo encontrado' : 'Nenhum empréstimo solicitado'}
            </h3>
            <p className="text-gray-600 text-center mb-4">
              {searchTerm
                ? 'Tente ajustar os termos de busca'
                : 'Comece registrando a primeira solicitação de empréstimo'
              }
            </p>
            {!searchTerm && (
              <Button onClick={onCreateEmprestimo}>
                <Plus className="h-4 w-4 mr-2" />
                Solicitar Empréstimo
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredEmprestimos.map((emprestimo) => (
            <Card key={emprestimo.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex items-center space-x-2">
                    {getStatusIcon(emprestimo.statusEmprestimo)}
                    <div>
                      <CardTitle className="text-lg">{formatCurrency(emprestimo.valorSolicitado)}</CardTitle>
                      <p className="text-sm text-gray-600">{emprestimo.cliente?.nome || 'N/A'}</p>
                    </div>
                  </div>
                  <Badge variant="outline">ID: {emprestimo.id}</Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2">
                  <div className="flex items-center text-sm text-gray-600">
                    <User className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>CPF: {emprestimo.cliente?.cpf || 'N/A'}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Solicitação: {formatDate(emprestimo.dataSolicitacao)}</span>
                  </div>
                  {emprestimo.valorAprovado && (
                    <div className="flex items-center text-sm text-gray-600">
                      <DollarSign className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Aprovado: {formatCurrency(emprestimo.valorAprovado)}</span>
                    </div>
                  )}
                  {emprestimo.numeroParcelas && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Hash className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Parcelas: {emprestimo.numeroParcelas}x</span>
                    </div>
                  )}
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-600">Status:</span>
                    <Badge className={getStatusColor(emprestimo.statusEmprestimo)}>
                      {emprestimo.statusEmprestimo || 'N/A'}
                    </Badge>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t">
                  <div className="flex space-x-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onEmprestimoSelect(emprestimo)}
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    {emprestimo.statusEmprestimo === 'PENDENTE' && (
                      <>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => handleAprovar(emprestimo.id)}
                          className="text-green-600 hover:text-green-700 hover:bg-green-50"
                        >
                          <CheckCircle className="h-4 w-4" />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => handleRejeitar(emprestimo.id)}
                          className="text-red-600 hover:text-red-700 hover:bg-red-50"
                        >
                          <XCircle className="h-4 w-4" />
                        </Button>
                      </>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default EmprestimosList;

