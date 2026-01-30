import { useState, useEffect, useCallback } from 'react';
import api from '../services/api';
import type { Product } from '../types';
import ProductCard from '../components/ProductCard';
import { Loader, Search } from 'lucide-react';
import { useDebounce } from '../hooks/useDebounce';
import Pagination from '../components/Pagination';

// Mock categories, as we don't have an endpoint for them yet
const categories = ['Electronics', 'Clothing', 'Books', 'Home Goods'];

const ProductListPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  const [searchTerm, setSearchTerm] = useState('');
  const [category, setCategory] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');

  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      params.append('page', String(currentPage - 1));
      params.append('size', '20');
      if (debouncedSearchTerm) {
        params.append('name', debouncedSearchTerm);
      }
      if (category) {
        params.append('category', category);
      }
      if (minPrice) {
        params.append('minPrice', minPrice);
      }
      if (maxPrice) {
        params.append('maxPrice', maxPrice);
      }

      const response = await api.get(`/product?${params.toString()}`);
      const pageData = response.data?.data?.data; 
      const productList = pageData?.content || [];

      setProducts(productList.map((p:Product)=>({...p,price:p.price/100})));
      setTotalPages(pageData?.totalPages || 1);
      setTotalItems(pageData?.totalElements || 0);
    } catch (err) {
      console.error("Error fetching products", err);
      setError('Failed to load products');
    } finally {
      setLoading(false);
    }
  }, [debouncedSearchTerm, category, minPrice, maxPrice, currentPage]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const clearFilters = () => {
    setSearchTerm('');
    setCategory('');
    setMinPrice('');
    setMaxPrice('');
    setCurrentPage(1);
  };

  // Reset page when filters change
  useEffect(() => {
    setCurrentPage(1);
  }, [debouncedSearchTerm, category, minPrice, maxPrice]);

  return (
    <div className="bg-gray-50 min-h-screen py-8">
      <div className="container-custom">
        <h2 className="text-3xl font-extrabold text-gray-900 mb-6">Our Products</h2>
        
        <div className="bg-white p-4 rounded-lg shadow-md mb-8">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
              <input
                type="text"
                placeholder="Search by name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 pr-4 py-2 border rounded-md w-full"
              />
            </div>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="border rounded-md w-full py-2 px-3"
            >
              <option value="">All Categories</option>
              {categories.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            <div className="flex items-center space-x-2">
              <input
                type="number"
                placeholder="Min Price"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                className="border rounded-md w-full py-2 px-3"
              />
              <span className="text-gray-500">-</span>
              <input
                type="number"
                placeholder="Max Price"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                className="border rounded-md w-full py-2 px-3"
              />
            </div>
            <button 
              onClick={clearFilters}
              className="bg-gray-200 text-gray-700 hover:bg-gray-300 rounded-md py-2 px-4"
            >
              Clear Filters
            </button>
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center items-center h-64">
            <Loader className="animate-spin h-10 w-10 text-indigo-600" />
          </div>
        ) : error ? (
          <div className="text-center py-10 text-red-600">
            {error}
          </div>
        ) : products.length === 0 ? (
          <p className="text-center text-gray-500 py-10">No products found matching your criteria.</p>
        ) : (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
            
            {/* Pagination */}
            {totalPages > 1 && (
              <div className="mt-8">
                <Pagination
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={setCurrentPage}
                  totalItems={totalItems}
                  itemsPerPage={20}
                />
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default ProductListPage;
