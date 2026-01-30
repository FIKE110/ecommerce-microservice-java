import type { Product } from '../types';
import { ShoppingCart } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';
import { formatCurrency } from '../utils/currency';
import {toast} from "sonner";

interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const addToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      // POST /cart/{productId}/add/{quantity}
      await api.post(`/cart/${product.id}/add/1`);
      toast.success(`${1} item(s) added to cart!`);
    } catch (error) {
      console.error('Failed to add to cart', error);
      toast.error(`Failed to add to cart`);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
      <div 
        className="h-48 bg-gray-200 w-full object-cover flex items-center justify-center text-gray-400 cursor-pointer"
        onClick={() => navigate(`/products/${product.id}`)}
      >
        {product.images ? (
          <img src={product.images[0]} alt={product.name} className="h-full w-full object-cover" />
        ) : (
          <span>No Image</span>
        )}
      </div>
      <div className="p-4">
        <h3 
          className="text-lg font-semibold text-gray-900 truncate cursor-pointer hover:text-indigo-600"
          onClick={() => navigate(`/products/${product.id}`)}
        >
          {product.name}
        </h3>
        <p className="mt-1 text-gray-500 text-sm h-10 overflow-hidden">{product.description}</p>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-xl font-bold text-indigo-600">₦{product.price.toFixed(2)}</span>
          <button
            onClick={addToCart}
            className="p-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 focus:outline-none transition-colors"
          >
            <ShoppingCart className="h-5 w-5" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;
