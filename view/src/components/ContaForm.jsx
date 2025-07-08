import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ArrowLeft, Save, Building2 } from 'lucide-react';
import ApiService from '../services/api';

const ContaForm = ({ onBack, onSave }) => {
  const [formData, setFormData] = useState({
    numeroConta: '',
    agencia: '',
    saldo: '',
    statusConta: 'ATIVA',
    tipoConta: 'CORRENTE', // Default to CORRENTE
    limiteChequeEspecial: '', // Specific to ContaCorrente
    taxaJuros: '', // Specific to ContaPoupanca
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [clientes, setClientes] = useState([]);
  const [selectedClienteId, setSelectedClienteId] = useState('');

  useEffect(() => {
    const fetchClientes = async () => {
      try {
        const data = await ApiService.getClientes();
        setClientes(data || []);
      } catch (error) {
        console.error('Erro ao carregar clientes:', error);
      }
    };
    fetchClientes();
  }, []);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.numeroConta.trim()) {
      newErrors.numeroConta = 'Número da conta é obrigatório';
    }
    if (!formData.agencia.trim()) {
      newErrors.agencia = 'Agência é obrigatória';
    }
    if (formData.saldo === '' || isNaN(parseFloat(formData.saldo))) {
      newErrors.saldo = 'Saldo inicial é obrigatório e deve ser um número';
    }
    if (!selectedClienteId) {
      newErrors.cliente = 'Um cliente deve ser selecionado para a conta';
    }

    if (formData.tipoConta === 'CORRENTE' && (formData.limiteChequeEspecial === '' || isNaN(parseFloat(formData.limiteChequeEspecial)))) {
      newErrors.limiteChequeEspecial = 'Limite de cheque especial é obrigatório para conta corrente';
    }
    if (formData.tipoConta === 'POUPANCA' && (formData.taxaJuros === '' || isNaN(parseFloat(formData.taxaJuros)))) {
      newErrors.taxaJuros = 'Taxa de juros é obrigatória para conta poupança';
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
      const baseAccountData = {
        numeroConta: formData.numeroConta,
        agencia: formData.agencia,
        saldo: parseFloat(formData.saldo),
        dataAbertura: new Date().toISOString(),
        statusConta: formData.statusConta,
        titulares: [{ cliente: { id: selectedClienteId } }], // Associar ao cliente selecionado
      };

      let savedConta;
      if (formData.tipoConta === 'CORRENTE') {
        const contaCorrenteData = {
          ...baseAccountData,
          limiteChequeEspecial: parseFloat(formData.limiteChequeEspecial),
        };
        savedConta = await ApiService.createContaCorrente(contaCorrenteData);
      } else {
        const contaPoupancaData = {
          ...baseAccountData,
          taxaJuros: parseFloat(formData.taxaJuros),
        };
        savedConta = await ApiService.createContaPoupanca(contaPoupancaData);
      }

      onSave(savedConta);
    } catch (error) {
      console.error('Erro ao salvar conta:', error);
      alert('Erro ao salvar conta. Verifique o console para mais detalhes.');
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

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" onClick={onBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Button>
        <h2 className="text-2xl font-bold text-gray-900">Nova Conta</h2>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Building2 className="h-5 w-5" />
            Cadastro de Conta
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="numeroConta">Número da Conta *</Label>
                <Input
                  id="numeroConta"
                  value={formData.numeroConta}
                  onChange={(e) => handleChange('numeroConta', e.target.value)}
                  placeholder="Ex: 12345-6"
                  className={errors.numeroConta ? 'border-red-500' : ''}
                />
                {errors.numeroConta && (
                  <p className="text-sm text-red-600">{errors.numeroConta}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="agencia">Agência *</Label>
                <Input
                  id="agencia"
                  value={formData.agencia}
                  onChange={(e) => handleChange('agencia', e.target.value)}
                  placeholder="Ex: 0001"
                  className={errors.agencia ? 'border-red-500' : ''}
                />
                {errors.agencia && (
                  <p className="text-sm text-red-600">{errors.agencia}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="saldo">Saldo Inicial *</Label>
                <Input
                  id="saldo"
                  type="number"
                  value={formData.saldo}
                  onChange={(e) => handleChange('saldo', e.target.value)}
                  placeholder="0.00"
                  step="0.01"
                  className={errors.saldo ? 'border-red-500' : ''}
                />
                {errors.saldo && (
                  <p className="text-sm text-red-600">{errors.saldo}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="statusConta">Status da Conta</Label>
                <Select
                  value={formData.statusConta}
                  onValueChange={(value) => handleChange('statusConta', value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ATIVA">Ativa</SelectItem>
                    <SelectItem value="INATIVA">Inativa</SelectItem>
                    <SelectItem value="BLOQUEADA">Bloqueada</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="tipoConta">Tipo de Conta *</Label>
                <Select
                  value={formData.tipoConta}
                  onValueChange={(value) => handleChange('tipoConta', value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CORRENTE">Corrente</SelectItem>
                    <SelectItem value="POUPANCA">Poupança</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="cliente">Cliente Titular *</Label>
                <Select
                  value={selectedClienteId}
                  onValueChange={(value) => {
                    setSelectedClienteId(value);
                    if (errors.cliente) setErrors(prev => ({ ...prev, cliente: '' }));
                  }}
                >
                  <SelectTrigger className={errors.cliente ? 'border-red-500' : ''}>
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
                {errors.cliente && (
                  <p className="text-sm text-red-600">{errors.cliente}</p>
                )}
              </div>

              {formData.tipoConta === 'CORRENTE' && (
                <div className="space-y-2">
                  <Label htmlFor="limiteChequeEspecial">Limite Cheque Especial *</Label>
                  <Input
                    id="limiteChequeEspecial"
                    type="number"
                    value={formData.limiteChequeEspecial}
                    onChange={(e) => handleChange('limiteChequeEspecial', e.target.value)}
                    placeholder="0.00"
                    step="0.01"
                    className={errors.limiteChequeEspecial ? 'border-red-500' : ''}
                  />
                  {errors.limiteChequeEspecial && (
                    <p className="text-sm text-red-600">{errors.limiteChequeEspecial}</p>
                  )}
                </div>
              )}

              {formData.tipoConta === 'POUPANCA' && (
                <div className="space-y-2">
                  <Label htmlFor="taxaJuros">Taxa de Juros Anual (%) *</Label>
                  <Input
                    id="taxaJuros"
                    type="number"
                    value={formData.taxaJuros}
                    onChange={(e) => handleChange('taxaJuros', e.target.value)}
                    placeholder="Ex: 0.5"
                    step="0.01"
                    className={errors.taxaJuros ? 'border-red-500' : ''}
                  />
                  {errors.taxaJuros && (
                    <p className="text-sm text-red-600">{errors.taxaJuros}</p>
                  )}
                </div>
              )}
            </div>

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
                    Cadastrar Conta
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

export default ContaForm;

