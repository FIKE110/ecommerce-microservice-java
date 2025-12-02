import { create } from "zustand";
import { getCart, addItemToCart, removeItemFromCart, clearCart as clearCartAPI } from "./cart-service";
import type { Product } from "./products-data";

export interface CartItem extends Product {
  quantity: number;
}

interface CartStore {
  items: CartItem[];
  isLoading: boolean;
  error: string | null;
  fetchCart: () => Promise<void>;
  addItem: (product: Product, quantity?: number) => Promise<void>;
  removeItem: (productId: string) => Promise<void>;
  updateQuantity: (productId: string, quantity: number) => void;
  clearCart: () => void;
  getTotalPrice: () => number;
  getTotalItems: () => number;
}

export const useCartStore = create<CartStore>((set, get) => ({
  items: [],
  isLoading: false,
  error: null,

  fetchCart: async () => {
    set({ isLoading: true, error: null });
    try {
      const items = await getCart();
      // @ts-ignore
      set({ items, isLoading: false });
    } catch (error) {
      set({ error: (error as Error).message, isLoading: false });
    }
  },


  
  addItem: async (product: Product, quantity = 1) => {
    try {
      await addItemToCart(product.id.toString(), quantity);
      set((state) => {
        const existingItem = state.items.find((item) => item.id === product.id);

        if (existingItem) {
          return {
            items: state.items.map((item) =>
              item.id === product.id
                ? { ...item, quantity: item.quantity + quantity }
                : item
            ),
          };
        }

        return {
          items: [...state.items, { ...product, quantity }],
        };
      });
    } catch (error) {
      set({ error: (error as Error).message });
    }
  },

  removeItem: async (productId: string) => {
    try {
      await removeItemFromCart(productId.toString());
      set((state) => ({
        items: state.items.filter((item) => item.id !== productId),
      }));
    } catch (error) {
      set({ error: (error as Error).message });
    }
  },

  updateQuantity: (productId: string, quantity: number) => {
    set((state) => ({
      items: state.items.map((item) =>
        item.id === productId ? { ...item, quantity } : item
      ),
    }));
  },

  clearCart: async () => {
    try {
      await clearCartAPI();
      set({ items: [] });
    } catch (error) {
      set({ error: (error as Error).message });
    }
  },

  getTotalPrice: () => {
    return get().items.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  },

  getTotalItems: () => {
    return get().items.reduce((total, item) => total + item.quantity, 0);
  },
}));
