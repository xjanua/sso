import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { AuthLayout } from '@/components/layout';
import LoginPage from '@/pages/LoginPage';

function AuthLayoutWrapper() {
  return (
    <AuthLayout title="SSO Portal" description="Sign in to continue">
      <Outlet />
    </AuthLayout>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AuthLayoutWrapper />}>
          <Route path="/login" element={<LoginPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
