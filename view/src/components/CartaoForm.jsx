import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ArrowLeft, Save, CreditCard } from 'lucide-react';
import ApiService from '../services/api';

const CartaoForm = ({ onBack, onSave }) => {
  const [formData, setFormData] = useState({
    numeroCartao: '',
    nomeTitular: '',
    dataValidade: '',
    cvvHash: '',
    statusCartao: 'ATIVO',
    tipoCartao: 'CREDITO', // Default to CREDITO
    limiteCredito: '', // Specific to CartaoCredito
    diaFechamento: '', // Specific to CartaoCredito
    diaVencimento: '', // Specific to CartaoCredito
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [contas, setContas] = useState([]);
  const [selectedContaId, setSelectedContaId] = useState('');

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

  const generateCardNumber = () => {
    // Gera um número de cartão fictício para demonstração
    const prefix = formData.tipoCartao === 'CREDITO' ? '4' : '5'; // Visa para crédito, Mastercard para débito
    let number = prefix;
    for (let i = 1; i < 16; i++) {
      number += Math.floor(Math.random() * 10);
    }
    return number;
  };

  const generateCVV = () => {
    return Math.floor(Math.random() * 900) + 100; // CVV de 3 dígitos
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.nomeTitular.trim()) {
      newErrors.nomeTitular = 'Nome do titular é obrigatório';
    }
    if (!formData.dataValidade) {
      newErrors.dataValidade = 'Data de validade é obrigatória';
    }
    if (!selectedContaId) {
      newErrors.conta = 'Uma conta deve ser selecionada para o cartão';
    }

    if (formData.tipoCartao === 'CREDITO') {
      if (formData.limiteCredito === '' || isNaN(parseFloat(formData.limiteCredito))) {
        newErrors.limiteCredito = 'Limite de crédito é obrigatório';
      }
      if (formData.diaFechamento === '' || isNaN(parseInt(formData.diaFechamento)) || parseInt(formData.diaFechamento) < 1 || parseInt(formData.diaFechamento) > 31) {
        newErrors.diaFechamento = 'Dia de fechamento deve ser entre 1 e 31';
      }
      if (formData.diaVencimento === '' || isNaN(parseInt(formData.diaVencimento)) || parseInt(formData.diaVencimento) < 1 || parseInt(formData.diaVencimento) > 31) {
        newErrors.diaVencimento = 'Dia de vencimento deve ser entre 1 e 31';
      }
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
      const baseCardData = {
        numeroCartao: formData.numeroCartao || generateCardNumber(),
        nomeTitular: formData.nomeTitular,
        dataEmissao: new Date().toISOString(),
        dataValidade: new Date(formData.dataValidade).toISOString(),
        cvvHash: formData.cvvHash || generateCVV().toString(),
        statusCartao: formData.statusCartao,
        conta: { id: selectedContaId },
      };

      let savedCartao;
      if (formData.tipoCartao === 'CREDITO') {
        const cartaoCreditoData = {
          ...baseCardData,
          limiteCredito: parseFloat(formData.limiteCredito),
          diaFechamento: parseInt(formData.diaFechamento),
          diaVencimento: parseInt(formData.diaVencimento),
        };
        savedCartao = await ApiService.createCartaoCredito(cartaoCreditoData);
      } else {
        savedCartao = await ApiService.createCartaoDebito(baseCardData);
      }

      onSave(savedCartao);
    } catch (error) {
      console.error('Erro ao salvar cartão:', error);
      alert('Erro ao salvar cartão. Verifique o console para mais detalhes.');
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

  const handleGenerateCard = () => {
    setFormData(prev => ({
      ...prev,
      numeroCartao: generateCardNumber(),
      cvvHash: generateCVV().toString(),
    }));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" onClick={onBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Button>
        <h2 className="text-2xl font-bold text-gray-900">Novo Cartão</h2>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Emissão de Cartão
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="tipoCartao">Tipo de Cartão *</Label>
                <Select
                  value={formData.tipoCartao}
                  onValueChange={(value) => handleChange('tipoCartao', value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CREDITO">Crédito</SelectItem>
                    <SelectItem value="DEBITO">Débito</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="conta">Conta Associada *</Label>
                <Select
                  value={selectedContaId}
                  onValueChange={(value) => {
                    setSelectedContaId(value);
                    if (errors.conta) setErrors(prev => ({ ...prev, conta: '' }));
                  }}
                >
                  <SelectTrigger className={errors.conta ? 'border-red-500' : ''}>
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
                {errors.conta && (
                  <p className="text-sm text-red-600">{errors.conta}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="nomeTitular">Nome do Titular *</Label>
                <Input
                  id="nomeTitular"
                  value={formData.nomeTitular}
                  onChange={(e) => handleChange('nomeTitular', e.target.value)}
                  placeholder="Nome como aparecerá no cartão"
                  className={errors.nomeTitular ? 'border-red-500' : ''}
                />
                {errors.nomeTitular && (
                  <p className="text-sm text-red-600">{errors.nomeTitular}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="dataValidade">Data de Validade *</Label>
                <Input
                  id="dataValidade"
                  type="date"
                  value={formData.dataValidade}
                  onChange={(e) => handleChange('dataValidade', e.target.value)}
                  className={errors.dataValidade ? 'border-red-500' : ''}
                />
                {errors.dataValidade && (
                  <p className="text-sm text-red-600">{errors.dataValidade}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="numeroCartao">Número do Cartão</Label>
                <div className="flex space-x-2">
                  <Input
                    id="numeroCartao"
                    value={formData.numeroCartao}
                    onChange={(e) => handleChange('numeroCartao', e.target.value)}
                    placeholder="Será gerado automaticamente"
                    maxLength={16}
                  />
                  <Button type="button" variant="outline" onClick={handleGenerateCard}>
                    Gerar
                  </Button>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="cvvHash">CVV</Label>
                <Input
                  id="cvvHash"
                  value={formData.cvvHash}
                  onChange={(e) => handleChange('cvvHash', e.target.value)}
                  placeholder="Será gerado automaticamente"
                  maxLength={3}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="statusCartao">Status do Cartão</Label>
                <Select
                  value={formData.statusCartao}
                  onValueChange={(value) => handleChange('statusCartao', value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ATIVO">Ativo</SelectItem>
                    <SelectItem value="INATIVO">Inativo</SelectItem>
                    <SelectItem value="BLOQUEADO">Bloqueado</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {formData.tipoCartao === 'CREDITO' && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="limiteCredito">Limite de Crédito *</Label>
                    <Input
                      id="limiteCredito"
                      type="number"
                      value={formData.limiteCredito}
                      onChange={(e) => handleChange('limiteCredito', e.target.value)}
                      placeholder="0.00"
                      step="0.01"
                      className={errors.limiteCredito ? 'border-red-500' : ''}
                    />
                    {errors.limiteCredito && (
                      <p className="text-sm text-red-600">{errors.limiteCredito}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="diaFechamento">Dia de Fechamento *</Label>
                    <Input
                      id="diaFechamento"
                      type="number"
                      value={formData.diaFechamento}
                      onChange={(e) => handleChange('diaFechamento', e.target.value)}
                      placeholder="Ex: 15"
                      min="1"
                      max="31"
                      className={errors.diaFechamento ? 'border-red-500' : ''}
                    />
                    {errors.diaFechamento && (
                      <p className="text-sm text-red-600">{errors.diaFechamento}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="diaVencimento">Dia de Vencimento *</Label>
                    <Input
                      id="diaVencimento"
                      type="number"
                      value={formData.diaVencimento}
                      onChange={(e) => handleChange('diaVencimento', e.target.value)}
                      placeholder="Ex: 10"
                      min="1"
                      max="31"
                      className={errors.diaVencimento ? 'border-red-500' : ''}
                    />
                    {errors.diaVencimento && (
                      <p className="text-sm text-red-600">{errors.diaVencimento}</p>
                    )}
                  </div>
                </>
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
                    Emitir Cartão
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

export default CartaoForm;

