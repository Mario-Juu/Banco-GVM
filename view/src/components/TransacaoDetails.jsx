import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
  ArrowLeft,
  DollarSign,
  Calendar,
  Hash,
  Building2,
  ArrowUpRight,
  ArrowDownLeft,
  FileText
} from 'lucide-react';

const TransacaoDetails = ({ transacao, onBack }) => {
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
        return <ArrowDownLeft className="h-5 w-5 text-green-600" />;
      case 'SAQUE':
        return <ArrowUpRight className="h-5 w-5 text-red-600" />;
      case 'TRANSFERENCIA':
        return <ArrowUpRight className="h-5 w-5 text-blue-600" />;
      default:
        return <DollarSign className="h-5 w-5 text-gray-600" />;
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

  const infoItems = [
    {
      label: 'Valor',
      value: formatCurrency(transacao.valor),
      icon: DollarSign,
    },
    {
      label: 'Data e Hora',
      value: formatDate(transacao.dataHora),
      icon: Calendar,
    },
    {
      label: 'Tipo de Transação',
      value: getTipoLabel(transacao.tipoTransacao),
      icon: Hash,
    },
    {
      label: 'Status',
      value: transacao.statusTransacao || 'N/A',
      icon: Hash,
    },
  ];

  if (transacao.descricao) {
    infoItems.push({
      label: 'Descrição',
      value: transacao.descricao,
      icon: FileText,
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
          <h2 className="text-2xl font-bold text-gray-900">Detalhes da Transação</h2>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Informações Principais */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                {getTipoIcon(transacao.tipoTransacao)}
                Informações da Transação
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      {getTipoLabel(transacao.tipoTransacao)}
                    </h3>
                    <p className="text-gray-600">{formatCurrency(transacao.valor)}</p>
                  </div>
                  <Badge variant="outline" className="text-sm">
                    ID: {transacao.id}
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

          {/* Contas Envolvidas */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building2 className="h-5 w-5" />
                Contas Envolvidas
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {transacao.contaOrigem && (
                  <div className="p-4 bg-red-50 rounded-lg border border-red-200">
                    <h4 className="font-medium text-red-900 mb-2">Conta de Origem</h4>
                    <div className="space-y-1 text-sm text-red-700">
                      <p><strong>Número:</strong> {transacao.contaOrigem.numeroConta || 'N/A'}</p>
                      <p><strong>Agência:</strong> {transacao.contaOrigem.agencia || 'N/A'}</p>
                      <p><strong>Saldo:</strong> {formatCurrency(transacao.contaOrigem.saldo)}</p>
                    </div>
                  </div>
                )}

                {transacao.contaDestino && (
                  <div className="p-4 bg-green-50 rounded-lg border border-green-200">
                    <h4 className="font-medium text-green-900 mb-2">Conta de Destino</h4>
                    <div className="space-y-1 text-sm text-green-700">
                      <p><strong>Número:</strong> {transacao.contaDestino.numeroConta || 'N/A'}</p>
                      <p><strong>Agência:</strong> {transacao.contaDestino.agencia || 'N/A'}</p>
                      <p><strong>Saldo:</strong> {formatCurrency(transacao.contaDestino.saldo)}</p>
                    </div>
                  </div>
                )}

                {!transacao.contaOrigem && !transacao.contaDestino && (
                  <div className="text-center py-8">
                    <Building2 className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-600">Nenhuma conta associada</p>
                  </div>
                )}
              </div>
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
                  {getTipoLabel(transacao.tipoTransacao)}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Status</span>
                <Badge className={getStatusColor(transacao.statusTransacao)}>
                  {transacao.statusTransacao || 'N/A'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Valor</span>
                <span className="font-medium text-blue-600">
                  {formatCurrency(transacao.valor)}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Data</span>
                <span className="font-medium">
                  {formatDate(transacao.dataHora)}
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Identificação */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Hash className="h-4 w-4" />
                Identificação
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">ID da Transação</p>
                <p className="font-mono text-lg">{transacao.id}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Data de Processamento</p>
                <p className="font-mono text-sm">{formatDate(transacao.dataHora)}</p>
              </div>
            </CardContent>
          </Card>

          {/* Fluxo de Valores */}
          {(transacao.contaOrigem || transacao.contaDestino) && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Fluxo de Valores</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {transacao.contaOrigem && (
                  <div className="flex items-center space-x-2 text-sm">
                    <span className="text-red-600">Saída:</span>
                    <span className="font-mono">{transacao.contaOrigem.numeroConta}</span>
                  </div>
                )}
                <div className="flex items-center justify-center">
                  <ArrowDownLeft className="h-6 w-6 text-gray-400" />
                </div>
                <div className="text-center">
                  <span className="text-lg font-bold text-blue-600">
                    {formatCurrency(transacao.valor)}
                  </span>
                </div>
                <div className="flex items-center justify-center">
                  <ArrowDownLeft className="h-6 w-6 text-gray-400 rotate-180" />
                </div>
                {transacao.contaDestino && (
                  <div className="flex items-center space-x-2 text-sm">
                    <span className="text-green-600">Entrada:</span>
                    <span className="font-mono">{transacao.contaDestino.numeroConta}</span>
                  </div>
                )}
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
};

export default TransacaoDetails;

