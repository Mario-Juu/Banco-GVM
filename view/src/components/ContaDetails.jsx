import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
  ArrowLeft,
  Edit,
  Building2,
  DollarSign,
  Calendar,
  Hash,
  Banknote,
  CreditCard,
  User
} from 'lucide-react';

const ContaDetails = ({ conta, onBack, onEdit }) => {
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

  const infoItems = [
    {
      label: 'Número da Conta',
      value: conta.numeroConta || 'N/A',
      icon: Hash,
    },
    {
      label: 'Agência',
      value: conta.agencia || 'N/A',
      icon: Building2,
    },
    {
      label: 'Saldo',
      value: formatCurrency(conta.saldo),
      icon: DollarSign,
    },
    {
      label: 'Data de Abertura',
      value: formatDate(conta.dataAbertura),
      icon: Calendar,
    },
    {
      label: 'Status da Conta',
      value: conta.statusConta || 'N/A',
      icon: Hash,
    },
    {
      label: 'Tipo de Conta',
      value: conta.tipoConta === 'CORRENTE' ? 'Corrente' : 'Poupança',
      icon: Banknote,
    },
  ];

  if (conta.tipoConta === 'CORRENTE') {
    infoItems.push({
      label: 'Limite Cheque Especial',
      value: formatCurrency(conta.limiteChequeEspecial),
      icon: DollarSign,
    });
  } else if (conta.tipoConta === 'POUPANCA') {
    infoItems.push({
      label: 'Taxa de Juros Anual',
      value: `${conta.taxaJuros || 'N/A'}%`,
      icon: DollarSign,
    });
    infoItems.push({
      label: 'Data de Aniversário',
      value: formatDate(conta.dataAniversario),
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
          <h2 className="text-2xl font-bold text-gray-900">Detalhes da Conta</h2>
        </div>
        {/* Edição de contas pode ser mais complexa, desabilitado por enquanto */}
        {/* <Button onClick={() => onEdit(conta)}>
          <Edit className="h-4 w-4 mr-2" />
          Editar
        </Button> */}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Informações Principais */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building2 className="h-5 w-5" />
                Informações da Conta
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      Conta: {conta.numeroConta || 'N/A'}
                    </h3>
                    <p className="text-gray-600">Agência: {conta.agencia || 'N/A'}</p>
                  </div>
                  <Badge variant="outline" className="text-sm">
                    ID: {conta.id}
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

          {/* Titulares da Conta */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Titulares da Conta
              </CardTitle>
            </CardHeader>
            <CardContent>
              {conta.titulares && conta.titulares.length > 0 ? (
                <div className="space-y-3">
                  {conta.titulares.map((titular, index) => (
                    <div key={index} className="p-4 bg-gray-50 rounded-lg">
                      <p className="font-medium">{titular.cliente?.nome || 'N/A'}</p>
                      <p className="text-sm text-gray-600">CPF: {titular.cliente?.cpf || 'N/A'}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8">
                  <User className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">Nenhum titular associado</p>
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
                <Badge variant="default">{conta.tipoConta === 'CORRENTE' ? 'Corrente' : 'Poupança'}</Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Status</span>
                <Badge variant="secondary">{conta.statusConta || 'N/A'}</Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Saldo Atual</span>
                <span className="font-medium text-green-600">
                  {formatCurrency(conta.saldo)}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Cartões Associados</span>
                <span className="font-medium">
                  {conta.cartoes?.length || 0}
                </span>
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
                <CreditCard className="h-4 w-4 mr-2" />
                Emitir Cartão
              </Button>
              <Button variant="outline" className="w-full justify-start">
                <DollarSign className="h-4 w-4 mr-2" />
                Realizar Transação
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default ContaDetails;

