import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Users, Building2, CreditCard, TrendingUp } from 'lucide-react';
import ApiService from '../services/api';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalClientes: 0,
    totalContas: 0,
    totalCartoes: 0,
    totalEmprestimos: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [clientes, contas, cartoes, emprestimos] = await Promise.all([
          ApiService.getClientes().catch(() => []),
          ApiService.getContas().catch(() => []),
          ApiService.getCartoes().catch(() => []),
          ApiService.getEmprestimos().catch(() => []),
        ]);

        setStats({
          totalClientes: clientes.length || 0,
          totalContas: contas.length || 0,
          totalCartoes: cartoes.length || 0,
          totalEmprestimos: emprestimos.length || 0,
        });
      } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const statCards = [
    {
      title: 'Total de Clientes',
      value: stats.totalClientes,
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      title: 'Total de Contas',
      value: stats.totalContas,
      icon: Building2,
      color: 'text-green-600',
      bgColor: 'bg-green-50',
    },
    {
      title: 'Total de Cartões',
      value: stats.totalCartoes,
      icon: CreditCard,
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
    },
    {
      title: 'Total de Empréstimos',
      value: stats.totalEmprestimos,
      icon: TrendingUp,
      color: 'text-orange-600',
      bgColor: 'bg-orange-50',
    },
  ];

  if (loading) {
    return (
      <div className="space-y-6">
        <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i} className="animate-pulse">
              <CardHeader className="pb-2">
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
              </CardHeader>
              <CardContent>
                <div className="h-8 bg-gray-200 rounded w-1/2"></div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>
        <div className="text-sm text-gray-500">
          Visão geral do sistema
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index} className="hover:shadow-lg transition-shadow">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600">
                  {stat.title}
                </CardTitle>
                <div className={`p-2 rounded-lg ${stat.bgColor}`}>
                  <Icon className={`h-4 w-4 ${stat.color}`} />
                </div>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900">
                  {stat.value.toLocaleString()}
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Bem-vindo ao Banco GVM</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600">
              Sistema de gestão bancária completo para administração de clientes, 
              contas, cartões, transações e empréstimos.
            </p>
            <div className="mt-4 space-y-2">
              <div className="flex items-center text-sm text-gray-600">
                <Users className="h-4 w-4 mr-2" />
                Gestão completa de clientes
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <Building2 className="h-4 w-4 mr-2" />
                Contas corrente e poupança
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <CreditCard className="h-4 w-4 mr-2" />
                Cartões de crédito e débito
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <TrendingUp className="h-4 w-4 mr-2" />
                Sistema de empréstimos
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Ações Rápidas</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="p-3 bg-blue-50 rounded-lg">
                <h4 className="font-medium text-blue-900">Cadastrar Cliente</h4>
                <p className="text-sm text-blue-700">
                  Adicione novos clientes ao sistema
                </p>
              </div>
              <div className="p-3 bg-green-50 rounded-lg">
                <h4 className="font-medium text-green-900">Criar Conta</h4>
                <p className="text-sm text-green-700">
                  Abra novas contas corrente ou poupança
                </p>
              </div>
              <div className="p-3 bg-purple-50 rounded-lg">
                <h4 className="font-medium text-purple-900">Emitir Cartão</h4>
                <p className="text-sm text-purple-700">
                  Emita cartões de crédito ou débito
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Dashboard;

