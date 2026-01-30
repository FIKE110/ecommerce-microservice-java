# Webapp

Next.js 16 frontend application providing user interface for the e-commerce platform.

## Overview

The Webapp is a modern React-based frontend built with Next.js 16, TypeScript, and Tailwind CSS. It provides a responsive user interface for browsing products, managing shopping carts, placing orders, and user authentication.

## Features

- User authentication (login, signup, logout)
- Product browsing and search
- Shopping cart management
- Order placement and tracking
- User profile management
- Responsive design (mobile-first)
- Real-time updates
- Toast notifications
- Form validation with Zod
- Optimized for performance

## Tech Stack

- **Framework**: Next.js 16.0.0
- **UI Library**: React 19.2.0
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4.1.9
- **State Management**: Zustand 5.0.8
- **HTTP Client**: Axios
- **Components**: Radix UI primitives
- **Form Handling**: React Hook Form + Zod validation
- **Routing**: React Router DOM 7.13.0
- **Animations**: Framer Motion 12.29.2
- **Build Tool**: Vite 7.3.1
- **Package Manager**: pnpm/bun

## Project Structure

```
webapp/
├── app/                    # Next.js app directory
│   ├── cart/              # Cart pages
│   ├── checkout/           # Checkout pages
│   ├── login/             # Login page
│   ├── order-confirmation/ # Order confirmation page
│   ├── products/          # Product pages
│   ├── profile/           # User profile pages
│   └── signup/            # Signup page
├── components/            # React components
│   └── ui/              # UI primitives
├── context/              # React contexts
├── hooks/                # Custom React hooks
├── lib/                  # Utility libraries
├── public/               # Static assets
├── services/             # API services
├── types/                # TypeScript types
├── utils/                # Utility functions
├── components.json       # Shadcn UI configuration
├── package.json
├── tsconfig.json
└── tailwind.config.ts
```

## Installation & Setup

### Prerequisites

- **Node.js**: 18+
- **pnpm or bun**: Package manager

### Installation

```bash
# Using pnpm
cd webapp
pnpm install

# Using bun
cd webapp
bun install

# Using npm (fallback)
cd webapp
npm install
```

### Environment Variables

Create `.env.local` file:

```bash
# API Gateway URL
VITE_API_URL=http://localhost:8081

# Optional: Feature flags
NEXT_PUBLIC_FEATURE_CART=true
NEXT_PUBLIC_FEATURE_PAYMENTS=true
```

## Configuration

### API Configuration

API client is configured in `services/api.ts`:

```typescript
import axios from 'axios';

export const apiUrl = `${import.meta.env.VITE_API_URL}/api/v1`;

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1`,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      // Redirect to login
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Routing Configuration

Routes are configured using React Router:

```typescript
// Main routing
<Routes>
  <Route path="/" element={<HomePage />} />
  <Route path="/login" element={<LoginPage />} />
  <Route path="/signup" element={<SignupPage />} />
  <Route path="/products" element={<ProductListPage />} />
  <Route path="/products/:id" element={<ProductDetailPage />} />
  <Route path="/cart" element={<CartPage />} />
  <Route path="/checkout" element={<CheckoutPage />} />
  <Route path="/profile" element={<ProfilePage />} />
</Routes>
```

## API Services

### Auth Service

```typescript
import api from './api';

export const authService = {
  signup: (data: SignupData) =>
    api.post('/auth/sign-up', data),

  signin: (data: SigninData) =>
    api.post('/auth/sign-in', data),

  refresh: (refreshToken: string) =>
    api.post('/auth/refresh', { refreshToken }),

  getProfile: (username: string) =>
    api.get(`/auth/profile?username=${username}`),
};
```

### Product Service

```typescript
export const productService = {
  getProducts: (params: ProductParams) =>
    api.get('/product', { params }),

  getProductById: (id: string) =>
    api.get(`/product/${id}`),

  getProductPrice: (id: string) =>
    api.get(`/product/${id}/price`),

  getProductName: (id: string) =>
    api.get(`/product/${id}/name`),
};
```

