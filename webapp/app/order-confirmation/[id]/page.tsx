"use client"

import Link from "next/link"
import { useParams } from "next/navigation"
import { CheckCircle, Package, Truck, Home } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useOrdersStore } from "@/lib/orders-store"

export default function OrderConfirmationPage() {
  const params = useParams()
  const orderId = params.id as string
  const { getOrders } = useOrdersStore()
  const orders = getOrders()
  const order = orders.find((o) => o.id === orderId)

  if (!order) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center px-4">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Order not found</h1>
          <Link href="/products">
            <Button>Continue Shopping</Button>
          </Link>
        </div>
      </div>
    )
  }

  const orderDate = new Date(order.date).toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  })

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        {/* Success Message */}
        <div className="text-center mb-12">
          <CheckCircle className="w-16 h-16 text-primary mx-auto mb-4" />
          <h1 className="text-4xl font-bold mb-2">Order Confirmed!</h1>
          <p className="text-muted-foreground text-lg">Thank you for your purchase</p>
        </div>

        {/* Order Details */}
        <div className="bg-secondary/30 rounded-lg p-8 space-y-8 mb-8">
          {/* Order Number and Date */}
          <div className="grid md:grid-cols-2 gap-8 border-b border-border pb-8">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Order Number</p>
              <p className="text-2xl font-bold">{order.id}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">Order Date</p>
              <p className="text-2xl font-bold">{orderDate}</p>
            </div>
          </div>

          {/* Shipping Address */}
          <div>
            <h2 className="text-xl font-bold mb-4">Shipping Address</h2>
            <div className="text-muted-foreground space-y-1">
              <p>{order.shippingAddress.fullName}</p>
              <p>{order.shippingAddress.address}</p>
              <p>
                {order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.zipCode}
              </p>
              <p>{order.shippingAddress.country}</p>
            </div>
          </div>

          {/* Order Items */}
          <div>
            <h2 className="text-xl font-bold mb-4">Order Items</h2>
            <div className="space-y-3">
              {order.items.map((item) => (
                <div
                  key={item.id}
                  className="flex justify-between items-center pb-3 border-b border-border last:border-0"
                >
                  <div>
                    <p className="font-medium">{item.name}</p>
                    <p className="text-sm text-muted-foreground">Quantity: {item.quantity}</p>
                  </div>
                  <p className="font-bold">${(item.price * item.quantity).toFixed(2)}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Order Summary */}
          <div className="border-t border-border pt-8 space-y-3">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Subtotal</span>
              <span>${order.subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Shipping</span>
              <span>{order.shipping === 0 ? "Free" : `$${order.shipping.toFixed(2)}`}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Tax</span>
              <span>${order.tax.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-lg font-bold border-t border-border pt-3">
              <span>Total</span>
              <span className="text-primary">${order.total.toFixed(2)}</span>
            </div>
          </div>
        </div>

        {/* Order Status */}
        <div className="bg-secondary/30 rounded-lg p-8 mb-8">
          <h2 className="text-xl font-bold mb-6">Order Status</h2>
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-primary text-primary-foreground flex items-center justify-center">
                <CheckCircle className="w-6 h-6" />
              </div>
              <div>
                <p className="font-semibold">Order Confirmed</p>
                <p className="text-sm text-muted-foreground">Your order has been received</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-secondary text-muted-foreground flex items-center justify-center">
                <Package className="w-6 h-6" />
              </div>
              <div>
                <p className="font-semibold">Processing</p>
                <p className="text-sm text-muted-foreground">We're preparing your order</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-secondary text-muted-foreground flex items-center justify-center">
                <Truck className="w-6 h-6" />
              </div>
              <div>
                <p className="font-semibold">Shipped</p>
                <p className="text-sm text-muted-foreground">Coming soon</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-secondary text-muted-foreground flex items-center justify-center">
                <Home className="w-6 h-6" />
              </div>
              <div>
                <p className="font-semibold">Delivered</p>
                <p className="text-sm text-muted-foreground">Coming soon</p>
              </div>
            </div>
          </div>
        </div>

        {/* Actions */}
        <div className="flex flex-col sm:flex-row gap-4">
          <Link href="/products" className="flex-1">
            <Button size="lg" className="w-full">
              Continue Shopping
            </Button>
          </Link>
          <Link href="/profile" className="flex-1">
            <Button size="lg" variant="outline" className="w-full bg-transparent">
              View Orders
            </Button>
          </Link>
        </div>
      </div>
    </div>
  )
}
