import { useAuth } from '@clerk/clerk-react';
import axios from 'axios';
import { useMemo } from 'react';

const BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

export function useApi() {
  const { getToken } = useAuth();

  const api = useMemo(() => {
    const instance = axios.create({ baseURL: BASE_URL });

    instance.interceptors.request.use(async (config) => {
      // 'fieldops-api' = JWT Template name you'll create in Clerk (backend phase)
      const token = await getToken({ template: 'fieldops-api' });
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    return instance;
  }, [getToken]);

  return api;
}