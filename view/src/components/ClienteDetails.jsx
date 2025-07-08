import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  ArrowLeft, 
  Edit, 
  User, 
  Mail, 
  Phone, 
  MapPin, 
  Calendar,
  CreditCard,
  Building2,
  Hash
} from 'lucide-react';

const ClienteDetails = ({ cliente, onBack, onEdit }) => {
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

  const formatPhone = (phone) => {
    if (!phone) return 'N/A';
    const numbers = phone.replace(/\D/g, '');
    if (numbers.length <= 10) {
      return numbers.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }
    return numbers.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
  };

  const infoItems = [
    {
      label: 'Email',
      value: cliente.email || 'N/A',
      icon: Mail,
    },
    {
      label: 'Telefone',
      value: formatPhone(cliente.telefone),
      icon: Phone,
    },
    {
      label: 'Endereço',
      value: cliente.endereco || 'N/A',
      icon: MapPin,
    },
    {
      label: 'Data de Nascimento',
      value: formatDate(cliente.dataNascimento),
      icon: Calendar,
    },
    {
      label: 'Login',
      value: cliente.loginUsuario || 'N/A',
      icon: User,
    },
    {
      label: 'Data de Cadastro',
      value: formatDate(cliente.dataCadastro),
      icon: Calendar,
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button variant="outline" onClick={onBack}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Voltar
          </Button>
          <h2 className="text-2xl font-bold text-gray-900">Detalhes do Cliente</h2>
        </div>
        <Button onClick={() => onEdit(cliente)}>
          <Edit className="h-4 w-4 mr-2" />
          Editar
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Informações Principais */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Informações Pessoais
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      {cliente.nome || 'Nome não informado'}
                    </h3>
                    <p className="text-gray-600">CPF: {formatCPF(cliente.cpf)}</p>
                  </div>
                  <Badge variant="outline" className="text-sm">
                    ID: {cliente.id}
                  </Badge>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {infoItems.map((item, index) => {
                    const Icon = item.icon;
                    return (
                      <div key={index} className="flex items-start space-x-3">
                        <div className="p-2 bg-gray-100 rounded-lg">
                          <Icon className="h-4 w-4 text-gray-600" />
                        </div>
                        <div>
                          <p className="text-sm font-medium text-gray-900">
                            {item.label}
                          </p>
                          <p className="text-sm text-gray-600 break-words">
                            {item.value}
                          </p>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Contas Associadas */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building2 className="h-5 w-5" />
                Contas Associadas
              </CardTitle>
            </CardHeader>
            <CardContent>
              {cliente.titularidades && cliente.titularidades.length > 0 ? (
                <div className="space-y-3">
                  {cliente.titularidades.map((titularidade, index) => (
                    <div key={index} className="p-4 bg-gray-50 rounded-lg">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="font-medium">
                            Conta: {titularidade.conta?.numeroConta || 'N/A'}
                          </p>
                          <p className="text-sm text-gray-600">
                            Agência: {titularidade.conta?.agencia || 'N/A'}
                          </p>
                        </div>
                        <Badge variant="secondary">
                          {titularidade.conta?.statusConta || 'Ativa'}
                        </Badge>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8">
                  <Building2 className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">Nenhuma conta associada</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar com Resumo */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Resumo</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Status</span>
                <Badge variant="default">Ativo</Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Contas</span>
                <span className="font-medium">
                  {cliente.titularidades?.length || 0}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Beneficiários</span>
                <span className="font-medium">
                  {cliente.beneficiarios?.length || 0}
                </span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Hash className="h-4 w-4" />
                Identificação
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">ID do Cliente</p>
                <p className="font-mono text-lg">{cliente.id}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">CPF</p>
                <p className="font-mono">{formatCPF(cliente.cpf)}</p>
              </div>
            </CardContent>
          </Card>

          {/* Ações Rápidas */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Ações Rápidas</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <Button variant="outline" className="w-full justify-start">
                <Building2 className="h-4 w-4 mr-2" />
                Criar Conta
              </Button>
              <Button variant="outline" className="w-full justify-start">
                <CreditCard className="h-4 w-4 mr-2" />
                Emitir Cartão
              </Button>
              <Button variant="outline" className="w-full justify-start">
                <User className="h-4 w-4 mr-2" />
                Adicionar Beneficiário
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default ClienteDetails;

