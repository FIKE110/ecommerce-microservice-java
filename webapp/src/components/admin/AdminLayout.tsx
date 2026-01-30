import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { LayoutDashboard, ShoppingCart, Users, LogOut, Box } from 'lucide-react';
import { useAdminAuth } from '../../context/AdminAuthContext';

const AdminLayout: React.FC = () => {
  const { logout } = useAdminAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/admin/login');
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="hidden md:flex flex-col w-64 bg-gray-800">
        <div className="flex items-center justify-center h-16 bg-gray-900">
          <span className="text-white font-bold uppercase">Admin Panel</span>
        </div>
        <div className="flex flex-col flex-1 overflow-y-auto">
          <nav className="flex-1 px-2 py-4 bg-gray-800">
            <NavLink to="/admin/dashboard" className={({ isActive }) => `flex items-center px-4 py-2 text-gray-100 hover:bg-gray-700 ${isActive ? 'bg-gray-700' : ''}`}>
              <LayoutDashboard className="h-6 w-6 mr-3" />
              Dashboard
            </NavLink>
            <NavLink to="/admin/products" className={({ isActive }) => `flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700 ${isActive ? 'bg-gray-700' : ''}`}>
              <Box className="h-6 w-6 mr-3" />
              Products
            </NavLink>
            <NavLink to="/admin/orders" className={({ isActive }) => `flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700 ${isActive ? 'bg-gray-700' : ''}`}>
              <ShoppingCart className="h-6 w-6 mr-3" />
              Orders
            </NavLink>
            <NavLink to="/admin/users" className={({ isActive }) => `flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700 ${isActive ? 'bg-gray-700' : ''}`}>
              <Users className="h-6 w-6 mr-3" />
              Users
            </NavLink>
          </nav>
        </div>
        <div className="px-2 py-4">
            <button onClick={handleLogout} className="flex items-center px-4 py-2 w-full text-gray-100 hover:bg-gray-700">
                <LogOut className="h-6 w-6 mr-3" />
                Logout
            </button>
        </div>
      </div>

      {/* Main content */}
      <div className="flex flex-col flex-1 overflow-y-auto">
        <div className="p-4">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AdminLayout;
