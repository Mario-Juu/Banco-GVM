import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ArrowLeft, Save, DollarSign } from 'lucide-react';
import ApiService from '../services/api';

const EmprestimoForm = ({ onBack, onSave }) => {
  const [formData, setFormData] = useState({
    valorSolicitado: '',
    taxaJurosMensal: '',
    numeroParcelas: '',
    clienteId: '',
    contaCreditoId: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [clientes, setClientes] = useState([]);
  const [contas, setContas] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [clientesData, contasData] = await Promise.all([
          ApiService.getClientes(),
          ApiService.getContas()
        ]);
        setClientes(clientesData || []);
        setContas(contasData || []);
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
      }
    };
    fetchData();
  }, []);

  const validateForm = () => {
    const newErrors = {};

    if (formData.valorSolicitado === '' || isNaN(parseFloat(formData.valorSolicitado)) || parseFloat(formData.valorSolicitado) <= 0) {
      newErrors.valorSolicitado = 'Valor solicitado deve ser um número positivo';
    }

    if (formData.taxaJurosMensal === '' || isNaN(parseFloat(formData.taxaJurosMensal)) || parseFloat(formData.taxaJurosMensal) < 0) {
      newErrors.taxaJurosMensal = 'Taxa de juros deve ser um número válido';
    }

    if (formData.numeroParcelas === '' || isNaN(parseInt(formData.numeroParcelas)) || parseInt(formData.numeroParcelas) <= 0) {
      newErrors.numeroParcelas = 'Número de parcelas deve ser um número positivo';
    }

    if (!formData.clienteId) {
      newErrors.clienteId = 'Cliente é obrigatório';
    }

    if (!formData.contaCreditoId) {
      newErrors.contaCreditoId = 'Conta para crédito é obrigatória';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const emprestimoData = {
        valorSolicitado: parseFloat(formData.valorSolicitado),
        taxaJurosMensal: parseFloat(formData.taxaJurosMensal),
        numeroParcelas: parseInt(formData.numeroParcelas),
        dataSolicitacao: new Date().toISOString(),
        statusEmprestimo: 'PENDENTE',
        cliente: { id: parseInt(formData.clienteId) },
        contaCredito: { id: parseInt(formData.contaCreditoId) },
      };

      const savedEmprestimo = await ApiService.createEmprestimo(emprestimoData);
      onSave(savedEmprestimo);
    } catch (error) {
      console.error('Erro ao salvar empréstimo:', error);
      alert('Erro ao salvar empréstimo. Verifique o console para mais detalhes.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const calcularValorParcela = () => {
    const valor = parseFloat(formData.valorSolicitado) || 0;
    const taxa = parseFloat(formData.taxaJurosMensal) / 100 || 0;
    const parcelas = parseInt(formData.numeroParcelas) || 1;

    if (valor > 0 && taxa >= 0 && parcelas > 0) {
      // Fórmula de juros compostos para parcelas fixas
      const valorComJuros = valor * Math.pow(1 + taxa, parcelas);
      const valorParcela = valorComJuros / parcelas;
      return valorParcela;
    }
    return 0;
  };

  const calcularValorTotal = () => {
    const valorParcela = calcularValorParcela();
    const parcelas = parseInt(formData.numeroParcelas) || 0;
    return valorParcela * parcelas;
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" onClick={onBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Button>
        <h2 className="text-2xl font-bold text-gray-900">Novo Empréstimo</h2>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <DollarSign className="h-5 w-5" />
            Solicitação de Empréstimo
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="clienteId">Cliente *</Label>
                <Select
                  value={formData.clienteId}
                  onValueChange={(value) => handleChange('clienteId', value)}
                >
                  <SelectTrigger className={errors.clienteId ? 'border-red-500' : ''}>
                    <SelectValue placeholder="Selecione um cliente" />
                  </SelectTrigger>
                  <SelectContent>
                    {clientes.length === 0 ? (
                      <SelectItem value="no-data" disabled>Nenhum cliente disponível</SelectItem>
                    ) : (
                      clientes.map(cliente => (
                        <SelectItem key={cliente.id} value={cliente.id.toString()}>
                          {cliente.nome} (CPF: {cliente.cpf})
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
                {errors.clienteId && (
                  <p className="text-sm text-red-600">{errors.clienteId}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="contaCreditoId">Conta para Crédito *</Label>
                <Select
                  value={formData.contaCreditoId}
                  onValueChange={(value) => handleChange('contaCreditoId', value)}
                >
                  <SelectTrigger className={errors.contaCreditoId ? 'border-red-500' : ''}>
                    <SelectValue placeholder="Selecione uma conta" />
                  </SelectTrigger>
                  <SelectContent>
                    {contas.length === 0 ? (
                      <SelectItem value="no-data" disabled>Nenhuma conta disponível</SelectItem>
                    ) : (
                      contas.map(conta => (
                        <SelectItem key={conta.id} value={conta.id.toString()}>
                          {conta.numeroConta} - Agência: {conta.agencia}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
                {errors.contaCreditoId && (
                  <p className="text-sm text-red-600">{errors.contaCreditoId}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="valorSolicitado">Valor Solicitado *</Label>
                <Input
                  id="valorSolicitado"
                  type="number"
                  value={formData.valorSolicitado}
                  onChange={(e) => handleChange('valorSolicitado', e.target.value)}
                  placeholder="0.00"
                  step="0.01"
                  min="0.01"
                  className={errors.valorSolicitado ? 'border-red-500' : ''}
                />
                {errors.valorSolicitado && (
                  <p className="text-sm text-red-600">{errors.valorSolicitado}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="taxaJurosMensal">Taxa de Juros Mensal (%) *</Label>
                <Input
                  id="taxaJurosMensal"
                  type="number"
                  value={formData.taxaJurosMensal}
                  onChange={(e) => handleChange('taxaJurosMensal', e.target.value)}
                  placeholder="Ex: 2.5"
                  step="0.01"
                  min="0"
                  className={errors.taxaJurosMensal ? 'border-red-500' : ''}
                />
                {errors.taxaJurosMensal && (
                  <p className="text-sm text-red-600">{errors.taxaJurosMensal}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="numeroParcelas">Número de Parcelas *</Label>
                <Input
                  id="numeroParcelas"
                  type="number"
                  value={formData.numeroParcelas}
                  onChange={(e) => handleChange('numeroParcelas', e.target.value)}
                  placeholder="Ex: 12"
                  min="1"
                  max="60"
                  className={errors.numeroParcelas ? 'border-red-500' : ''}
                />
                {errors.numeroParcelas && (
                  <p className="text-sm text-red-600">{errors.numeroParcelas}</p>
                )}
              </div>
            </div>

            {/* Simulação do Empréstimo */}
            {formData.valorSolicitado && formData.taxaJurosMensal && formData.numeroParcelas && (
              <div className="p-4 bg-blue-50 rounded-lg border border-blue-200">
                <h4 className="font-medium text-blue-900 mb-3">Simulação do Empréstimo</h4>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                  <div>
                    <p className="text-blue-700 font-medium">Valor Solicitado</p>
                    <p className="text-lg font-bold text-blue-900">
                      {formatCurrency(parseFloat(formData.valorSolicitado) || 0)}
                    </p>
                  </div>
                  <div>
                    <p className="text-blue-700 font-medium">Valor da Parcela</p>
                    <p className="text-lg font-bold text-blue-900">
                      {formatCurrency(calcularValorParcela())}
                    </p>
                  </div>
                  <div>
                    <p className="text-blue-700 font-medium">Valor Total a Pagar</p>
                    <p className="text-lg font-bold text-blue-900">
                      {formatCurrency(calcularValorTotal())}
                    </p>
                  </div>
                </div>
                <div className="mt-3 pt-3 border-t border-blue-200">
                  <p className="text-sm text-blue-700">
                    <strong>Juros Total:</strong> {formatCurrency(calcularValorTotal() - (parseFloat(formData.valorSolicitado) || 0))}
                  </p>
                  <p className="text-sm text-blue-700">
                    <strong>Taxa Mensal:</strong> {formData.taxaJurosMensal}% | 
                    <strong> Parcelas:</strong> {formData.numeroParcelas}x
                  </p>
                </div>
              </div>
            )}

            <div className="flex items-center justify-end space-x-4 pt-6 border-t">
              <Button type="button" variant="outline" onClick={onBack}>
                Cancelar
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? (
                  'Salvando...'
                ) : (
                  <>
                    <Save className="h-4 w-4 mr-2" />
                    Solicitar Empréstimo
                  </>
                )}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default EmprestimoForm;

