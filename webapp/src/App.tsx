import { Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { Toaster } from 'sonner';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CartPage from './pages/CartPage';
import CreateProfilePage from './pages/CreateProfilePage';
import OrderPage from './pages/OrderPage';
import OrderDetailPage from './pages/OrderDetailPage';
import ProfilePage from './pages/ProfilePage';
import { AuthProvider, useAuth } from './context/AuthContext';

// Admin Imports
import { AdminAuthProvider } from './context/AdminAuthContext';
import AdminLoginPage from './pages/admin/AdminLoginPage';
import AdminLayout from './components/admin/AdminLayout';
import AdminProtectedRoute from './components/admin/AdminProtectedRoute';
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminProductPage from './pages/admin/AdminProductPage';
import AdminOrderPage from './pages/admin/AdminOrderPage';
import AdminUserPage from './pages/admin/AdminUserPage';

import './App.css';

function App() {
  const { isAuthenticated, profile, profileFetched, isLoading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading && isAuthenticated && profileFetched && !profile) {
      navigate('/create-profile');
    }
  }, [isAuthenticated, profile, profileFetched, isLoading, navigate]);

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-grow">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductListPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/create-profile" element={<CreateProfilePage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/orders" element={<OrderPage />} />
          <Route path="/orders/:id" element={<OrderDetailPage />} />


          {/* Admin Routes */}
          <Route path="/admin/login" element={<AdminLoginPage />} />
          <Route
            path="/admin"
            element={
              <AdminProtectedRoute>
                <AdminLayout />
              </AdminProtectedRoute>
            }
          >
            <Route index element={<AdminDashboardPage />} /> {/* Default admin route */}
            <Route path="dashboard" element={<AdminDashboardPage />} />
            <Route path="products" element={<AdminProductPage />} />
            <Route path="orders" element={<AdminOrderPage />} />
            <Route path="users" element={<AdminUserPage />} />
          </Route>
        </Routes>
      </main>
      <Footer />
      <Toaster 
        position="top-right"
        expand={false}
        richColors
        toastOptions={{
          className: 'text-lg',
          style: {
            fontSize: '18px',
            padding: '16px 24px',
            borderRadius: '12px',
          }
        }}
      />
    </div>
  );
}

const AppWrapper = () => (
  <AdminAuthProvider>
    <AuthProvider>
      <App />
    </AuthProvider>
  </AdminAuthProvider>
);

export default AppWrapper;