### Cart Service

```typescript
export const cartService = {
  getCart: () =>
    api.get('/cart'),

  addToCart: (productId: string, quantity?: number) =>
    api.post(`/cart/${productId}/add${quantity ? `/${quantity}` : ''}`),

  removeFromCart: (productId: string) =>
    api.post(`/cart/${productId}/remove`),

  deleteFromCart: (productId: string) =>
    api.post(`/cart/${productId}/delete`),

  checkout: () =>
    api.post('/cart/checkout'),
};
```

### Order Service

```typescript
export const orderService = {
  createOrder: (data: OrderData) =>
    api.post('/order', data),

  getOrders: (params: OrderParams) =>
    api.get('/order', { params }),

  getOrderById: (id: string) =>
    api.get(`/order/${id}`),

  getInvoice: (reference: string) =>
    api.get(`/order/invoice/${reference}`),
};
```

## State Management

### Zustand Store

```typescript
import { create } from 'zustand';

interface AuthState {
  user: User | null;
  token: string | null;
  login: (user: User, token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  login: (user, token) => set({ user, token }),
  logout: () => set({ user: null, token: null }),
}));
```

## Components

### UI Components

Using Radix UI primitives:

```typescript
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { Toast, ToastProvider, useToast } from '@/components/ui/toast';
```

### Example Product Card Component

```typescript
export function ProductCard({ product }: { product: Product }) {
  const { toast } = useToast();

  const handleAddToCart = () => {
    cartService.addToCart(product.id)
      .then(() => {
        toast({
          title: "Added to cart",
          description: `${product.name} has been added to your cart`,
        });
      })
      .catch((error) => {
        toast({
          variant: "destructive",
          title: "Error",
          description: error.message,
        });
      });
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{product.name}</CardTitle>
      </CardHeader>
      <CardContent>
        <p>{product.description}</p>
        <p className="font-bold">${product.price}</p>
      </CardContent>
      <CardFooter>
        <Button onClick={handleAddToCart}>Add to Cart</Button>
      </CardFooter>
    </Card>
  );
}
```

## Pages

### HomePage

```typescript
export default function HomePage() {
  const { data: products } = useQuery({
    queryKey: ['products'],
    queryFn: () => productService.getProducts({ page: 0, size: 10 }),
  });

  return (
    <div className="container">
      <h1>Welcome to E-Commerce</h1>
      <div className="grid">
        {products?.content.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}
```

### LoginPage

```typescript
export default function LoginPage() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const { login } = useAuthStore();

  const form = useForm({
    resolver: zodResolver(signinSchema),
  });

  const onSubmit = async (data: SigninData) => {
    try {
      const response = await authService.signin(data);
      const user = await authService.getProfile(data.username);
      login(user, response.data.access_token);
      navigate('/');
      toast({
        title: "Login successful",
        description: "Welcome back!",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Login failed",
        description: error.message,
      });
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <FormField name="username">
          <FormItem>
            <FormLabel>Username</FormLabel>
            <FormControl>
              <Input placeholder="Username" />
            </FormControl>
          </FormItem>
        </FormField>
        <Button type="submit">Login</Button>
      </form>
    </Form>
  );
}
```

## Running the App

### Development Mode

```bash
# Using pnpm
pnpm dev

# Using bun
bun dev

# Using npm
npm run dev
```

**URL**: http://localhost:3000

### Production Build

```bash
# Using pnpm
pnpm build
pnpm start

# Using bun
bun run build
bun run start

# Using npm
npm run build
npm run start
```

### Docker

```bash
# Build image
docker build -t webapp:latest .

# Run container
docker run -p 3000:3000 webapp:latest
```

### From Root Directory

```bash
# Using Make
make start-webapp
```

## Testing

### Run Tests

```bash
# Using pnpm
pnpm test

# Using bun
bun test

# Using npm
npm test
```

