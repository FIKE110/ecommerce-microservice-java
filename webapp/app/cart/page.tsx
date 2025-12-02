"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { ShoppingCart, Search, Menu, X, Trash2, Plus, Minus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useCartStore } from "@/lib/cart-store"
import { useState } from "react"

export default function CartPage() {
  const router = useRouter()
  const { items, removeItem, updateQuantity, getTotalPrice, clearCart } = useCartStore()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const subtotal = getTotalPrice()
  const shipping = subtotal > 100 ? 0 : 10
  const tax = subtotal * 0.08
  const total = subtotal + shipping + tax

  const handleCheckout = () => {
    if (items.length > 0) {
      router.push("/checkout")
    }
  }

  return (
    <div className="min-h-screen bg-background text-foreground">
      {/* Navigation */}
      <nav className="sticky top-0 z-50 bg-background/95 backdrop-blur border-b border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <Link href="/" className="text-2xl font-bold tracking-tight">
              LUXE
            </Link>

            <div className="hidden xmd:flex items-center gap-8">
              <Link href="/products" className="text-sm hover:text-primary transition-colors">
                Shop
              </Link>
              <a href="#" className="text-sm hover:text-primary transition-colors">
                Collections
              </a>
              <a href="#" className="text-sm hover:text-primary transition-colors">
                About
              </a>
            </div>

            <div className="flex items-center gap-4">
              <button className="p-2 hover:bg-secondary rounded-lg transition-colors">
                <Search className="w-5 h-5" />
              </button>
              <Link href="/cart" className="p-2 hover:bg-secondary rounded-lg transition-colors relative">
                <ShoppingCart className="w-5 h-5" />
                <span className="absolute top-1 right-1 w-2 h-2 bg-primary rounded-full"></span>
              </Link>
              <button
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                className="md:hidden p-2 hover:bg-secondary rounded-lg transition-colors"
              >
                {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </button>
            </div>
          </div>

          {mobileMenuOpen && (
            <div className="md:hidden pb-4 space-y-2">
              <Link href="/products" className="block px-4 py-2 hover:bg-secondary rounded-lg transition-colors">
                Shop
              </Link>
              <a href="#" className="block px-4 py-2 hover:bg-secondary rounded-lg transition-colors">
                Collections
              </a>
              <a href="#" className="block px-4 py-2 hover:bg-secondary rounded-lg transition-colors">
                About
              </a>
            </div>
          )}
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2">Shopping Cart</h1>
          <p className="text-muted-foreground">{items.length} item(s) in your cart</p>
        </div>

        {items.length > 0 ? (
          <div className="grid lg:grid-cols-3 gap-8">
            {/* Cart Items */}
            <div className="lg:col-span-2 space-y-4">
              {items.map((item) => (
                <div
                  key={item.id}
                  className="flex gap-4 p-4 border border-border rounded-lg hover:bg-secondary/30 transition-colors"
                >
                  {/* Product Image */}
                  <Link href={`/products/${item.id}`} className="flex-shrink-0">
                    <img
                      src={item.image || "/placeholder.svg"}
                      alt={item.name}
                      className="w-24 h-24 object-cover rounded-lg"
                    />
                  </Link>

                  {/* Product Info */}
                  <div className="flex-1 min-w-0">
                    <Link href={`/products/${item.id}`}>
                      <h3 className="font-semibold hover:text-primary transition-colors line-clamp-2">{item.name}</h3>
                    </Link>
                    <p className="text-sm text-muted-foreground mb-2">{item.category}</p>
                    <p className="text-lg font-bold text-primary">${item.price}</p>
                  </div>

                  {/* Quantity Controls */}
                  <div className="flex flex-col items-end gap-4">
                    <button
                      onClick={() => removeItem(item.id)}
                      className="p-2 text-destructive hover:bg-destructive/10 rounded-lg transition-colors"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>

                    <div className="flex items-center border border-input rounded-lg">
                      <button
                        onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                        className="px-3 py-1 hover:bg-secondary transition-colors"
                      >
                        <Minus className="w-4 h-4" />
                      </button>
                      <span className="px-4 py-1 border-l border-r border-input">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="px-3 py-1 hover:bg-secondary transition-colors"
                      >
                        <Plus className="w-4 h-4" />
                      </button>
                    </div>

                    <p className="text-lg font-bold">${(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                </div>
              ))}
            </div>

            {/* Order Summary */}
            <div className="lg:col-span-1">
              <div className="sticky top-20 bg-secondary/30 rounded-lg p-6 space-y-4">
                <h2 className="text-xl font-bold">Order Summary</h2>

                <div className="space-y-3 border-b border-border pb-4">
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Subtotal</span>
                    <span>${subtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Shipping</span>
                    <span>{shipping === 0 ? "Free" : `$${shipping.toFixed(2)}`}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Tax</span>
                    <span>${tax.toFixed(2)}</span>
                  </div>
                </div>

                <div className="flex justify-between text-lg font-bold">
                  <span>Total</span>
                  <span className="text-primary">${total.toFixed(2)}</span>
                </div>

                {subtotal <= 100 && (
                  <div className="bg-accent/10 border border-accent/30 rounded-lg p-3 text-sm">
                    <p className="text-accent-foreground">Add ${(100 - subtotal).toFixed(2)} more for free shipping!</p>
                  </div>
                )}

                <Button onClick={handleCheckout} size="lg" className="w-full">
                  Proceed to Checkout
                </Button>

                <Link href="/products">
                  <Button variant="outline" size="lg" className="w-full bg-transparent">
                    Continue Shopping
                  </Button>
                </Link>

                <button
                  onClick={() => clearCart()}
                  className="w-full text-sm text-destructive hover:text-destructive/80 transition-colors py-2"
                >
                  Clear Cart
                </button>
              </div>
            </div>
          </div>
        ) : (
          <div className="text-center py-16">
            <ShoppingCart className="w-16 h-16 text-muted-foreground mx-auto mb-4 opacity-50" />
            <h2 className="text-2xl font-bold mb-2">Your cart is empty</h2>
            <p className="text-muted-foreground mb-8">Start shopping to add items to your cart</p>
            <Link href="/products">
              <Button size="lg">Start Shopping</Button>
            </Link>
          </div>
        )}
      </div>
    </div>
  )
}
