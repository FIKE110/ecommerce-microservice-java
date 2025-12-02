import { create } from "zustand"
import { persist } from "zustand/middleware"
import { saveKey } from "./keystore-service"

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
  signup: (email: string, password: string, username: string, phoneNumber: string) => Promise<void>
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
          const response = await fetch(`${process.env.NEXT_PUBLIC_GATEWAY_URL}/api/v1/auth/sign-in`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ email, password }),
          })

          if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || "Login failed")
          }

          const data = await response.json()
          const user: User = {
            id: data.id, // Assuming the API returns the user object
            email: data.email,
            name: data.name,
            token: data.token,
          }

          set({ user, isLoading: false })
          console.log(data)
          saveKey("access_token", data.data.data.accessToken)
          saveKey("refresh_token", data.data.data.refreshToken)
        } catch (error) {
          set({
            error: error instanceof Error ? error.message : "Login failed",
            isLoading: false,
          })
          throw error
        }
      },

      signup: async (email: string, password: string, username: string, phoneNumber: string) => {
        set({ isLoading: true, error: null })
        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_GATEWAY_URL}/api/v1/auth/sign-up`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ email, password, username, phoneNumber }),
          })

          if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || "Signup failed")
          }

          set({ isLoading: false })
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
