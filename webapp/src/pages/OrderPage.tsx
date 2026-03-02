import {useState, useEffect, useContext} from 'react';
import { Link } from 'react-router-dom';
import type { Order } from '../types';
import {
  Loader,
  AlertCircle,
  ShoppingBag,
  ExternalLink,
  Package,
  Truck,
  CheckCircle,
  Clock,
  Calendar,
  X
} from 'lucide-react';
import api from '../services/api';
import Pagination from '../components/Pagination';
import {AuthContext} from "../context/AuthContext.tsx";

const OrderPage = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const Auth=useContext(AuthContext)
  console.log(Auth)

  useEffect(() => {
    const fetchOrders = async () => {
      setLoading(true);
      try {

        const response = await api.get(`/order/username?username=${Auth.profile.username}&page=${currentPage - 1}&size=10&sort=createdAt`);
        const paginationData = response.data;
        
        setOrders(paginationData.content.map((p:Order)=>({...p,totalPrice:p.totalPrice/100})) || []);
        setTotalPages(paginationData.totalPages || 1);
        setTotalItems(paginationData.totalElements || 0);
      } catch (err) {
        console.error("Error fetching orders", err);
        setError('Failed to load orders.');
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [currentPage]);

  const getStatusIcon = (status: string) => {
    switch (status.toLowerCase()) {
      case 'delivered':
      case 'completed':
        return <CheckCircle className="h-5 w-5" />;
      case 'shipped':
        return <Truck className="h-5 w-5" />;
      case 'pending':
        return <Clock className="h-5 w-5" />;
      case 'failed':
        return <X className="h-5 w-5" />;
      default:
        return <Package className="h-5 w-5 " />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'delivered':
      case 'completed':
        return 'bg-green-100 text-green-800';
      case 'shipped':
        return 'bg-blue-100 text-blue-800';
      case 'paid':
        return 'bg-indigo-100 text-indigo-800';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'failed':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-96">
      <Loader className="animate-spin h-10 w-10 text-indigo-600" />
    </div>
  );

  if (error) return (
    <div className="text-center py-10 text-red-600 flex items-center justify-center">
      <AlertCircle className="h-6 w-6 mr-2" />
      {error}
    </div>
  );

  return (
    <div className="bg-gray-50 min-h-screen py-12">
      <div className="container-custom">
        <h1 className="text-3xl font-extrabold tracking-tight text-gray-900 mb-8">Your Orders</h1>
        
        {orders.length === 0 ? (
          <div className="text-center py-10 bg-white rounded-lg shadow-md">
            <ShoppingBag className="h-12 w-12 mx-auto text-gray-400" />
            <h2 className="mt-4 text-xl font-semibold text-gray-700">No orders yet</h2>
            <p className="mt-2 text-gray-500">Looks like you haven't placed an order.</p>
          </div>
        ) : (
          <>
            <div className="space-y-8">
              {orders.map((order) => (
                <Link 
                  key={order.id} 
                  to={`/orders/${order.id}`}
                  className="block bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-all duration-200"
                >
                <div className="p-6">
                  {/* Header with Order ID and Status */}
                  <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start mb-4">
                    <div className="space-y-2">
                      <div className="flex items-center space-x-2">
                        <p className="text-sm font-medium text-gray-500">Order ID:</p>
                        <p className="text-sm font-semibold text-gray-800 font-mono">{order.id.slice(0, 8)}...</p>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Calendar className="h-4 w-4 text-gray-400" />
                        <p className="text-sm text-gray-600">
                          {new Date(order.orderTime).toLocaleDateString()}
                        </p>
                      </div>

                    </div>
                    <div className="mt-4 sm:mt-0 text-right space-y-2">
                      <p className="text-2xl font-bold text-indigo-600">₦{order.totalPrice.toFixed(2)}</p>
                      <div className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(order.status)}`}>
                        {getStatusIcon(order.status)}
                        <span className="ml-2">{order.status}</span>
                      </div>
                    </div>
                  </div>





                  {/* Timeline Info */}
                  <div className="flex justify-between items-center text-xs text-gray-500 mb-4">
                    <div>
                      {order.shippingTime ? (
                        <span>Shipped: {new Date(order.shippingTime).toLocaleDateString()}</span>
                      ) : (
                        <span>Not shipped yet</span>
                      )}
                    </div>
                    <div>
                      {order.deliveryTime ? (
                        <span>Delivered: {new Date(order.deliveryTime).toLocaleDateString()}</span>
                      ) : order.status.toLowerCase() === 'delivered' ? (
                        <span>Delivery pending</span>
                      ) : null}
                    </div>
                  </div>
                </div>

                {/* Footer with View Details */}
                <div className="bg-gradient-to-r from-gray-50 to-gray-100 px-6 py-3 border-t border-gray-200">
                  <div className="flex items-center justify-center text-indigo-600 hover:text-indigo-800 transition-colors">
                    <span className="text-sm font-medium">View Full Details</span>
                    <ExternalLink className="h-4 w-4 ml-2" />
                  </div>
                </div>
              </Link>
            ))}
          </div>
          
          {/* Pagination */}
          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
              totalItems={totalItems}
              itemsPerPage={10}
            />
          )}
        </>
        )}
      </div>
    </div>
  );
};

export default OrderPage;
