import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingCart, User, LogOut, Menu } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="container-custom">
        <div className="relative flex items-center justify-between h-16">
          <div className="absolute inset-y-0 left-0 flex items-center sm:hidden">
            {/* Mobile menu button*/}
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              type="button"
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
              aria-controls="mobile-menu"
              aria-expanded="false"
            >
              <span className="sr-only">Open main menu</span>
              <Menu className="block h-6 w-6" aria-hidden="true" />
            </button>
          </div>
          <div className="flex-1 flex items-center justify-center sm:items-stretch sm:justify-start">
            <div className="flex-shrink-0 flex items-center">
              <Link to="/">
                <span className="text-xl font-bold text-indigo-600">FortuneStore</span>
              </Link>
            </div>
            <div className="hidden sm:block sm:ml-6">
              <div className="flex space-x-4">
                <Link to="/products" className="text-gray-500 hover:text-gray-700 px-3 py-2 rounded-md text-sm font-medium">
                  Products
                </Link>
                {isAuthenticated && (
                  <Link to="/orders" className="text-gray-500 hover:text-gray-700 px-3 py-2 rounded-md text-sm font-medium">
                    Orders
                  </Link>
                )}
              </div>
            </div>
          </div>
          <div className="absolute inset-y-0 right-0 flex items-center pr-2 sm:static sm:inset-auto sm:ml-6 sm:pr-0">
            <Link to="/cart" className="p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none">
              <ShoppingCart className="h-6 w-6" />
            </Link>

            {isAuthenticated ? (
              <div className="ml-3 relative flex items-center space-x-4">
                <Link to="/profile" className="p-1 rounded-full text-gray-400 hover:text-gray-500">
                  <User className="h-6 w-6" />
                </Link>
                <button
                  onClick={handleLogout}
                  className="p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none"
                >
                  <LogOut className="h-6 w-6" />
                </button>
              </div>
            ) : (
              <div className="hidden sm:flex sm:items-center sm:space-x-2 ml-4">
                <Link to="/login" className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
                  Sign in
                </Link>
                <Link to="/register" className="bg-indigo-600 text-white hover:bg-indigo-700 px-3 py-2 rounded-md text-sm font-medium">
                  Sign up
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>

      {isMenuOpen && (
        <div className="sm:hidden" id="mobile-menu">
          <div className="px-2 pt-2 pb-3 space-y-1">
            <Link to="/products" className="text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
              Products
            </Link>
            {isAuthenticated && (
              <Link to="/orders" className="text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
                Orders
              </Link>
            )}
            {isAuthenticated && (
              <Link to="/profile" className="text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
                Profile
              </Link>
            )}
            {!isAuthenticated && (
              <>
                <Link to="/login" className="text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
                  Sign in
                </Link>
                <Link to="/register" className="text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
                  Sign up
                </Link>
              </>
            )}
            {isAuthenticated && (
              <button onClick={handleLogout} className="w-full text-left text-gray-500 hover:text-gray-700 block px-3 py-2 rounded-md text-base font-medium">
                Logout
              </button>
            )}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
