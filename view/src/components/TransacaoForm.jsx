import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { ArrowLeft, Save, DollarSign } from 'lucide-react';
import ApiService from '../services/api';

const TransacaoForm = ({ onBack, onSave }) => {
  const [formData, setFormData] = useState({
    contaOrigemId: '',
    contaDestinoId: '',
    valor: '',
    tipoTransacao: 'TRANSFERENCIA',
    descricao: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [contas, setContas] = useState([]);

  useEffect(() => {
    const fetchContas = async () => {
      try {
        const data = await ApiService.getContas();
        setContas(data || []);
      } catch (error) {
        console.error('Erro ao carregar contas:', error);
      }
    };
    fetchContas();
  }, []);

  const validateForm = () => {
    const newErrors = {};

    if (formData.valor === '' || isNaN(parseFloat(formData.valor)) || parseFloat(formData.valor) <= 0) {
      newErrors.valor = 'Valor deve ser um número positivo';
    }

    if (!formData.tipoTransacao) {
      newErrors.tipoTransacao = 'Tipo de transação é obrigatório';
    }

    if (!formData.contaDestinoId) {
      newErrors.contaDestinoId = 'Conta de destino é obrigatória';
    }

    // Para transferências, conta de origem é obrigatória
    if (formData.tipoTransacao === 'TRANSFERENCIA' && !formData.contaOrigemId) {
      newErrors.contaOrigemId = 'Conta de origem é obrigatória para transferências';
    }

    // Para saques, conta de origem é obrigatória
    if (formData.tipoTransacao === 'SAQUE' && !formData.contaOrigemId) {
      newErrors.contaOrigemId = 'Conta de origem é obrigatória para saques';
    }

    // Verificar se conta origem e destino são diferentes para transferências
    if (formData.tipoTransacao === 'TRANSFERENCIA' && 
        formData.contaOrigemId && 
        formData.contaDestinoId && 
        formData.contaOrigemId === formData.contaDestinoId) {
      newErrors.contaDestinoId = 'Conta de destino deve ser diferente da conta de origem';
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
      const transacaoData = {
        contaOrigemId: formData.contaOrigemId || null,
        contaDestinoId: parseInt(formData.contaDestinoId),
        valor: parseFloat(formData.valor),
        tipoTransacao: formData.tipoTransacao,
        descricao: formData.descricao || null,
      };

      const savedTransacao = await ApiService.createTransacao(transacaoData);
      onSave(savedTransacao);
    } catch (error) {
      console.error('Erro ao salvar transação:', error);
      alert('Erro ao salvar transação. Verifique o console para mais detalhes.');
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

  const handleTipoChange = (tipo) => {
    setFormData(prev => ({
      ...prev,
      tipoTransacao: tipo,
      contaOrigemId: tipo === 'DEPOSITO' ? '' : prev.contaOrigemId,
    }));
    if (errors.tipoTransacao) {
      setErrors(prev => ({ ...prev, tipoTransacao: '' }));
    }
  };

  const getContaDisplay = (conta) => {
    return `${conta.numeroConta} - Agência: ${conta.agencia} (Saldo: ${new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(conta.saldo)})`;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" onClick={onBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Button>
        <h2 className="text-2xl font-bold text-gray-900">Nova Transação</h2>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <DollarSign className="h-5 w-5" />
            Registro de Transação
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="tipoTransacao">Tipo de Transação *</Label>
                <Select
                  value={formData.tipoTransacao}
                  onValueChange={handleTipoChange}
                >
                  <SelectTrigger className={errors.tipoTransacao ? 'border-red-500' : ''}>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="DEPOSITO">Depósito</SelectItem>
                    <SelectItem value="SAQUE">Saque</SelectItem>
                    <SelectItem value="TRANSFERENCIA">Transferência</SelectItem>
                  </SelectContent>
                </Select>
                {errors.tipoTransacao && (
                  <p className="text-sm text-red-600">{errors.tipoTransacao}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="valor">Valor *</Label>
                <Input
                  id="valor"
                  type="number"
                  value={formData.valor}
                  onChange={(e) => handleChange('valor', e.target.value)}
                  placeholder="0.00"
                  step="0.01"
                  min="0.01"
                  className={errors.valor ? 'border-red-500' : ''}
                />
                {errors.valor && (
                  <p className="text-sm text-red-600">{errors.valor}</p>
                )}
              </div>

              {(formData.tipoTransacao === 'TRANSFERENCIA' || formData.tipoTransacao === 'SAQUE') && (
                <div className="space-y-2">
                  <Label htmlFor="contaOrigemId">Conta de Origem *</Label>
                  <Select
                    value={formData.contaOrigemId}
                    onValueChange={(value) => handleChange('contaOrigemId', value)}
                  >
                    <SelectTrigger className={errors.contaOrigemId ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Selecione a conta de origem" />
                    </SelectTrigger>
                    <SelectContent>
                      {contas.length === 0 ? (
                        <SelectItem value="no-data" disabled>Nenhuma conta disponível</SelectItem>
                      ) : (
                        contas.map(conta => (
                          <SelectItem key={conta.id} value={conta.id.toString()}>
                            {getContaDisplay(conta)}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  {errors.contaOrigemId && (
                    <p className="text-sm text-red-600">{errors.contaOrigemId}</p>
                  )}
                </div>
              )}

              <div className="space-y-2">
                <Label htmlFor="contaDestinoId">
                  {formData.tipoTransacao === 'SAQUE' ? 'Conta para Saque *' : 'Conta de Destino *'}
                </Label>
                <Select
                  value={formData.contaDestinoId}
                  onValueChange={(value) => handleChange('contaDestinoId', value)}
                >
                  <SelectTrigger className={errors.contaDestinoId ? 'border-red-500' : ''}>
                    <SelectValue placeholder={
                      formData.tipoTransacao === 'SAQUE' 
                        ? 'Selecione a conta para saque'
                        : 'Selecione a conta de destino'
                    } />
                  </SelectTrigger>
                  <SelectContent>
                    {contas.length === 0 ? (
                      <SelectItem value="no-data" disabled>Nenhuma conta disponível</SelectItem>
                    ) : (
                      contas
                        .filter(conta => conta.id.toString() !== formData.contaOrigemId)
                        .map(conta => (
                          <SelectItem key={conta.id} value={conta.id.toString()}>
                            {getContaDisplay(conta)}
                          </SelectItem>
                        ))
                    )}
                  </SelectContent>
                </Select>
                {errors.contaDestinoId && (
                  <p className="text-sm text-red-600">{errors.contaDestinoId}</p>
                )}
              </div>

              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="descricao">Descrição</Label>
                <Textarea
                  id="descricao"
                  value={formData.descricao}
                  onChange={(e) => handleChange('descricao', e.target.value)}
                  placeholder="Descrição opcional da transação"
                  rows={3}
                />
              </div>
            </div>

            {/* Resumo da Transação */}
            {formData.valor && formData.tipoTransacao && (
              <div className="p-4 bg-gray-50 rounded-lg">
                <h4 className="font-medium text-gray-900 mb-2">Resumo da Transação</h4>
                <div className="space-y-1 text-sm text-gray-600">
                  <p><strong>Tipo:</strong> {
                    formData.tipoTransacao === 'DEPOSITO' ? 'Depósito' :
                    formData.tipoTransacao === 'SAQUE' ? 'Saque' : 'Transferência'
                  }</p>
                  <p><strong>Valor:</strong> {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(parseFloat(formData.valor) || 0)}</p>
                  {formData.contaOrigemId && (
                    <p><strong>Conta de Origem:</strong> {
                      contas.find(c => c.id.toString() === formData.contaOrigemId)?.numeroConta || 'N/A'
                    }</p>
                  )}
                  {formData.contaDestinoId && (
                    <p><strong>Conta de Destino:</strong> {
                      contas.find(c => c.id.toString() === formData.contaDestinoId)?.numeroConta || 'N/A'
                    }</p>
                  )}
                </div>
              </div>
            )}

            <div className="flex items-center justify-end space-x-4 pt-6 border-t">
              <Button type="button" variant="outline" onClick={onBack}>
                Cancelar
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? (
                  'Processando...'
                ) : (
                  <>
                    <Save className="h-4 w-4 mr-2" />
                    Registrar Transação
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

export default TransacaoForm;

