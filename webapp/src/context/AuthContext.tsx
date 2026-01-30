import React, { createContext, useContext, useState, useEffect } from 'react';
import type { User } from '../types';
import api from '../services/api'; // Import api

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
  isLoading: boolean;
  profile: any | null; // Add profile to context type
  profileFetched: boolean; // To indicate if profile fetch attempt was made
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState(true);
  const [profile, setProfile] = useState<any | null>(null); // State for profile
  const [profileFetched, setProfileFetched] = useState(false); // State to track if profile was fetched

  useEffect(() => {
    const fetchUserProfile = async () => {
      if (token) {
        try {
          const response = await api.get('/customer');
          setProfile(response.data.data.data); // Assuming nested data structure
          setProfileFetched(true);
        } catch (err: any) {
          console.error("Error fetching profile", err);
          if (err.response?.status === 404) {
            setProfile(null); // Profile does not exist
            setProfileFetched(true);
          }
          else if(err.response?.status ===503 || 401){
            setProfile(null);
            setProfileFetched(true);
            logout()
          }
          else {
            // Handle other errors
            setProfile(null);
            setProfileFetched(true);
          }
        } finally {
          setIsLoading(false);
        }
      } else {
        setProfile(null);
        setProfileFetched(false);
        setIsLoading(false);
      }
    };

    fetchUserProfile();
  }, [token]);

  const login = (newToken: string) => {
    localStorage.setItem('token', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    setProfile(null); // Clear profile on logout
    setProfileFetched(false);
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, login, logout, isLoading, profile, profileFetched }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
