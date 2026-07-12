export interface User {
  id: string;
  email: string;
  name: string;
  avatar?: string;
}

export interface ClientApp {
  id: string;
  clientId: string;
  name: string;
  description?: string;
  logoUrl?: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Scope {
  id: string;
  code: string;
  description?: string;
  isDefault: boolean;
}

export interface OAuthParams {
  client_id: string;
  redirect_uri: string;
  scope?: string;
  state?: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
}

export interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PaginatedResponse<T> {
  response: T[];
  info: PaginationInfo;
}
