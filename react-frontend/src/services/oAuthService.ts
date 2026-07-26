import api from '@/lib/api';

export const oAuthService = {
  validateAuthorizationRequest: async (clientId: string, redirectUri: string) => {
    const response = await api.get<{
      success: boolean;
      data: {
        clientId: string;
        clientName: string;
        logoUrl?: string;
      };
    }>('/oauth/authorize', {
      params: {
        client_id: clientId,
        redirect_uri: redirectUri,
      },
    });
    return response.data.data;
  },

  getAuthorizationUrl: (clientId: string, redirectUri: string) => {
    const params = new URLSearchParams({
      client_id: clientId,
      redirect_uri: redirectUri,
    });
    return `${window.location.origin}/oauth/authorize?${params.toString()}`;
  },

  login: async (data: {
    email: string;
    password: string;
    clientId: string;
    redirectUri: string;
  }) => {
    const response = await api.post<{
      success: boolean;
      data: {
        status: 'AUTHORIZED' | 'CONSENT_REQUIRED';
        redirectUrl?: string;
        consentRequestCode?: string;
        client?: {
          name: string;
          logoUrl?: string;
        };
        scopes?: Array<{
          code: string;
          description?: string;
        }>;
      };
    }>('/oauth/login', data);
    return response.data.data;
  },

  submitConsent: async (consentRequestCode: string, approved: boolean) => {
    const response = await api.post<{
      success: boolean;
      data: {
        redirectUrl: string;
      };
    }>('/oauth/consent', {
      consentRequestCode,
      approved,
    });
    return response.data.data;
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
