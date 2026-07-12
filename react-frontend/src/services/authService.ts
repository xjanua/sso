export const authService = {
  login: async (email: string, password: string) => {
    const { default: api } = await import('@/lib/api');
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },

  register: async (data: { email: string; password: string; name: string }) => {
    const { default: api } = await import('@/lib/api');
    const response = await api.post('/auth/register', data);
    return response.data;
  },

  logout: async () => {
    const { default: api } = await import('@/lib/api');
    await api.post('/auth/logout');
    localStorage.removeItem('token');
  },

  getCurrentUser: async () => {
    const { default: api } = await import('@/lib/api');
    const response = await api.get('/auth/me');
    return response.data;
  },
};

export default authService;
