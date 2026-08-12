import { Outlet, Navigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const ADMIN_ONLY_MENU = [
  { path: '/admin/products', label: 'Sản phẩm' },
  { path: '/admin/categories', label: 'Danh mục' },
  { path: '/admin/brands', label: 'Thương hiệu' },
  { path: '/admin/suppliers', label: 'Nhà cung cấp' },
  { path: '/admin/purchase-orders', label: 'Phiếu nhập kho' },
  { path: '/admin/customers', label: 'Khách hàng' },
  { path: '/admin/employees', label: 'Nhân viên' },
];

const SHARED_MENU = [
  { path: '/admin', label: 'Tổng quan' },
  { path: '/admin/orders', label: 'Đơn hàng' },
  { path: '/admin/my-account', label: 'Tài khoản của tôi' },
];

export default function AdminLayout() {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role !== 'ADMIN' && user?.role !== 'STAFF') return <Navigate to="/" replace />;

  // STAFF chi thay: Tong quan, Don hang, Tai khoan cua toi. ADMIN thay day du + cac muc quan tri rieng
  const menu =
    user?.role === 'ADMIN'
      ? [SHARED_MENU[0], SHARED_MENU[1], ...ADMIN_ONLY_MENU, SHARED_MENU[2]]
      : SHARED_MENU;

  return (
    <div style={{ display: 'flex', minHeight: 'calc(100vh - 60px)' }}>
      <aside style={{ width: 220, borderRight: '1px solid #eee', padding: 20 }}>
        <h3 style={{ marginBottom: 16 }}>Quản trị</h3>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          {menu.map((item) => (
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