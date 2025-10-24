import { create } from "zustand"
import { persist } from "zustand/middleware"
import type { CartItem } from "./cart-store"

export interface Order {
  id: string
  date: string
  items: CartItem[]
  subtotal: number
  shipping: number
  tax: number
  total: number
  status: "pending" | "processing" | "shipped" | "delivered"
  shippingAddress: {
    fullName: string
    email: string
    address: string
    city: string
    state: string
    zipCode: string
    country: string
  }
}

interface OrdersStore {
  orders: Order[]
  addOrder: (order: Order) => void
  getOrders: () => Order[]
}

export const useOrdersStore = create<OrdersStore>()(
  persist(
    (set, get) => ({
      orders: [],

      addOrder: (order: Order) => {
        set((state) => ({
          orders: [order, ...state.orders],
        }))
      },

      getOrders: () => {
        return get().orders
      },
    }),
    {
      name: "orders-store",
    },
  ),
)
