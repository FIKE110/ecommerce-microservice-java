import { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, Trash2, CreditCard } from 'lucide-react';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
const CartPage = () => {
  const [cartItems, setCartItems] = useState<Record<string, { quantity: number; price?: number }>>({});
  const [isPaymentLoading, setIsPaymentLoading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetchCart();
  }, []);

  const [checkoutUrl, setCheckoutUrl] = useState<string | null>(null);
  const fetchCheckoutLink = async () => {
    setIsPaymentLoading(true);
    try {
      const response = await api.post('/cart/checkout');

      if (response.data?.checkout_url) {
        setCheckoutUrl(response.data.checkout_url);
        toast.success("Checkout ready");
      } else {
        toast.error('Checkout URL not found');
      }
    } catch (err) {
      console.error('Checkout failed', err);
      toast.error('Checkout failed');
    } finally {
      setIsPaymentLoading(false);
    }
  };

  const openCheckout = () => {
    if (!checkoutUrl) return;

    window.open(checkoutUrl, "_blank");

    // optional: reset + reload cart
    setCheckoutUrl(null);

    setTimeout(() => {
      window.location.reload();
    }, 500);
  };

  const fetchCart = async () => {
    try {
      const response = await api.get('/cart');
      const data = response.data;
      const enrichedCart = await Promise.all(
          Object.entries(data).map(async ([productId, item]) => {
            const productRes = await api.get(`/product/${productId}/name`);
            return {
              productId,
              quantity: item.quantity,
              price: item.price / 100,
              name: productRes.data.name
            };
          })
      );
      setCartItems(enrichedCart);
    } catch (err) {
      console.error("Error fetching cart", err);
      setError('Failed to load cart');
    } finally {
      setLoading(false);
    }
  };
  const removeFromCart = async (productId: string) => {
    try {
      await api.post(`/cart/${productId}/delete`);
      fetchCart();
    } catch (err) {
      console.error("Failed to remove item", err);
      toast.error('Failed to remove item');
    }
  };
  const checkout = async () => {
    setIsPaymentLoading(true);
    try {
      const response = await api.post('/cart/checkout');
      if (response.data?.checkout_url) {
        window.location.href = response.data.checkout_url;
      } else {
        toast.error('Checkout URL not found');
      }
    } catch (err) {
      console.error('Checkout failed', err);
      toast.error('Checkout failed');
    } finally {
      setIsPaymentLoading(false);
    }
  };
  if (loading) return (
      <div className="flex justify-center items-center h-96">
        <Loader className="animate-spin h-10 w-10 text-indigo-600" />
      </div>
  );
  if (error) return (
      <div className="text-center py-10 text-red-600">{error}</div>
  );
  const items = Object.entries(cartItems);
  const total = items.reduce((sum, [_, details]) => {
    const d = details as any;
    return sum + (d.price || 0) * (d.quantity || 0);
  }, 0);
  return (
      <div className="bg-gray-50 min-h-screen py-12">
        <div className="container-custom">
          <h2 className="text-3xl font-bold text-gray-900 mb-8">Shopping Cart</h2>
          {items.length === 0 ? (
              <div className="text-center py-12 bg-white rounded-lg shadow">
                <p className="text-xl text-gray-500 mb-6">Your cart is empty.</p>
                <Link to="/products" className="text-indigo-600 hover:text-indigo-800 font-medium">
                  Continue Shopping
                </Link>
              </div>
          ) : (
              <div className="bg-white shadow overflow-hidden rounded-lg">
                <ul className="divide-y divide-gray-200">
                  {items.map(([_, details]) => {
                    const d = details as any;
                    const productId = d.productId;
                    return (
                        <li key={productId} className="p-4 sm:p-6 flex flex-wrap items-center justify-between">
                          <div className="flex items-center mb-4 sm:mb-0">
                            <div className="ml-4">
                              <p className="text-sm font-medium text-gray-900">{d.name}</p>
                              <p className="text-sm text-gray-500">Quantity: {d.quantity}</p>
                            </div>
                          </div>
                          <div className="flex items-center">
                      <span className="text-gray-900 font-medium mr-4 sm:mr-6">
                        ₦{((d.price || 0) * (d.quantity || 0)).toFixed(2)}
                      </span>
                            <button
                                onClick={() => removeFromCart(productId)}
                                className="text-red-600 hover:text-red-800"
                            >
                              <Trash2 className="h-5 w-5" />
                            </button>
                          </div>
                        </li>
                    );
                  })}
                </ul>
                <div className="p-4 sm:p-6 bg-gray-50 border-t border-gray-200 flex flex-wrap justify-between items-center">
                  <span className="text-xl font-bold text-gray-900 mb-4 sm:mb-0">Total: ₦{total.toFixed(2)}</span>
                  <button
                      onClick={checkoutUrl ? openCheckout : fetchCheckoutLink}
                      disabled={isPaymentLoading}
                      className="w-full sm:w-auto inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none disabled:opacity-50"
                  >
                    {isPaymentLoading ? (
                        "Loading..."
                    ) : checkoutUrl ? (
                        <>
                          <CreditCard className="mr-2 h-5 w-5" />
                          Checkout
                        </>
                    ) : (
                        <>
                          <CreditCard className="mr-2 h-5 w-5" />
                          Proceed to Payment
                        </>
                    )}
                  </button>
                </div>
              </div>
          )}
        </div>
      </div>
  );
};
export default CartPage;