import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
  ArrowLeft,
  CreditCard,
  DollarSign,
  Calendar,
  Hash,
  Building2,
  Lock,
  Unlock,
  Shield
} from 'lucide-react';

const CartaoDetails = ({ cartao, onBack }) => {
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

  const infoItems = [
    {
      label: 'Número do Cartão',
      value: formatCardNumber(cartao.numeroCartao),
      icon: CreditCard,
    },
    {
      label: 'Nome do Titular',
      value: cartao.nomeTitular || 'N/A',
      icon: Hash,
    },
    {
      label: 'Tipo de Cartão',
      value: cartao.tipoCartao === 'CREDITO' ? 'Crédito' : 'Débito',
      icon: CreditCard,
    },
    {
      label: 'Data de Emissão',
      value: formatDate(cartao.dataEmissao),
      icon: Calendar,
    },
    {
      label: 'Data de Validade',
      value: formatDate(cartao.dataValidade),
      icon: Calendar,
    },
    {
      label: 'CVV',
      value: cartao.cvvHash ? '***' : 'N/A',
      icon: Shield,
    },
  ];

  if (cartao.tipoCartao === 'CREDITO') {
    infoItems.push({
      label: 'Limite de Crédito',
      value: formatCurrency(cartao.limiteCredito),
      icon: DollarSign,
    });
    infoItems.push({
      label: 'Dia de Fechamento',
      value: cartao.diaFechamento || 'N/A',
      icon: Calendar,
    });
    infoItems.push({
      label: 'Dia de Vencimento',
      value: cartao.diaVencimento || 'N/A',
      icon: Calendar,
    });
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button variant="outline" onClick={onBack}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Voltar
          </Button>
          <h2 className="text-2xl font-bold text-gray-900">Detalhes do Cartão</h2>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Informações Principais */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <CreditCard className="h-5 w-5" />
                Informações do Cartão
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      {formatCardNumber(cartao.numeroCartao)}
                    </h3>
                    <p className="text-gray-600">{cartao.nomeTitular || 'N/A'}</p>
                  </div>
                  <Badge variant="outline" className="text-sm">
                    ID: {cartao.id}
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

          {/* Conta Associada */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building2 className="h-5 w-5" />
                Conta Associada
              </CardTitle>
            </CardHeader>
            <CardContent>
              {cartao.conta ? (
                <div className="p-4 bg-gray-50 rounded-lg">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-medium">
                        Conta: {cartao.conta.numeroConta || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        Agência: {cartao.conta.agencia || 'N/A'}
                      </p>
                      <p className="text-sm text-gray-600">
                        Saldo: {formatCurrency(cartao.conta.saldo)}
                      </p>
                    </div>
                    <Badge variant="secondary">
                      {cartao.conta.statusConta || 'Ativa'}
                    </Badge>
                  </div>
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
                <span className="text-sm text-gray-600">Tipo</span>
                <Badge variant="default">
                  {cartao.tipoCartao === 'CREDITO' ? 'Crédito' : 'Débito'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Status</span>
                <Badge className={getStatusColor(cartao.statusCartao)}>
                  {cartao.statusCartao || 'N/A'}
                </Badge>
              </div>
              {cartao.tipoCartao === 'CREDITO' && (
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Limite</span>
                  <span className="font-medium text-green-600">
                    {formatCurrency(cartao.limiteCredito)}
                  </span>
                </div>
              )}
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Validade</span>
                <span className="font-medium">
                  {formatDate(cartao.dataValidade)}
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Ações Rápidas */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Ações</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {cartao.statusCartao === 'ATIVO' ? (
                <Button variant="outline" className="w-full justify-start text-red-600 hover:text-red-700 hover:bg-red-50">
                  <Lock className="h-4 w-4 mr-2" />
                  Bloquear Cartão
                </Button>
              ) : (
                <Button variant="outline" className="w-full justify-start text-green-600 hover:text-green-700 hover:bg-green-50">
                  <Unlock className="h-4 w-4 mr-2" />
                  Desbloquear Cartão
                </Button>
              )}
              {cartao.tipoCartao === 'CREDITO' && (
                <Button variant="outline" className="w-full justify-start">
                  <DollarSign className="h-4 w-4 mr-2" />
                  Ver Fatura
                </Button>
              )}
            </CardContent>
          </Card>

          {/* Informações de Segurança */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Shield className="h-4 w-4" />
                Segurança
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">Número do Cartão</p>
                <p className="font-mono text-sm">{formatCardNumber(cartao.numeroCartao)}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">CVV</p>
                <p className="font-mono text-sm">***</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Validade</p>
                <p className="font-mono text-sm">
                  {cartao.dataValidade ? new Date(cartao.dataValidade).toLocaleDateString('pt-BR', { month: '2-digit', year: '2-digit' }) : 'N/A'}
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CartaoDetails;

