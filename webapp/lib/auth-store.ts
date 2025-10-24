import { create } from "zustand"
import { persist } from "zustand/middleware"

export interface User {
  id: string
  email: string
  name: string
  token: string
}

interface AuthStore {
  user: User | null
  isLoading: boolean
  error: string | null
  login: (email: string, password: string) => Promise<void>
  signup: (email: string, password: string, name: string) => Promise<void>
  logout: () => void
  clearError: () => void
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      user: null,
      isLoading: false,
      error: null,

      login: async (email: string, password: string) => {
        set({ isLoading: true, error: null })
        try {
          // Simulate API call
          await new Promise((resolve) => setTimeout(resolve, 1000))

          // Mock validation
          if (!email || !password) {
            throw new Error("Email and password are required")
          }

          const user: User = {
            id: Math.random().toString(36).substr(2, 9),
            email,
            name: email.split("@")[0],
            token: "mock-jwt-token-" + Math.random().toString(36).substr(2, 9),
          }

          set({ user, isLoading: false })
        } catch (error) {
          set({
            error: error instanceof Error ? error.message : "Login failed",
            isLoading: false,
          })
          throw error
        }
      },

      signup: async (email: string, password: string, name: string) => {
        set({ isLoading: true, error: null })
        try {
          // Simulate API call
          await new Promise((resolve) => setTimeout(resolve, 1000))

          // Mock validation
          if (!email || !password || !name) {
            throw new Error("All fields are required")
          }

          if (password.length < 6) {
            throw new Error("Password must be at least 6 characters")
          }

          const user: User = {
            id: Math.random().toString(36).substr(2, 9),
            email,
            name,
            token: "mock-jwt-token-" + Math.random().toString(36).substr(2, 9),
          }

          set({ user, isLoading: false })
        } catch (error) {
          set({
            error: error instanceof Error ? error.message : "Signup failed",
            isLoading: false,
          })
          throw error
        }
      },

      logout: () => {
        set({ user: null, error: null })
      },

      clearError: () => {
        set({ error: null })
      },
    }),
    {
      name: "auth-store",
    },
  ),
)
