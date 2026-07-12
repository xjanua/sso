import api from '@/lib/api';

export const oAuthService = {
  getAuthorizationUrl: (clientId: string, redirectUri: string, scope?: string, state?: string) => {
    const params = new URLSearchParams({
      client_id: clientId,
      redirect_uri: redirectUri,
    });
    if (scope) params.append('scope', scope);
    if (state) params.append('state', state);
    return `${api.defaults.baseURL}/oauth/authorize?${params.toString()}`;
  },

  exchangeCode: async (data: {
    code: string;
    client_id: string;
    client_secret: string;
    redirect_uri: string;
  }) => {
    const response = await api.post<{
      user_id: string;
      email: string;
      name: string;
      avatar?: string;
      scopes: string[];
    }>('/oauth/token', data);
    return response.data;
  },
};
