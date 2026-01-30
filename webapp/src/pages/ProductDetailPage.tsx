import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import type { Product } from '../types';
import { ShoppingCart, ArrowLeft, Loader, ChevronLeft, ChevronRight, Tag, Percent, Package } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';

const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await api.get(`/product/${id}`);
        const p:Product=response.data?.data?.data
        setProduct({...p,price:p.price/100});
      } catch (err) {
        console.error("Error fetching product", err);
        setError('Failed to load product');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchProduct();
    }
  }, [id]);

  const handleQuantityChange = (change: number) => {
    setQuantity(prev => {
      const newQuantity = prev + change;
      if (newQuantity < 1) return 1;
      if (product?.quantity && newQuantity > product.quantity) return product.quantity;
      return newQuantity;
    });
  };

  const handleImageChange = (index: number) => {
    setSelectedImageIndex(index);
  };

  const nextImage = () => {
    if (product?.images && product.images.length > 0) {
      setSelectedImageIndex((prev) => (prev + 1) % product.images!.length);
    }
  };

  const prevImage = () => {
    if (product?.images && product.images.length > 0) {
      setSelectedImageIndex((prev) => (prev - 1 + product.images!.length) % product.images!.length);
    }
  };

  const calculateDiscountedPrice = () => {
    if (product?.discount) {
      return product.price * (1 - product.discount / 100);
    }
    return product?.price;
  };

  const addToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!product || product.quantity === 0) return;
    try {
      await api.post(`/cart/${product?.id}/add/${quantity}`);
      toast.success(`${quantity} item(s) added to cart!`);
    } catch (error) {
      console.error('Failed to add to cart', error);
      toast.error('Failed to add to cart');
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-96">
      <Loader className="animate-spin h-10 w-10 text-indigo-600" />
    </div>
  );

  if (error || !product) return (
    <div className="text-center py-10 text-red-600">
      {error || 'Product not found'}
    </div>
  );

  const isOutOfStock = !product.quantity || product.quantity <= 0;

  return (
    <div className="bg-white min-h-screen py-12">
      <div className="container-custom">
        <button 
          onClick={() => navigate(-1)} 
          className="flex items-center text-gray-600 hover:text-indigo-600 mb-8"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Back to products
        </button>
        
        <div className="lg:grid lg:grid-cols-2 lg:gap-x-8 lg:items-start">
          {/* Image gallery */}
          <div className="space-y-4">
            {/* Main image */}
            <div className="relative w-full aspect-square bg-gray-200 rounded-lg overflow-hidden">
              {product.images && product.images.length > 0 ? (
                <>
                  <img
                    src={product.images[selectedImageIndex]}
                    alt={`${product.name} - Image ${selectedImageIndex + 1}`}
                    className="w-full h-full object-center object-cover"
                  />
                  {product.images.length > 1 && (
                    <>
                      <button
                        onClick={prevImage}
                        className="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full shadow-md"
                      >
                        <ChevronLeft className="h-5 w-5 text-gray-800" />
                      </button>
                      <button
                        onClick={nextImage}
                        className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full shadow-md"
                      >
                        <ChevronRight className="h-5 w-5 text-gray-800" />
                      </button>
                    </>
                  )}
                </>
              ) : (
                <div className="flex items-center justify-center h-full bg-gray-100 text-gray-400">
                  <Package className="h-16 w-16" />
                  <span className="ml-2">No Image Available</span>
                </div>
              )}
            </div>

            {/* Thumbnail gallery */}
            {product.images && product.images.length > 1 && (
              <div className="flex space-x-2 overflow-x-auto pb-2">
                {product.images.map((image, index) => (
                  <button
                    key={index}
                    onClick={() => handleImageChange(index)}
                    className={`flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 ${
                      selectedImageIndex === index 
                        ? 'border-indigo-600' 
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <img
                      src={image}
                      alt={`${product.name} thumbnail ${index + 1}`}
                      className="w-full h-full object-center object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Product info */}
          <div className="mt-10 sm:mt-16 lg:mt-0">
            <h1 className="text-3xl font-extrabold tracking-tight text-gray-900">{product.name}</h1>
            
            {/* Brand and Category */}
            <div className="mt-2 flex flex-wrap items-center gap-2 text-sm text-gray-600">
              {product.brand && (
                <span className="font-medium">Brand: {product.brand}</span>
              )}
              {product.brand && product.category && <span>•</span>}
              {product.category && (
                <span className="bg-gray-100 px-2 py-1 rounded-md">{product.category}</span>
              )}
            </div>

            {/* Price and Discount */}
            <div className="mt-4">
              <h2 className="sr-only">Product information</h2>
              <div className="flex items-baseline space-x-2">
                {product.discount ? (
                  <>
                    <p className="text-3xl font-bold text-indigo-600">
                      ₦{calculateDiscountedPrice()?.toFixed(2)}
                    </p>
                    <p className="text-lg text-gray-500 line-through">
                      ₦{product.price.toFixed(2)}
                    </p>
                    <span className="bg-red-100 text-red-800 px-2 py-1 rounded-md text-sm font-medium flex items-center">
                      <Percent className="h-3 w-3 mr-1" />
                      {product.discount}% OFF
                    </span>
                  </>
                ) : (
                  <p className="text-3xl font-bold text-indigo-600">
                    ₦{product.price.toFixed(2)}
                  </p>
                )}
              </div>
            </div>

            {/* Stock Status */}
            <div className="mt-3">
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
                product.quantity > 0 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              }`}>
                {product.quantity > 0 ? (
                  <>
                    <Package className="h-4 w-4 mr-1" />
                    In Stock ({product.quantity} available)
                  </>
                ) : (
                  'Out of Stock'
                )}
              </span>
            </div>

            {/* Tags */}
            {product.tags && product.tags.length > 0 && (
              <div className="mt-4">
                <div className="flex flex-wrap gap-2">
                  {product.tags.map((tag, index) => (
                    <span
                      key={index}
                      className="inline-flex items-center px-3 py-1 rounded-md text-sm bg-indigo-50 text-indigo-700 hover:bg-indigo-100"
                    >
                      <Tag className="h-3 w-3 mr-1" />
                      {tag}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {/* Description */}
            <div className="mt-6">
              <h3 className="text-lg font-medium text-gray-900">Description</h3>
              <div className="mt-2 text-base text-gray-700">
                <p>{product.description}</p>
              </div>
            </div>

            <div className="mt-10">
              {isOutOfStock ? (
                <div className="flex items-center justify-center px-8 py-3 border border-transparent rounded-md bg-gray-200 text-gray-500 text-base font-medium">
                  Out of Stock
                </div>
              ) : (
                <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                  <div className="flex items-center border rounded-md">
                    <button onClick={() => handleQuantityChange(-1)} className="px-4 py-2 text-lg font-bold text-gray-600 hover:bg-gray-100 rounded-l-md">-</button>
                    <input type="text" value={quantity} readOnly className="w-12 text-center border-0 focus:ring-0" />
                    <button onClick={() => handleQuantityChange(1)} className="px-4 py-2 text-lg font-bold text-gray-600 hover:bg-gray-100 rounded-r-md">+</button>
                  </div>
                  <button
                    type="button"
                    onClick={addToCart}
                    className="flex-1 bg-indigo-600 border border-transparent rounded-md py-3 px-8 flex items-center justify-center text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:bg-indigo-300"
                    disabled={isOutOfStock}
                  >
                    <ShoppingCart className="h-5 w-5 mr-2" />
                    Add to cart
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
