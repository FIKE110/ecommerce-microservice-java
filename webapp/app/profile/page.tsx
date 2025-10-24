"use client"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { ShoppingCart, Search, Menu, X, LogOut, Package, User, Settings } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useAuthStore } from "@/lib/auth-store"
import { useOrdersStore } from "@/lib/orders-store"

export default function ProfilePage() {
  const router = useRouter()
  const { user, logout } = useAuthStore()
  const { getOrders } = useOrdersStore()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const [activeTab, setActiveTab] = useState<"profile" | "orders">("profile")

  const orders = getOrders()

  if (!user) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center px-4">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Please sign in to view your profile</h1>
          <Link href="/login">
            <Button size="lg">Sign In</Button>
          </Link>
        </div>
      </div>
    )
  }

  const handleLogout = () => {
    logout()
    router.push("/")
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

            <div className="hidden md:flex items-center gap-8">
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
          <h1 className="text-4xl font-bold mb-2">My Account</h1>
          <p className="text-muted-foreground">Manage your profile and view your orders</p>
        </div>

        <div className="grid lg:grid-cols-4 gap-8">
          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-secondary/30 rounded-lg p-6 space-y-4 sticky top-20">
              {/* User Info */}
              <div className="flex items-center gap-4 pb-4 border-b border-border">
                <div className="w-12 h-12 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-lg font-bold">
                  {user.name.charAt(0).toUpperCase()}
                </div>
                <div>
                  <p className="font-semibold">{user.name}</p>
                  <p className="text-sm text-muted-foreground">{user.email}</p>
                </div>
              </div>

              {/* Navigation */}
              <nav className="space-y-2">
                <button
                  onClick={() => setActiveTab("profile")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition-colors ${
                    activeTab === "profile"
                      ? "bg-primary text-primary-foreground"
                      : "hover:bg-secondary text-foreground"
                  }`}
                >
                  <User className="w-5 h-5" />
                  <span>Profile</span>
                </button>

                <button
                  onClick={() => setActiveTab("orders")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition-colors ${
                    activeTab === "orders" ? "bg-primary text-primary-foreground" : "hover:bg-secondary text-foreground"
                  }`}
                >
                  <Package className="w-5 h-5" />
                  <span>Orders</span>
                </button>

                <button className="w-full flex items-center gap-3 px-4 py-2 rounded-lg hover:bg-secondary transition-colors text-foreground">
                  <Settings className="w-5 h-5" />
                  <span>Settings</span>
                </button>
              </nav>

              {/* Logout */}
              <button
                onClick={handleLogout}
                className="w-full flex items-center gap-3 px-4 py-2 rounded-lg hover:bg-destructive/10 text-destructive transition-colors mt-4"
              >
                <LogOut className="w-5 h-5" />
                <span>Sign Out</span>
              </button>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            {activeTab === "profile" ? (
              // Profile Tab
              <div className="space-y-6">
                {/* Personal Information */}
                <div className="bg-secondary/30 rounded-lg p-6">
                  <h2 className="text-2xl font-bold mb-6">Personal Information</h2>

                  <div className="space-y-4">
                    <div className="grid md:grid-cols-2 gap-4">
                      <div>
                        <label className="text-sm font-medium text-muted-foreground">Full Name</label>
                        <p className="text-lg font-semibold mt-1">{user.name}</p>
                      </div>
                      <div>
                        <label className="text-sm font-medium text-muted-foreground">Email Address</label>
                        <p className="text-lg font-semibold mt-1">{user.email}</p>
                      </div>
                    </div>

                    <Button variant="outline" className="bg-transparent">
                      Edit Profile
                    </Button>
                  </div>
                </div>

                {/* Account Statistics */}
                <div className="grid md:grid-cols-3 gap-4">
                  <div className="bg-secondary/30 rounded-lg p-6 text-center">
                    <p className="text-3xl font-bold text-primary">{orders.length}</p>
                    <p className="text-sm text-muted-foreground mt-2">Total Orders</p>
                  </div>

                  <div className="bg-secondary/30 rounded-lg p-6 text-center">
                    <p className="text-3xl font-bold text-primary">
                      ${orders.reduce((sum, order) => sum + order.total, 0).toFixed(2)}
                    </p>
                    <p className="text-sm text-muted-foreground mt-2">Total Spent</p>
                  </div>

                  <div className="bg-secondary/30 rounded-lg p-6 text-center">
                    <p className="text-3xl font-bold text-primary">
                      {orders.filter((o) => o.status === "delivered").length}
                    </p>
                    <p className="text-sm text-muted-foreground mt-2">Delivered</p>
                  </div>
                </div>

                {/* Preferences */}
                <div className="bg-secondary/30 rounded-lg p-6">
                  <h2 className="text-2xl font-bold mb-6">Preferences</h2>

                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-semibold">Email Notifications</p>
                        <p className="text-sm text-muted-foreground">Receive updates about your orders</p>
                      </div>
                      <input type="checkbox" defaultChecked className="w-5 h-5" />
                    </div>

                    <div className="flex items-center justify-between border-t border-border pt-4">
                      <div>
                        <p className="font-semibold">Marketing Emails</p>
                        <p className="text-sm text-muted-foreground">Get exclusive offers and promotions</p>
                      </div>
                      <input type="checkbox" className="w-5 h-5" />
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              // Orders Tab
              <div className="space-y-4">
                <h2 className="text-2xl font-bold mb-6">Order History</h2>

                {orders.length > 0 ? (
                  <div className="space-y-4">
                    {orders.map((order) => {
                      const orderDate = new Date(order.date).toLocaleDateString("en-US", {
                        year: "numeric",
                        month: "short",
                        day: "numeric",
                      })

                      return (
                        <Link key={order.id} href={`/order-confirmation/${order.id}`}>
                          <div className="bg-secondary/30 rounded-lg p-6 hover:bg-secondary/50 transition-colors cursor-pointer">
                            <div className="grid md:grid-cols-4 gap-4 items-center">
                              <div>
                                <p className="text-sm text-muted-foreground">Order Number</p>
                                <p className="font-bold">{order.id}</p>
                              </div>

                              <div>
                                <p className="text-sm text-muted-foreground">Date</p>
                                <p className="font-semibold">{orderDate}</p>
                              </div>

                              <div>
                                <p className="text-sm text-muted-foreground">Status</p>
                                <div className="flex items-center gap-2 mt-1">
                                  <span
                                    className={`px-3 py-1 rounded-full text-xs font-semibold ${
                                      order.status === "delivered"
                                        ? "bg-green-100 text-green-800"
                                        : order.status === "shipped"
                                          ? "bg-blue-100 text-blue-800"
                                          : "bg-yellow-100 text-yellow-800"
                                    }`}
                                  >
                                    {order.status.charAt(0).toUpperCase() + order.status.slice(1)}
                                  </span>
                                </div>
                              </div>

                              <div className="text-right">
                                <p className="text-sm text-muted-foreground">Total</p>
                                <p className="text-lg font-bold text-primary">${order.total.toFixed(2)}</p>
                              </div>
                            </div>

                            {/* Order Items Preview */}
                            <div className="mt-4 pt-4 border-t border-border">
                              <p className="text-sm text-muted-foreground mb-2">
                                {order.items.length} item{order.items.length !== 1 ? "s" : ""}
                              </p>
                              <div className="flex gap-2 flex-wrap">
                                {order.items.slice(0, 3).map((item) => (
                                  <img
                                    key={item.id}
                                    src={item.image || "/placeholder.svg"}
                                    alt={item.name}
                                    className="w-12 h-12 object-cover rounded"
                                  />
                                ))}
                                {order.items.length > 3 && (
                                  <div className="w-12 h-12 rounded bg-secondary flex items-center justify-center text-xs font-semibold">
                                    +{order.items.length - 3}
                                  </div>
                                )}
                              </div>
                            </div>
                          </div>
                        </Link>
                      )
                    })}
                  </div>
                ) : (
                  <div className="text-center py-12 bg-secondary/30 rounded-lg">
                    <Package className="w-12 h-12 text-muted-foreground mx-auto mb-4 opacity-50" />
                    <p className="text-muted-foreground text-lg">No orders yet</p>
                    <p className="text-sm text-muted-foreground mb-6">Start shopping to place your first order</p>
                    <Link href="/products">
                      <Button>Start Shopping</Button>
                    </Link>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
