import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { oAuthService } from '@/services/oAuthService';

interface OAuthLoginPageProps {
  ssoName?: string;
}

interface ConsentState {
  requestCode: string;
  scopes: Array<{
    code: string;
    description?: string;
  }>;
}

export default function OAuthLoginPage({
  ssoName = 'SSO Portal',
}: OAuthLoginPageProps) {
  const [searchParams] = useSearchParams();
  const [clientName, setClientName] = useState('');
  const [clientLogo, setClientLogo] = useState<string>();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [consent, setConsent] = useState<ConsentState>();
  const [isValidating, setIsValidating] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const clientId = searchParams.get('client_id');
    const redirectUri = searchParams.get('redirect_uri');

    if (!clientId || !redirectUri) {
      setError('Thiếu client_id hoặc redirect_uri');
      setIsValidating(false);
      return;
    }

    oAuthService.validateAuthorizationRequest(clientId, redirectUri)
      .then((client) => {
        setClientName(client.clientName);
        setClientLogo(client.logoUrl);
      })
      .catch(() => {
        setError('Yêu cầu đăng nhập SSO không hợp lệ');
      })
      .finally(() => {
        setIsValidating(false);
      });
  }, [searchParams]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const clientId = searchParams.get('client_id');
    const redirectUri = searchParams.get('redirect_uri');

    if (!clientId || !redirectUri) {
      setError('Thiếu client_id hoặc redirect_uri');
      return;
    }

    setError('');
    setIsLoading(true);

    try {
      const response = await oAuthService.login({
        email,
        password,
        clientId,
        redirectUri,
      });

      if (response.status === 'AUTHORIZED' && response.redirectUrl) {
        window.location.assign(response.redirectUrl);
        return;
      }

      if (response.status === 'CONSENT_REQUIRED' && response.consentRequestCode) {
        setConsent({
          requestCode: response.consentRequestCode,
          scopes: response.scopes ?? [],
        });
        return;
      }

      setError('Phản hồi đăng nhập SSO không hợp lệ');
    } catch {
      setError('Email hoặc mật khẩu không chính xác');
    } finally {
      setIsLoading(false);
    }
  };

  const handleConsent = async (approved: boolean) => {
    if (!consent) return;

    setError('');
    setIsLoading(true);

    try {
      const response = await oAuthService.submitConsent(consent.requestCode, approved);
      window.location.assign(response.redirectUrl);
    } catch {
      setError('Không thể xử lý yêu cầu cấp quyền');
    } finally {
      setIsLoading(false);
    }
  };

  if (isValidating) {
    return (
      <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50">
        <p className="text-sm text-gray-500">Đang kiểm tra ứng dụng...</p>
      </div>
    );
  }

  if (error && !clientName) {
    return (
      <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 px-4">
        <Alert variant="destructive" className="max-w-md">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 px-4">
      <div className="w-full max-w-[400px]">
        {/* Client Logo */}
        <div className="flex justify-center mb-6">
          {clientLogo ? (
            <img 
              src={clientLogo} 
              alt={`${clientName} logo`} 
              className="h-14 w-auto object-contain"
            />
          ) : (
            <div className="h-14 w-14 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-md">
              <span className="text-2xl font-bold text-white">
                {clientName.charAt(0).toUpperCase()}
              </span>
            </div>
          )}
        </div>

        {/* Sign in message */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-semibold text-gray-900 tracking-tight">
            {consent ? `${clientName} muốn truy cập tài khoản` : `Sign in to ${clientName}`}
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            {consent ? 'Xem và xác nhận các quyền bên dưới' : `to continue to ${ssoName}`}
          </p>
        </div>

        {/* Form Card */}
        <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-2xl p-8 shadow-sm">
          {error && (
            <Alert variant="destructive" className="mb-6">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {consent ? (
            <div className="space-y-5">
              <div className="space-y-3">
                {consent.scopes.length > 0 ? consent.scopes.map((scope) => (
                  <div key={scope.code} className="rounded-xl border border-gray-200 bg-white p-4">
                    <p className="text-sm font-semibold text-gray-900">{scope.code}</p>
                    <p className="mt-1 text-sm text-gray-500">
                      {scope.description || `Cho phép truy cập phạm vi ${scope.code}`}
                    </p>
                  </div>
                )) : (
                  <p className="text-sm text-gray-500">Ứng dụng không yêu cầu thêm thông tin.</p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <Button
                  type="button"
                  variant="outline"
                  disabled={isLoading}
                  onClick={() => handleConsent(false)}
                  className="h-12 rounded-xl"
                >
                  Hủy
                </Button>
                <Button
                  type="button"
                  disabled={isLoading}
                  onClick={() => handleConsent(true)}
                  className="h-12 rounded-xl bg-blue-600 text-white hover:bg-blue-700"
                >
                  {isLoading ? 'Đang xử lý...' : 'Đồng ý'}
                </Button>
              </div>
            </div>
          ) : (
          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="space-y-2">
              <label 
                htmlFor="email" 
                className="text-sm font-medium text-gray-700"
              >
                Email
              </label>
              <Input
                id="email"
                type="email"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="h-12 border-gray-300 bg-white text-gray-900 placeholder:text-gray-400 focus:border-blue-500 focus:ring-blue-500 rounded-xl"
              />
            </div>

            <div className="space-y-2">
              <label 
                htmlFor="password" 
                className="text-sm font-medium text-gray-700"
              >
                Password
              </label>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="h-12 border-gray-300 bg-white text-gray-900 placeholder:text-gray-400 focus:border-blue-500 focus:ring-blue-500 rounded-xl"
              />
            </div>

            <div className="flex items-center justify-end">
              <button 
                type="button"
                className="text-sm text-blue-600 hover:text-blue-700 font-medium transition-colors"
              >
                Forgot password?
              </button>
            </div>

            <Button 
              type="submit" 
              disabled={isLoading}
              className="w-full h-12 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-xl transition-all duration-200 shadow-sm hover:shadow-md"
            >
              {isLoading ? (
                <span className="flex items-center gap-2">
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Signing in...
                </span>
              ) : 'Sign in'}
            </Button>
          </form>
          )}
        </div>

        {/* Terms */}
        <p className="text-center text-xs text-gray-400 mt-6">
          By signing in, you agree to our{' '}
          <button className="text-gray-500 hover:text-gray-700 transition-colors">Terms of Service</button>
          {' '}and{' '}
          <button className="text-gray-500 hover:text-gray-700 transition-colors">Privacy Policy</button>
        </p>
      </div>
    </div>
  );
}
