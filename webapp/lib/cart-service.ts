import { getKey } from "./keystore-service";
import { Product } from "./products-data";

const API_URL = process.env.NEXT_PUBLIC_GATEWAY_URL;

export interface CartItem {
  product: Product;
  quantity: number;
}

export const getCart = async (): Promise<CartItem[]> => {
  const response = await fetch(`${API_URL}/api/v1/cart`, {
    headers: {
      "Authorization": `Bearer ${getKey("access_token")}`,
    },
  });
  if (!response.ok) {
    throw new Error("Failed to fetch cart");
  }
  return response.json();
};

export const addItemToCart = async (productId: string, quantity: number): Promise<void> => {
  const response = await fetch(`${API_URL}/api/v1/cart/${productId}/add/${quantity}`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${getKey("access_token")}`,
      "Content-Type": "application/json",
    },
  });

  if (response.status !== 204) {
    throw new Error("An error occurred");
  }
};

export const removeItemFromCart = async (productId: string): Promise<void> => {
    const response = await fetch(`${API_URL}/api/v1/cart/${productId}/remove`, {
        method: 'POST',
        headers: {
            "Authorization": `Bearer ${getKey("access_token")}`,
        },
    });
    if (!response.ok) {
        throw new Error('Failed to remove item from cart');
    }
};

export const clearCart = async (): Promise<void> => {
    const response = await fetch(`${API_URL}/api/v1/cart`, {
        method: 'DELETE',
        headers: {
            "Authorization": `Bearer ${getKey("access_token")}`,
        },
    });
    if (!response.ok) {
        throw new Error('Failed to clear cart');
    }
};
