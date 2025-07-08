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
  Mail,
  Phone,
  MapPin,
  Calendar
} from 'lucide-react';
import ApiService from '../services/api';

const ClientesList = ({ onClienteSelect, onCreateCliente }) => {
  const [clientes, setClientes] = useState([]);
  const [filteredClientes, setFilteredClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchClientes();
  }, []);

  useEffect(() => {
    const filtered = clientes.filter(cliente =>
      cliente.nome?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cliente.cpf?.includes(searchTerm) ||
      cliente.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredClientes(filtered);
  }, [clientes, searchTerm]);

  const fetchClientes = async () => {
    try {
      setLoading(true);
      const data = await ApiService.getClientes();
      setClientes(data || []);
    } catch (error) {
      console.error('Erro ao carregar clientes:', error);
      setClientes([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await ApiService.deleteCliente(id);
        await fetchClientes();
      } catch (error) {
        console.error('Erro ao excluir cliente:', error);
        alert('Erro ao excluir cliente. Tente novamente.');
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

  const formatCPF = (cpf) => {
    if (!cpf) return 'N/A';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Clientes</h2>
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
        <h2 className="text-2xl font-bold text-gray-900">Clientes</h2>
        <Button onClick={onCreateCliente} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Novo Cliente
        </Button>
      </div>

      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Buscar por nome, CPF ou email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Badge variant="secondary">
          {filteredClientes.length} cliente(s)
        </Badge>
      </div>

      {filteredClientes.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-gray-400 mb-4">
              <Search className="h-12 w-12" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              {searchTerm ? 'Nenhum cliente encontrado' : 'Nenhum cliente cadastrado'}
            </h3>
            <p className="text-gray-600 text-center mb-4">
              {searchTerm 
                ? 'Tente ajustar os termos de busca'
                : 'Comece cadastrando o primeiro cliente do sistema'
              }
            </p>
            {!searchTerm && (
              <Button onClick={onCreateCliente}>
                <Plus className="h-4 w-4 mr-2" />
                Cadastrar Cliente
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredClientes.map((cliente) => (
            <Card key={cliente.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-lg">{cliente.nome || 'Nome não informado'}</CardTitle>
                    <p className="text-sm text-gray-600">CPF: {formatCPF(cliente.cpf)}</p>
                  </div>
                  <Badge variant="outline">ID: {cliente.id}</Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="space-y-2">
                  {cliente.email && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Mail className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span className="truncate">{cliente.email}</span>
                    </div>
                  )}
                  {cliente.telefone && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Phone className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>{cliente.telefone}</span>
                    </div>
                  )}
                  {cliente.endereco && (
                    <div className="flex items-center text-sm text-gray-600">
                      <MapPin className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span className="truncate">{cliente.endereco}</span>
                    </div>
                  )}
                  {cliente.dataNascimento && (
                    <div className="flex items-center text-sm text-gray-600">
                      <Calendar className="h-4 w-4 mr-2 flex-shrink-0" />
                      <span>Nascimento: {formatDate(cliente.dataNascimento)}</span>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-3 border-t">
                  <div className="flex space-x-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onClienteSelect(cliente)}
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onClienteSelect(cliente, 'edit')}
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleDelete(cliente.id)}
                    className="text-red-600 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="h-4 w-4" />
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

export default ClientesList;

