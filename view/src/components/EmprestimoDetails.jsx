import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
  ArrowLeft,
  DollarSign,
  Calendar,
  Hash,
  User,
  Building2,
  CheckCircle,
  XCircle,
  Clock,
  AlertTriangle
} from 'lucide-react';

const EmprestimoDetails = ({ emprestimo, onBack }) => {
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
        return <CheckCircle className="h-5 w-5 text-green-600" />;
      case 'PENDENTE':
        return <Clock className="h-5 w-5 text-yellow-600" />;
      case 'REJEITADO':
        return <XCircle className="h-5 w-5 text-red-600" />;
      default:
        return <Hash className="h-5 w-5 text-gray-600" />;
    }
  };

  const calcularValorParcela = () => {
    const valor = emprestimo.valorAprovado || emprestimo.valorSolicitado || 0;
    const taxa = (emprestimo.taxaJurosMensal || 0) / 100;
    const parcelas = emprestimo.numeroParcelas || 1;

    if (valor > 0 && taxa >= 0 && parcelas > 0) {
      const valorComJuros = valor * Math.pow(1 + taxa, parcelas);
      return valorComJuros / parcelas;
    }
    return 0;
  };

  const calcularValorTotal = () => {
    const valorParcela = calcularValorParcela();
    const parcelas = emprestimo.numeroParcelas || 0;
    return valorParcela * parcelas;
  };

  const infoItems = [
    {
      label: 'Valor Solicitado',
      value: formatCurrency(emprestimo.valorSolicitado),
      icon: DollarSign,
    },
    {
      label: 'Data de Solicitação',
      value: formatDate(emprestimo.dataSolicitacao),
      icon: Calendar,
    },
    {
      label: 'Taxa de Juros Mensal',
      value: `${emprestimo.taxaJurosMensal || 'N/A'}%`,
      icon: Hash,
    },
    {
      label: 'Número de Parcelas',
      value: `${emprestimo.numeroParcelas || 'N/A'}x`,
      icon: Hash,
    },
  ];

  if (emprestimo.valorAprovado) {
    infoItems.push({
      label: 'Valor Aprovado',
      value: formatCurrency(emprestimo.valorAprovado),
      icon: DollarSign,
    });
  }

  if (emprestimo.dataAprovacao) {
    infoItems.push({
      label: 'Data de Aprovação',
      value: formatDate(emprestimo.dataAprovacao),
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
          <h2 className="text-2xl font-bold text-gray-900">Detalhes do Empréstimo</h2>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Informações Principais */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                {getStatusIcon(emprestimo.statusEmprestimo)}
                Informações do Empréstimo
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      {formatCurrency(emprestimo.valorSolicitado)}
                    </h3>
                    <p className="text-gray-600">
                      {emprestimo.numeroParcelas}x de {formatCurrency(calcularValorParcela())}
                    </p>
                  </div>
                  <Badge variant="outline" className="text-sm">
                    ID: {emprestimo.id}
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

          {/* Cliente e Conta */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Cliente e Conta
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {emprestimo.cliente && (
                  <div className="p-4 bg-blue-50 rounded-lg border border-blue-200">
                    <h4 className="font-medium text-blue-900 mb-2">Cliente</h4>
                    <div className="space-y-1 text-sm text-blue-700">
                      <p><strong>Nome:</strong> {emprestimo.cliente.nome || 'N/A'}</p>
                      <p><strong>CPF:</strong> {emprestimo.cliente.cpf || 'N/A'}</p>
                      <p><strong>Email:</strong> {emprestimo.cliente.email || 'N/A'}</p>
                      <p><strong>Telefone:</strong> {emprestimo.cliente.telefone || 'N/A'}</p>
                    </div>
                  </div>
                )}

                {emprestimo.contaCredito && (
                  <div className="p-4 bg-green-50 rounded-lg border border-green-200">
                    <h4 className="font-medium text-green-900 mb-2">Conta para Crédito</h4>
                    <div className="space-y-1 text-sm text-green-700">
                      <p><strong>Número:</strong> {emprestimo.contaCredito.numeroConta || 'N/A'}</p>
                      <p><strong>Agência:</strong> {emprestimo.contaCredito.agencia || 'N/A'}</p>
                      <p><strong>Saldo:</strong> {formatCurrency(emprestimo.contaCredito.saldo)}</p>
                    </div>
                  </div>
                )}

                {!emprestimo.cliente && !emprestimo.contaCredito && (
                  <div className="text-center py-8">
                    <User className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-600">Nenhuma informação de cliente ou conta disponível</p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Motivo de Rejeição */}
          {emprestimo.statusEmprestimo === 'REJEITADO' && emprestimo.motivoRejeicao && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-red-700">
                  <AlertTriangle className="h-5 w-5" />
                  Motivo da Rejeição
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="p-4 bg-red-50 rounded-lg border border-red-200">
                  <p className="text-red-700">{emprestimo.motivoRejeicao}</p>
                </div>
              </CardContent>
            </Card>
          )}
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
                <Badge className={getStatusColor(emprestimo.statusEmprestimo)}>
                  {emprestimo.statusEmprestimo || 'N/A'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Valor Solicitado</span>
                <span className="font-medium text-blue-600">
                  {formatCurrency(emprestimo.valorSolicitado)}
                </span>
              </div>
              {emprestimo.valorAprovado && (
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Valor Aprovado</span>
                  <span className="font-medium text-green-600">
                    {formatCurrency(emprestimo.valorAprovado)}
                  </span>
                </div>
              )}
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Parcelas</span>
                <span className="font-medium">
                  {emprestimo.numeroParcelas}x
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Taxa Mensal</span>
                <span className="font-medium">
                  {emprestimo.taxaJurosMensal}%
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Simulação Financeira */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Simulação Financeira</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">Valor da Parcela</p>
                <p className="text-lg font-bold text-blue-600">
                  {formatCurrency(calcularValorParcela())}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Valor Total a Pagar</p>
                <p className="text-lg font-bold text-orange-600">
                  {formatCurrency(calcularValorTotal())}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Total de Juros</p>
                <p className="text-lg font-bold text-red-600">
                  {formatCurrency(calcularValorTotal() - (emprestimo.valorAprovado || emprestimo.valorSolicitado || 0))}
                </p>
              </div>
            </CardContent>
          </Card>

          {/* Cronograma */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar className="h-4 w-4" />
                Cronograma
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm text-gray-600">Data de Solicitação</p>
                <p className="font-medium">{formatDate(emprestimo.dataSolicitacao)}</p>
              </div>
              {emprestimo.dataAprovacao && (
                <div>
                  <p className="text-sm text-gray-600">Data de Aprovação</p>
                  <p className="font-medium">{formatDate(emprestimo.dataAprovacao)}</p>
                </div>
              )}
              <div>
                <p className="text-sm text-gray-600">Duração</p>
                <p className="font-medium">{emprestimo.numeroParcelas} meses</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default EmprestimoDetails;

