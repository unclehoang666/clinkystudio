import { Outlet, Navigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const MENU = [
  { path: '/admin/orders', label: 'Đơn hàng' },
  { path: '/admin/products', label: 'Sản phẩm' },
  { path: '/admin/categories', label: 'Danh mục' },
  { path: '/admin/brands', label: 'Thương hiệu' },
];

export default function AdminLayout() {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />;

  return (
    <div style={{ display: 'flex', minHeight: 'calc(100vh - 60px)' }}>
      <aside style={{ width: 220, borderRight: '1px solid #eee', padding: 20 }}>
        <h3 style={{ marginBottom: 16 }}>Quản trị</h3>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          {MENU.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              style={{
                padding: '8px 12px',
                borderRadius: 6,
                textDecoration: 'none',
                color: location.pathname === item.path ? '#fff' : '#333',
                background: location.pathname === item.path ? '#111' : 'transparent',
              }}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>
      <div style={{ flex: 1, padding: 24 }}>
        <Outlet />
      </div>
    </div>
  );
}