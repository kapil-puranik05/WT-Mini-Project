import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, getUser } from '../utils/api';
import { Header } from '../components/Header';
import { SummaryCards } from '../components/SummaryCards';
import { ChartsSection } from '../components/ChartsSection';
import { TransactionsTable } from '../components/TransactionsTable';
import { AddTransactionForm } from '../components/AddTransactionForm';

interface DashboardData {
  totalIncome: number;
  totalExpense: number;
  netBalance: number;
  categoryIncome: Array<{ category: string; amount: number }>;
  categoryExpense: Array<{ category: string; amount: number }>;
  monthlyIncome: number;
  monthlyExpense: number;
  recentTransactions: Array<{
    date: string;
    type: 'INCOME' | 'EXPENSE';
    category: string;
    amount: number;
    description: string;
  }>;
  transactionCount: number;
}

export default function Dashboard() {
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const user = getUser();

  const fetchDashboard = async () => {
    try {
      const currentDate = new Date();
      const month = currentDate.getMonth() + 1;
      const year = currentDate.getFullYear();

      const data = await api.get(`/transactions/dashboard?month=${month}&year=${year}`);
      setDashboardData(data);
      setError('');
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchDashboard();
  }, [navigate, user]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-600">Loading dashboard...</div>
      </div>
    );
  }

  if (error || !dashboardData) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-red-600">{error || 'Failed to load data'}</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header email={user?.email || ''} />

      <main className="max-w-7xl mx-auto px-6 py-8 space-y-8">
        <SummaryCards
          totalIncome={dashboardData.totalIncome}
          totalExpense={dashboardData.totalExpense}
          netBalance={dashboardData.netBalance}
          transactionCount={dashboardData.transactionCount}
        />

        <ChartsSection
          categoryIncome={dashboardData.categoryIncome}
          categoryExpense={dashboardData.categoryExpense}
          monthlyIncome={dashboardData.monthlyIncome}
          monthlyExpense={dashboardData.monthlyExpense}
        />

        {user?.role === 'ADMIN' && (
          <AddTransactionForm onSuccess={fetchDashboard} />
        )}

        <TransactionsTable transactions={dashboardData.recentTransactions} />
      </main>
    </div>
  );
}
