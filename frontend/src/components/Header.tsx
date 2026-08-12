import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Header() {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '12px 24px',
        borderBottom: '1px solid #e5e5e5',
        position: 'sticky',
        top: 0,
        background: '#fff',
        zIndex: 10,
      }}
    >
      <Link to="/" style={{ fontSize: 22, fontWeight: 700, textDecoration: 'none', color: '#111' }}>
        Clicky
      </Link>

      <nav style={{ display: 'flex', gap: 20, alignItems: 'center' }}>
        <Link to="/products" style={{ textDecoration: 'none', color: '#333' }}>
          Sản phẩm
        </Link>

        <Link to="/cart" style={{ textDecoration: 'none', color: '#333' }}>
          Giỏ hàng
        </Link>

        {isAuthenticated ? (
          <>
            <Link to="/my-orders" style={{ textDecoration: 'none', color: '#333' }}>
              Đơn hàng của tôi
            </Link>
            <Link to="/profile" style={{ textDecoration: 'none', color: '#333' }}>
              Tài khoản
            </Link>
            {user?.role === 'ADMIN' && (
              <>
                <Link to="/pos" style={{ textDecoration: 'none', color: '#333', fontWeight: 600 }}>
                  POS
                </Link>
                <Link to="/admin" style={{ textDecoration: 'none', color: '#c0392b', fontWeight: 600 }}>
                  Quản trị
                </Link>
              </>
            )}
            <span style={{ color: '#666' }}>Xin chào, {user?.username}</span>
            <button onClick={handleLogout} style={{ padding: '6px 12px', cursor: 'pointer' }}>
              Đăng xuất
            </button>
          </>
        ) : (
          <>
            <Link to="/login" style={{ textDecoration: 'none', color: '#333' }}>
              Đăng nhập
            </Link>
            <Link to="/register" style={{ textDecoration: 'none', color: '#333' }}>
              Đăng ký
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}