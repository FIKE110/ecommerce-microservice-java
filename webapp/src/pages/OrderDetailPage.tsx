import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import type { Order, OrderItem } from '../types';
import {
  Loader, AlertCircle, ArrowLeft, Package, Calendar,
  Clock, CheckCircle, Truck, CreditCard, User
} from 'lucide-react';
import api from '../services/api';
import { motion } from 'framer-motion';
import { toast } from 'sonner';

const OrderActions = ({ order }: { order: Order }) => {
  const [invoiceData, setInvoiceData] = useState<any>(null);
  const [loadingInvoice, setLoadingInvoice] = useState(false);

  const handleReorder = async () => {
    try {
      await api.post(`/order/reorder/${order.id}`);
      toast.success('Order has been reordered successfully!');
      setTimeout(()=>window.location.reload(),500)
    } catch (err) {
      console.error("Error reordering:", err);
      toast.error('Failed to reorder.');
    }
  };

  const handleGetInvoice = async () => {
    setLoadingInvoice(true);
    try {
      const response = await api.get(`/order/invoice/${order.txnReference}`);
      setInvoiceData(response.data);
    } catch (err) {
      console.error("Error fetching invoice:", err);
      toast.error('Failed to load invoice.');
    } finally {
      setLoadingInvoice(false);
    }
  };

  const renderActions = () => {
    switch (order.status.toUpperCase()) {
      case 'PENDING':
      case 'FAILED':
        return (
          <button
            onClick={handleReorder}
            className="w-full text-center px-6 py-3 rounded-xl bg-yellow-500/20 text-yellow-400 border border-yellow-500/30 hover:bg-yellow-500/30 transition-colors"
          >
            Reorder
          </button>
        );
      case 'PLACED':
        return (
          <a
            href={order.paymentLink}
            target="_blank"
            rel="noopener noreferrer"
            className="w-full text-center block px-6 py-3 rounded-xl bg-green-500/20 text-green-400 border border-green-500/30 hover:bg-green-500/30 transition-colors"
          >
            Pay Here
          </a>
        );
      default:
        if (loadingInvoice) {
          return (
            <button
              disabled
              className="w-full text-center px-6 py-3 rounded-xl bg-blue-500/20 text-blue-400 border border-blue-500/30 opacity-50 cursor-not-allowed"
            >
              Loading Invoice...
            </button>
          );
        }

        if (invoiceData?.link) {
          return (
            <a
              href={invoiceData.link}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full text-center block px-6 py-3 rounded-xl bg-blue-500/20 text-blue-400 border border-blue-500/30 hover:bg-blue-500/30 transition-colors"
            >
              View Invoice
            </a>
          );
        }

        return (
          <button
            onClick={handleGetInvoice}
            className="w-full text-center px-6 py-3 rounded-xl bg-blue-500/20 text-blue-400 border border-blue-500/30 hover:bg-blue-500/30 transition-colors"
          >
            Get Invoice
          </button>
        );
    }
  };

  return (
    <motion.div className="mt-6">
      {renderActions()}
    </motion.div>
  );
};

const OrderDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchOrderDetails = async () => {
      if (!id) {
        setError('Order ID is required');
        setLoading(false);
        return;
      }

      try {
        const response = await api.get(`/order/${id}`);
        const data: Order = response.data;
        setOrder({ ...data, totalPrice: data.totalPrice / 100 });
      } catch (err) {
        console.error("Error fetching order details", err);
        setError('Failed to load order details.');
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetails();
  }, [id]);

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'completed':
        return 'bg-green-500/20 text-green-400 border-green-500/30';
      case 'shipped':
        return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      case 'processing':
        return 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30';
      default:
        return 'bg-gray-500/20 text-gray-400 border-gray-500/30';
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: { type: "spring", stiffness: 100 }
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-gray-900 flex justify-center items-center">
      <motion.div
        animate={{ rotate: 360 }}
        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
      >
        <Loader className="h-12 w-12 text-indigo-500" />
      </motion.div>
    </div>
  );

  if (error) return (
    <div className="min-h-screen bg-gray-900 flex justify-center items-center text-red-400">
      <div className="text-center">
        <AlertCircle className="h-12 w-12 mx-auto mb-4" />
        <p className="text-xl font-semibold">{error}</p>
        <Link to="/orders" className="mt-4 inline-block text-indigo-400 hover:text-indigo-300">
          Return to Orders
        </Link>
      </div>
    </div>
  );

  if (!order) return (
    <div className="min-h-screen bg-gray-900 flex justify-center items-center text-gray-400">
      <div className="text-center">
        <Package className="h-16 w-16 mx-auto mb-4 opacity-50" />
        <p className="text-xl">Order not found</p>
        <Link to="/orders" className="mt-4 inline-block text-indigo-400 hover:text-indigo-300">
          Return to Orders
        </Link>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-900 text-gray-100 font-sans selection:bg-indigo-500/30">
      {/* Background Gradients */}
      <div className="fixed inset-0 z-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] rounded-full bg-indigo-600/10 blur-[100px]" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] rounded-full bg-purple-600/10 blur-[100px]" />
      </div>

      <div className="relative z-10 container mx-auto px-4 py-12 max-w-6xl">
        <motion.div
          initial={{ x: -20, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          className="mb-8"
        >
          <Link
            to="/orders"
            className="inline-flex items-center text-gray-400 hover:text-white transition-colors group"
          >
            <ArrowLeft className="h-5 w-5 mr-2 transform group-hover:-translate-x-1 transition-transform" />
            Back to Orders
          </Link>
        </motion.div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="grid grid-cols-1 lg:grid-cols-3 gap-8"
        >
          {/* Main Content - Left Column */}
          <div className="lg:col-span-2 space-y-8">
            {/* Header Card */}
            <motion.div
              variants={itemVariants}
              className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-8 shadow-2xl relative overflow-hidden"
            >
              <div className="absolute top-0 right-0 p-8 opacity-10">
                <Package className="w-32 h-32" />
              </div>

              <div className="relative z-10">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
                  <div>
                    <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-gray-400">
                      Order #{order.id.slice(0, 8)}...
                    </h1>
                    <p className="text-gray-400 mt-1 flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      {new Date(order.orderTime).toLocaleDateString(undefined, {
                        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
                      })}
                    </p>
                  </div>
                  <div className={`px-4 py-2 rounded-full border ${getStatusColor(order.status)} flex items-center gap-2 font-medium`}>
                    {order.status.toLowerCase() === 'completed' ? <CheckCircle className="w-4 h-4" /> :
                      order.status.toLowerCase() === 'shipped' ? <Truck className="w-4 h-4" /> :
                        <Clock className="w-4 h-4" />}
                    {order.status}
                  </div>
                </div>

                <div className="flex items-end gap-2">
                  <span className="text-4xl font-bold text-white">₦{order.totalPrice.toFixed(2)}</span>
                  <span className="text-gray-400 mb-1">Total Amount</span>
                </div>
              </div>
            </motion.div>

            {/* Order Actions */}
            <OrderActions order={order} />

            {/* Order Items */}
            <motion.div variants={itemVariants}>
              <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                <Package className="w-5 h-5 text-indigo-400" />
                Items
              </h2>
              <div className="space-y-4">
                {order.products && order.products.length > 0 ? (
                  order.products.map((item: OrderItem, index) => (
                    <motion.div
                      key={item.productId}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: index * 0.1 }}
                      className="bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl p-4 flex items-center gap-4 hover:bg-white/10 transition-colors group"
                    >
                      <div className="w-16 h-16 rounded-xl bg-gray-800 flex items-center justify-center text-gray-500 group-hover:scale-105 transition-transform">
                        <Package className="w-8 h-8" />
                      </div>
                      <div className="flex-1">
                        <h3 className="font-medium text-lg text-white group-hover:text-indigo-300 transition-colors">
                          {item.productName}
                        </h3>
                        <p className="text-sm text-gray-400">ID: {item.productId}</p>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold text-white">₦{(item.price / 100).toFixed(2)}</p>
                        <p className="text-sm text-gray-400">Qty: {item.quantity}</p>
                      </div>
                    </motion.div>
                  ))
                ) : (
                  <div className="text-center py-12 bg-white/5 rounded-2xl border border-white/10 border-dashed">
                    <p className="text-gray-400">No items found</p>
                  </div>
                )}
              </div>
            </motion.div>
          </div>

          {/* Sidebar - Right Column */}
          <div className="space-y-6">
            {/* Timeline */}
            <motion.div
              variants={itemVariants}
              className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-6"
            >
              <h3 className="text-lg font-semibold mb-6 flex items-center gap-2">
                <Clock className="w-5 h-5 text-indigo-400" />
                Timeline
              </h3>
              <div className="relative pl-4 border-l-2 border-white/10 space-y-8">
                <div className="relative">
                  <div className="absolute -left-[21px] top-1 w-3 h-3 rounded-full bg-indigo-500 ring-4 ring-gray-900" />
                  <p className="text-sm text-gray-400 mb-1">Order Placed</p>
                  <p className="font-medium text-white">
                    {new Date(order.orderTime).toLocaleString()}
                  </p>
                </div>
                {order.shippingTime && (order.status == "SHIPPED" || order.status === "DELIVERED") && (
                  <div className="relative">
                    <div className="absolute -left-[21px] top-1 w-3 h-3 rounded-full bg-blue-500 ring-4 ring-gray-900" />
                    <p className="text-sm text-gray-400 mb-1">Shipped</p>
                    <p className="font-medium text-white">
                      {new Date(order.shippingTime).toLocaleString()}
                    </p>
                  </div>
                )}
                {order.deliveryTime && order.status === "DELIVERED" && (
                  <div className="relative">
                    <div className="absolute -left-[21px] top-1 w-3 h-3 rounded-full bg-green-500 ring-4 ring-gray-900" />
                    <p className="text-sm text-gray-400 mb-1">Delivered</p>
                    <p className="font-medium text-white">
                      {new Date(order.deliveryTime).toLocaleString()}
                    </p>
                  </div>
                )}
              </div>
            </motion.div>

            {/* Customer Info */}
            <motion.div
              variants={itemVariants}
              className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-6"
            >
              <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                <User className="w-5 h-5 text-purple-400" />
                Customer
              </h3>
              <div className="space-y-3">
                <div className="flex items-center gap-3 text-gray-300">
                  <div className="w-8 h-8 rounded-full bg-purple-500/20 flex items-center justify-center text-purple-400">
                    <User className="w-4 h-4" />
                  </div>
                  <div>
                    <p className="text-sm text-gray-400">Username</p>
                    <p className="font-medium">{order.username}</p>
                  </div>
                </div>
              </div>
            </motion.div>

            {/* Payment Info */}
            {order.txnReference && (
              <motion.div
                variants={itemVariants}
                className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-6"
              >
                <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                  <CreditCard className="w-5 h-5 text-emerald-400" />
                  Payment
                </h3>
                <div className="bg-emerald-500/10 rounded-xl p-4 border border-emerald-500/20">
                  <p className="text-sm text-emerald-400 mb-1">Transaction Ref</p>
                  <p className="font-mono text-sm text-emerald-200 break-all">
                    {order.txnReference}
                  </p>
                </div>
              </motion.div>
            )}
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default OrderDetailPage;