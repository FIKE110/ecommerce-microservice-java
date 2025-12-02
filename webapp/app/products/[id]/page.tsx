"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useParams } from "next/navigation"
import { ShoppingCart, Search, Menu, X, ChevronLeft, ChevronRight, Heart } from "lucide-react"
import { Button } from "@/components/ui/button"
import { products } from "@/lib/products-data"
import { useCartStore } from "@/lib/cart-store"
import { getKey } from "@/lib/keystore-service"
import { saveItemInCart } from "@/lib/cart-service"

export default function ProductDetailsPage() {
  const params = useParams()
  const productId = params.id
  // const product = products.find((p) => p.id === productId)
  const { addItem } = useCartStore()

  const [quantity, setQuantity] = useState(1)
  const [currentImageIndex, setCurrentImageIndex] = useState(0)
  const [isFavorite, setIsFavorite] = useState(false)
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const [product, setProduct] = useState<any>(null)


  useEffect(() => {
      const fetchProducts = async () => {
        try {
          // setLoading(true)
          const res = await fetch(`${process.env.NEXT_PUBLIC_GATEWAY_URL}/api/v1/product/${productId}`,{
            headers: {
              'Content-Type': 'application/json',
              "Authorization": `Bearer ${getKey("access_token")}`
            },
          }) 
          if (!res.ok) throw new Error("Failed to fetch products")
          const data = await res.json()
          console.log(data.data.data.content)
          setProduct(data.data.data)
        } catch (err: any) {
          // setError(err.message)
        } finally {
          // setLoading(false)
        }
      }
  
      fetchProducts()
    }, [])

  if (!product) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Product not found</h1>
          <Link href="/products">
            <Button>Back to Products</Button>
          </Link>
        </div>
      </div>
    )
  }

  // Create image carousel with multiple views
  const productImages = [product.image, product.image, product.image, product.image]

  const handleAddToCart = () => {
    addItem(product, quantity)
    saveItemInCart(productId?.toString() as string,quantity)
    setQuantity(1)
  }

  const nextImage = () => {
    setCurrentImageIndex((prev) => (prev + 1) % productImages.length)
  }

  const prevImage = () => {
    setCurrentImageIndex((prev) => (prev - 1 + productImages.length) % productImages.length)
  }

  // Related products
  const relatedProducts = products.filter((p) => p.category === product.category && p.id !== product.id).slice(0, 4)

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
        {/* Breadcrumb */}
        <div className="flex items-center gap-2 mb-8 text-sm text-muted-foreground">
          <Link href="/products" className="hover:text-foreground transition-colors">
            Products
          </Link>
          <span>/</span>
          <Link href={`/products?category=${product.category}`} className="hover:text-foreground transition-colors">
            {product.category}
          </Link>
          <span>/</span>
          <span className="text-foreground">{product.name}</span>
        </div>

        <div className="grid md:grid-cols-2 gap-12 mb-16">
          {/* Image Carousel */}
          <div className="space-y-4">
            <div className="relative bg-muted rounded-xl overflow-hidden h-96 md:h-full min-h-96">
              <img
                src={productImages[currentImageIndex] || "/placeholder.svg"}
                alt={product.name}
                className="w-full h-full object-cover"
              />

              {/* Carousel Controls */}
              <button
                onClick={prevImage}
                className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-background/80 hover:bg-background p-2 rounded-full transition-colors"
              >
                <ChevronLeft className="w-6 h-6" />
              </button>
              <button
                onClick={nextImage}
                className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-background/80 hover:bg-background p-2 rounded-full transition-colors"
              >
                <ChevronRight className="w-6 h-6" />
              </button>

              {/* Image Indicators */}
              <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-2">
                {productImages.map((_, index) => (
                  <button
                    key={index}
                    onClick={() => setCurrentImageIndex(index)}
                    className={`w-2 h-2 rounded-full transition-colors ${
                      index === currentImageIndex ? "bg-primary" : "bg-background/50"
                    }`}
                  />
                ))}
              </div>
            </div>

            {/* Thumbnail Gallery */}
            <div className="grid grid-cols-4 gap-2">
              {productImages.map((image, index) => (
                <button
                  key={index}
                  onClick={() => setCurrentImageIndex(index)}
                  className={`relative rounded-lg overflow-hidden h-20 border-2 transition-colors ${
                    index === currentImageIndex ? "border-primary" : "border-border"
                  }`}
                >
                  <img
                    src={image || "/placeholder.svg"}
                    alt={`View ${index + 1}`}
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          </div>

          {/* Product Info */}
          <div className="space-y-6">
            {/* Header */}
            <div>
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h1 className="text-4xl font-bold mb-2">{product.name}</h1>
                  <p className="text-muted-foreground">{product.category}</p>
                </div>
                <button
                  onClick={() => setIsFavorite(!isFavorite)}
                  className={`p-3 rounded-lg transition-colors ${
                    isFavorite ? "bg-accent text-accent-foreground" : "bg-secondary hover:bg-secondary/80"
                  }`}
                >
                  <Heart className={`w-6 h-6 ${isFavorite ? "fill-current" : ""}`} />
                </button>
              </div>

              {/* Rating */}
              <div className="flex items-center gap-4">
                <div className="flex items-center">
                  {[...Array(5)].map((_, i) => (
                    <span
                      key={i}
                      className={`text-xl ${i < Math.floor(product.rating) ? "text-accent" : "text-muted"}`}
                    >
                      ★
                    </span>
                  ))}
                </div>
                <span className="text-sm text-muted-foreground">
                  {product.rating} ({product.reviews} reviews)
                </span>
              </div>
            </div>

            {/* Price */}
            <div className="border-t border-b border-border py-6">
              <p className="text-5xl font-bold text-primary">${product.price}</p>
              <p className="text-sm text-muted-foreground mt-2">Free shipping on orders over $100</p>
            </div>

            {/* Description */}
            <div>
              <h3 className="font-semibold mb-2">Description</h3>
              <p className="text-muted-foreground leading-relaxed">{product.description}</p>
            </div>

            {/* Features */}
            <div>
              <h3 className="font-semibold mb-4">Key Features</h3>
              <ul className="space-y-2">
                <li className="flex items-center gap-3">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  <span className="text-sm">Premium quality materials</span>
                </li>
                <li className="flex items-center gap-3">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  <span className="text-sm">Carefully crafted design</span>
                </li>
                <li className="flex items-center gap-3">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  <span className="text-sm">Lifetime warranty</span>
                </li>
                <li className="flex items-center gap-3">
                  <span className="w-2 h-2 bg-primary rounded-full"></span>
                  <span className="text-sm">Free returns within 30 days</span>
                </li>
              </ul>
            </div>

            {/* Quantity and Add to Cart */}
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <span className="text-sm font-medium">Quantity:</span>
                <div className="flex items-center border border-input rounded-lg">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-4 py-2 hover:bg-secondary transition-colors"
                  >
                    −
                  </button>
                  <span className="px-6 py-2 border-l border-r border-input">{quantity}</span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="px-4 py-2 hover:bg-secondary transition-colors"
                  >
                    +
                  </button>
                </div>
              </div>

              <Button onClick={handleAddToCart} size="lg" className="w-full">
                <ShoppingCart className="w-5 h-5" />
                Add to Cart
              </Button>

              <Button variant="outline" size="lg" className="w-full bg-transparent">
                Buy Now
              </Button>
            </div>

            {/* Trust Badges */}
            <div className="grid grid-cols-3 gap-4 pt-4 border-t border-border">
              <div className="text-center">
                <p className="text-sm font-semibold">Free Shipping</p>
                <p className="text-xs text-muted-foreground">On orders over $100</p>
              </div>
              <div className="text-center">
                <p className="text-sm font-semibold">Secure Payment</p>
                <p className="text-xs text-muted-foreground">100% protected</p>
              </div>
              <div className="text-center">
                <p className="text-sm font-semibold">Easy Returns</p>
                <p className="text-xs text-muted-foreground">30-day guarantee</p>
              </div>
            </div>
          </div>
        </div>

        {/* Related Products */}
        {relatedProducts.length > 0 && (
          <div className="border-t border-border pt-16">
            <h2 className="text-3xl font-bold mb-8">Related Products</h2>
            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
              {relatedProducts.map((relatedProduct) => (
                <Link key={relatedProduct.id} href={`/products/${relatedProduct.id}`}>
                  <div className="group cursor-pointer">
                    <div className="relative overflow-hidden rounded-xl mb-4 bg-muted h-48">
                      <img
                        src={relatedProduct.image || "/placeholder.svg"}
                        alt={relatedProduct.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                    </div>
                    <h3 className="font-semibold group-hover:text-primary transition-colors line-clamp-2">
                      {relatedProduct.name}
                    </h3>
                    <p className="text-primary font-bold mt-2">${relatedProduct.price}</p>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
