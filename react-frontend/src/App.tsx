import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/LoginPage';
import OAuthLoginPage from '@/pages/OAuthLoginPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* SSO Login - User đăng nhập vào hệ thống */}
        <Route path="/login" element={<LoginPage />} />

        {/* OAuth Authorization - Client redirect */}
        <Route
          path="/oauth/authorize"
          element={
            <OAuthLoginPage
              ssoName="SSO Portal"
            />
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
