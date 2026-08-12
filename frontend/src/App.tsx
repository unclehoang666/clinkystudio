import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderSuccessPage from './pages/OrderSuccessPage';
import MyOrdersPage from './pages/MyOrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';
import PosPage from './pages/PosPage';
import AdminLayout from './pages/admin/AdminLayout';
import AdminOrdersPage from './pages/admin/AdminOrdersPage';
import AdminCategoriesPage from './pages/admin/AdminCategoriesPage';
import AdminBrandsPage from './pages/admin/AdminBrandsPage';
import AdminProductsPage from './pages/admin/AdminProductsPage';
import AdminMyAccountPage from './pages/admin/AdminMyAccountPage';
import AdminEmployeesPage from './pages/admin/AdminEmployeesPage';
import AdminSuppliersPage from './pages/admin/AdminSuppliersPage';
import AdminPurchaseOrdersPage from './pages/admin/AdminPurchaseOrdersPage';
import AdminCustomersPage from './pages/admin/AdminCustomersPage';
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import ProfilePage from './pages/ProfilePage';
import { useAuth } from './contexts/AuthContext';

function HomePage() {
  const { user } = useAuth();
  return (
    <div style={{ maxWidth: 600, margin: '80px auto', padding: 24 }}>
      <h1>Trang chủ Clicky</h1>
      {user ? (
        <p>
          Xin chào, <strong>{user.username}</strong> ({user.role})
        </p>
      ) : (
        <p>Bạn chưa đăng nhập.</p>
      )}
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<ProductListPage />} />
        <Route path="/products/:id" element={<ProductDetailPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/order-success/:id" element={<OrderSuccessPage />} />
        <Route path="/my-orders" element={<MyOrdersPage />} />
        <Route path="/orders/:id" element={<OrderDetailPage />} />
        <Route path="/pos" element={<PosPage />} />
        <Route path="/profile" element={<ProfilePage />} />

        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<AdminDashboardPage />} />
          <Route path="orders" element={<AdminOrdersPage />} />
          <Route path="products" element={<AdminProductsPage />} />
          <Route path="categories" element={<AdminCategoriesPage />} />
          <Route path="brands" element={<AdminBrandsPage />} />
          <Route path="employees" element={<AdminEmployeesPage />} />
          <Route path="suppliers" element={<AdminSuppliersPage />} />
          <Route path="purchase-orders" element={<AdminPurchaseOrdersPage />} />
          <Route path="customers" element={<AdminCustomersPage />} />
          <Route path="my-account" element={<AdminMyAccountPage />} />
        </Route>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>
    </Routes>
  );
}

export default App;