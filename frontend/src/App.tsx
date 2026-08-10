import { Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import { useAuth } from './contexts/AuthContext';

function HomePage() {
  const { user, logout } = useAuth();
  return (
    <div style={{ maxWidth: 600, margin: '80px auto', padding: 24 }}>
      <h1>Trang chủ Clicky</h1>
      {user ? (
        <div>
          <p>
            Xin chào, <strong>{user.username}</strong> ({user.role})
          </p>
          <button onClick={logout}>Đăng xuất</button>
        </div>
      ) : (
        <p>Bạn chưa đăng nhập.</p>
      )}
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
    </Routes>
  );
}

export default App;