import React from 'react';
import { useAdminAuth } from '../../context/AdminAuthContext';

const AdminDashboardPage: React.FC = () => {
  const { admin } = useAdminAuth();

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>
      <div className="bg-white p-6 rounded-lg shadow-md">
        <p className="text-lg text-gray-700">Welcome, {admin?.username || 'Admin'}!</p>
        <p className="mt-4 text-gray-600">This is your central hub to manage products, orders, and users.</p>
        {/* Add more dashboard widgets or summaries here */}
      </div>
    </div>
  );
};

export default AdminDashboardPage;
