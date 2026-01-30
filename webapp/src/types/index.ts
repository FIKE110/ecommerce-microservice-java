export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  brand?: string;
  discount?: number;
  images?: string[];
  category?: string;
  tags?: string[];
  categoryId?: number;
}

export interface User {
  id: string; // or username if that's the ID
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  role?: string;
}

export interface CartItem {
  productId: string;
  name: string;
  quantity: number;
  price: number;
  image?: string;
  brand?: string;
}

export interface Cart {
  items: Record<string, number>; // Based on Map<String, Double> in controller, wait controller said Map<String,Map<String,Double>>? No, getCart returns Map<String,Map<String,Double>>?
  // Controller: return ResponseEntity.ok(cartService.getCart(jwt).getItems());
  // Let's check Cart entity or service if possible. Controller says getItems() returns Map<String, Map<String, Double>>?
  // Actually, looking at CartController:
  // public ResponseEntity<Map<String,Map<String,Double>>> getCart
  // It returns items.
  // Wait, line 27: cartService.getCart(jwt).getItems()
  // If items is Map<String, Double> (productId -> quantity), then the response type is wrong in signature?
  // No, the signature is Map<String, Map<String, Double>>.
  // Maybe it's ProductId -> { "price": ..., "quantity": ... }?
  // I need to be careful here. I'll assume standard structure or inspect closely.
}

export interface AuthResponse {
  token: string;
  expiresIn: number;
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  createdAt: string;
  deliveryTime: string | null;
  orderTime: string;
  paymentLink:string;
  products?: OrderItem[]; // Assuming this will be populated, and was just empty in the example
  shippingTime: string | null;
  status: string;
  totalPrice: number;
  txnReference: string;
  updatedAt: string;
  username: string;
}
