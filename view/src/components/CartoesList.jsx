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
  CreditCard,
  DollarSign,
  Calendar,
  Hash,
  Lock,
  Unlock
} from 'lucide-react';
import ApiService from '../services/api';

const CartoesList = ({ onCartaoSelect, onCreateCartao }) => {
  const [cartoes, setCartoes] = useState([]);
  const [filteredCartoes, setFilteredCartoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchCartoes();
  }, []);

  useEffect(() => {
    const filtered = cartoes.filter(cartao =>
      cartao.numeroCartao?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cartao.nomeTitular?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cartao.statusCartao?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredCartoes(filtered);
  }, [cartoes, searchTerm]);

  const fetchCartoes = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getCartoes();
      setCartoes(data || []);
    } catch (error) {
      console.error('Erro ao carregar cartões:', error);
      setCartoes([]);
    } finally {
      setLoading(false);
    }
  };

  const handleBloquear = async (id) => {
    if (window.confirm('Tem certeza que deseja bloquear este cartão?')) {
      try {
        await ApiService.bloquearCartao(id);
        await fetchCartoes();
      } catch (error) {
        console.error('Erro ao bloquear cartão:', error);
        alert('Erro ao bloquear cartão. Tente novamente.');
      }
    }
  };

  const handleDesbloquear = async (id) => {
    if (window.confirm('Tem certeza que deseja desbloquear este cartão?')) {
      try {
        await ApiService.desbloquearCartao(id);
        await fetchCartoes();
      } catch (error) {
        console.error('Erro ao desbloquear cartão:', error);
        alert('Erro ao desbloquear cartão. Tente novamente.');
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

  const formatCardNumber = (number) => {
    if (!number) return 'N/A';
    return number.replace(/(\d{4})(\d{4})(\d{4})(\d{4})/, '**** **** **** $4');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ATIVO':
        return 'bg-green-100 text-green-800';
      case 'BLOQUEADO':
        return 'bg-red-100 text-red-800';
      case 'INATIVO':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Cartões</h2>
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
        <h2 className="text-2xl font-bold text-gray-900">Cartões</h2>
        <Button onClick={onCreateCartao} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Novo Cartão
        </Button>
      </div>

      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Buscar por número, titular ou status..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Badge variant="secondary">
          {filteredCartoes.length} cartão(ões)
        </Badge>
      </div>

      {filteredCartoes.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-gray-400 mb-4">
              <Search className="h-12 w-12" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm ? 'Nenhum cartão encontrado' : 'Nenhum cartão cadastrado'}
            </h3>
            <p className="text-gray-600 text-center mb-4">
              {searchTerm
                ? 'Tente ajustar os termos de busca'
                : 'Comece emitindo o primeiro cartão do sistema'
              }
            </p>
            {!searchTerm && (
              <Button onClick={onCreateCartao}>
                <Plus className="h-4 w-4 mr-2" />
                Emitir Cartão
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCartoes.map((cartao) => (
            <Card key={cartao.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-lg">{formatCardNumber(cartao.numeroCartao)}</CardTitle>
                    <p className="text-sm text-gray-600">{cartao.nomeTitular || 'N/A'}</p>
                  </div>
                  <Badge variant="outline">ID: {cartao.id}</Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2">
                  <div className="flex items-center text-sm text-gray-600">
                    <CreditCard className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Tipo: {cartao.tipoCartao === 'CREDITO' ? 'Crédito' : 'Débito'}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Validade: {formatDate(cartao.dataValidade)}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                    <span>Emissão: {formatDate(cartao.dataEmissao)}</span>
                  </div>
                  {cartao.limiteCredito && (
                    <div className="flex items-center text-sm text-gray-600">
                      <DollarSign className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Limite: {formatCurrency(cartao.limiteCredito)}</span>
                    </div>
                  )}
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-600">Status:</span>
                    <Badge className={getStatusColor(cartao.statusCartao)}>
                      {cartao.statusCartao || 'N/A'}
                    </Badge>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t">
                  <div className="flex space-x-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onCartaoSelect(cartao)}
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    {cartao.statusCartao === 'ATIVO' ? (
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleBloquear(cartao.id)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50"
                      >
                        <Lock className="h-4 w-4" />
                      </Button>
                    ) : (
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleDesbloquear(cartao.id)}
                        className="text-green-600 hover:text-green-700 hover:bg-green-50"
                      >
                        <Unlock className="h-4 w-4" />
                      </Button>
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

export default CartoesList;

