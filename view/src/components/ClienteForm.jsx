import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ArrowLeft, Save, User } from 'lucide-react';
import ApiService from '../services/api';

const ClienteForm = ({ cliente, onBack, onSave }) => {
  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    dataNascimento: '',
    endereco: '',
    telefone: '',
    email: '',
    loginUsuario: '',
    senhaHash: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const isEditing = !!cliente;

  useEffect(() => {
    if (cliente) {
      setFormData({
        nome: cliente.nome || '',
        cpf: cliente.cpf || '',
        dataNascimento: cliente.dataNascimento || '',
        endereco: cliente.endereco || '',
        telefone: cliente.telefone || '',
        email: cliente.email || '',
        loginUsuario: cliente.loginUsuario || '',
        senhaHash: cliente.senhaHash || '',
      });
    }
  }, [cliente]);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome é obrigatório';
    }

    if (!formData.cpf.trim()) {
      newErrors.cpf = 'CPF é obrigatório';
    } else if (!/^\d{11}$/.test(formData.cpf.replace(/\D/g, ''))) {
      newErrors.cpf = 'CPF deve ter 11 dígitos';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email é obrigatório';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Email inválido';
    }

    if (!formData.telefone.trim()) {
      newErrors.telefone = 'Telefone é obrigatório';
    }

    if (!formData.dataNascimento) {
      newErrors.dataNascimento = 'Data de nascimento é obrigatória';
    }

    if (!isEditing && !formData.loginUsuario.trim()) {
      newErrors.loginUsuario = 'Login é obrigatório';
    }

    if (!isEditing && !formData.senhaHash.trim()) {
      newErrors.senhaHash = 'Senha é obrigatória';
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
      const clienteData = {
        ...formData,
        cpf: formData.cpf.replace(/\D/g, ''),
        dataCadastro: isEditing ? cliente.dataCadastro : new Date().toISOString(),
      };

      let savedCliente;
      if (isEditing) {
        savedCliente = await ApiService.updateCliente(cliente.id, clienteData);
      } else {
        savedCliente = await ApiService.createCliente(clienteData);
      }

      onSave(savedCliente);
    } catch (error) {
      console.error('Erro ao salvar cliente:', error);
      alert('Erro ao salvar cliente. Tente novamente.');
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

  const formatCPF = (value) => {
    const numbers = value.replace(/\D/g, '');
    return numbers.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  const formatPhone = (value) => {
    const numbers = value.replace(/\D/g, '');
    if (numbers.length <= 10) {
      return numbers.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }
    return numbers.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" onClick={onBack}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Button>
        <h2 className="text-2xl font-bold text-gray-900">
          {isEditing ? 'Editar Cliente' : 'Novo Cliente'}
        </h2>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            {isEditing ? `Editando: ${cliente.nome}` : 'Cadastro de Cliente'}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="nome">Nome Completo *</Label>
                <Input
                  id="nome"
                  value={formData.nome}
                  onChange={(e) => handleChange('nome', e.target.value)}
                  placeholder="Digite o nome completo"
                  className={errors.nome ? 'border-red-500' : ''}
                />
                {errors.nome && (
                  <p className="text-sm text-red-600">{errors.nome}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="cpf">CPF *</Label>
                <Input
                  id="cpf"
                  value={formatCPF(formData.cpf)}
                  onChange={(e) => handleChange('cpf', e.target.value.replace(/\D/g, ''))}
                  placeholder="000.000.000-00"
                  maxLength={14}
                  className={errors.cpf ? 'border-red-500' : ''}
                />
                {errors.cpf && (
                  <p className="text-sm text-red-600">{errors.cpf}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="dataNascimento">Data de Nascimento *</Label>
                <Input
                  id="dataNascimento"
                  type="date"
                  value={formData.dataNascimento}
                  onChange={(e) => handleChange('dataNascimento', e.target.value)}
                  className={errors.dataNascimento ? 'border-red-500' : ''}
                />
                {errors.dataNascimento && (
                  <p className="text-sm text-red-600">{errors.dataNascimento}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="telefone">Telefone *</Label>
                <Input
                  id="telefone"
                  value={formatPhone(formData.telefone)}
                  onChange={(e) => handleChange('telefone', e.target.value.replace(/\D/g, ''))}
                  placeholder="(11) 99999-9999"
                  maxLength={15}
                  className={errors.telefone ? 'border-red-500' : ''}
                />
                {errors.telefone && (
                  <p className="text-sm text-red-600">{errors.telefone}</p>
                )}
              </div>

              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="email">Email *</Label>
                <Input
                  id="email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleChange('email', e.target.value)}
                  placeholder="cliente@email.com"
                  className={errors.email ? 'border-red-500' : ''}
                />
                {errors.email && (
                  <p className="text-sm text-red-600">{errors.email}</p>
                )}
              </div>

              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="endereco">Endereço</Label>
                <Input
                  id="endereco"
                  value={formData.endereco}
                  onChange={(e) => handleChange('endereco', e.target.value)}
                  placeholder="Rua, número, bairro, cidade - UF"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="loginUsuario">Login {!isEditing && '*'}</Label>
                <Input
                  id="loginUsuario"
                  value={formData.loginUsuario}
                  onChange={(e) => handleChange('loginUsuario', e.target.value)}
                  placeholder="Nome de usuário"
                  className={errors.loginUsuario ? 'border-red-500' : ''}
                  disabled={isEditing}
                />
                {errors.loginUsuario && (
                  <p className="text-sm text-red-600">{errors.loginUsuario}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="senhaHash">Senha {!isEditing && '*'}</Label>
                <Input
                  id="senhaHash"
                  type="password"
                  value={formData.senhaHash}
                  onChange={(e) => handleChange('senhaHash', e.target.value)}
                  placeholder={isEditing ? "Deixe em branco para manter a atual" : "Digite a senha"}
                  className={errors.senhaHash ? 'border-red-500' : ''}
                />
                {errors.senhaHash && (
                  <p className="text-sm text-red-600">{errors.senhaHash}</p>
                )}
              </div>
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
                    {isEditing ? 'Atualizar' : 'Cadastrar'}
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

export default ClienteForm;