### Run Linter

```bash
# Using pnpm
pnpm lint

# Using bun
bun run lint

# Using npm
npm run lint
```

## Styling

### Tailwind CSS

Tailwind CSS is configured for utility-first styling:

```typescript
// tailwind.config.ts
export default {
  darkMode: 'class',
  content: [
    './app/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: 'hsl(var(--primary))',
        },
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};
```

### Component Styling

```typescript
<div className="flex flex-col items-center justify-center min-h-screen">
  <h1 className="text-4xl font-bold text-gray-900">
    Welcome
  </h1>
  <button className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
    Get Started
  </button>
</div>
```

## Error Handling

### Global Error Handler

```typescript
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }

    if (error.response?.status === 404) {
      window.location.href = '/404';
    }

    return Promise.reject(error);
  }
);
```

### Error Boundaries

```typescript
export function ErrorBoundary({ children }: { children: React.ReactNode }) {
  return (
    <ErrorBoundaryWrapper
      FallbackComponent={ErrorFallback}
      onReset={() => window.location.reload()}
    >
      {children}
    </ErrorBoundaryWrapper>
  );
}
```

## Performance Optimization

### Code Splitting

```typescript
import lazy from 'next/dynamic';

const ProductDetailPage = lazy(() =>
  import('./pages/ProductDetailPage')
);
```

### Image Optimization

```typescript
import Image from 'next/image';

<Image
  src={product.imageUrl}
  alt={product.name}
  width={400}
  height={300}
  loading="lazy"
/>
```

### Memoization

```typescript
const ProductList = memo(function ProductList({ products }: ProductListProps) {
  return (
    <div>
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
});
```

## Deployment

### Vercel Deployment (Recommended)

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel --prod
```

### Docker Deployment

```bash
# Build image
docker build -t webapp:latest .

# Run container
docker run -p 3000:3000 -e VITE_API_URL=https://api.example.com webapp:latest
```

### Environment Variables for Production

```bash
VITE_API_URL=https://api.example.com
NEXT_PUBLIC_GA_TRACKING_ID=UA-XXXXX-Y
```

## Troubleshooting

### Build Errors

1. Clear cache:
   ```bash
   rm -rf .next node_modules
   pnpm install
   pnpm build
   ```

2. Check TypeScript errors:
   ```bash
   pnpm type-check
   ```

### API Connection Issues

1. Verify API Gateway URL in `.env.local`
2. Check API Gateway is running at specified URL
3. Review browser console for network errors
4. Test API endpoint directly

### Styling Issues

1. Clear browser cache
2. Restart development server
3. Check Tailwind CSS configuration
4. Verify component imports

## Future Improvements

- [ ] Add product image upload
- [ ] Implement wishlists
- [ ] Add product reviews and ratings
- [ ] Implement advanced search/filters
- [ ] Add admin dashboard
- [ ] Implement analytics dashboard
- [ ] Add multi-language support (i18n)
- [ ] Add PWA support
- [ ] Implement offline mode
- [ ] Add dark mode toggle

## Dependencies

See `package.json` for full list of dependencies:

```json
{
  "dependencies": {
    "next": "16.0.0",
    "react": "19.2.0",
    "react-dom": "19.2.0",
    "axios": "^1.13.2",
    "zustand": "5.0.8",
    "@radix-ui/react-dialog": "1.1.4",
    "lucide-react": "^0.454.0",
    "tailwindcss": "^4.1.9",
    "typescript": "^5"
  },
  "devDependencies": {
    "@types/node": "^22",
    "@types/react": "^19",
    "@types/react-dom": "^19",
    "typescript": "^5",
    "eslint": "^9.39.2"
  }
}
```

## Additional Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [Radix UI Documentation](https://www.radix-ui.com/docs/primitives)
- [Zustand Documentation](https://docs.pmnd.rs/zustand)

---

For more information, see: [main project README](../README.md).
