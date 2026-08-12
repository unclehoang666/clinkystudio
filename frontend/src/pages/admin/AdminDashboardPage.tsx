import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosClient from '../../api/axiosClient';

interface OrderSummary {
  id: number;
  code: string;
  totalAmount: number;
  createdAt: string;
  status: { code: string; name: string } | null;
}

interface ProductSummary {
  id: number;
  name: string;
}

export default function AdminDashboardPage() {
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [lowStockVariants, setLowStockVariants] = useState<
    { productName: string; sku: string; available: number }[]
  >([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      setLoading(true);

      // Lay toan bo don hang gan day (backend chua co API tong hop rieng, tinh tam o frontend)
      const ordersRes = await axiosClient.get('/orders', { params: { page: 0, size: 100 } });
      setOrders(ordersRes.data.content);

      // Lay san pham dang ban de kiem tra ton kho thap (demo don gian: lay 20 san pham dau, xem variant)
      const productsRes = await axiosClient.get('/products', { params: { page: 0, size: 20, status: true } });
      const products: ProductSummary[] = productsRes.data.content;

      const lowStock: { productName: string; sku: string; available: number }[] = [];
      await Promise.all(
        products.map(async (p) => {
          const variantsRes = await axiosClient.get(`/products/${p.id}/variants`);
          for (const v of variantsRes.data) {
            const available = v.quantity - v.reservedQuantity;
            if (available <= 5) {
              lowStock.push({ productName: p.name, sku: v.sku, available });
            }
          }
        })
      );
      setLowStockVariants(lowStock.sort((a, b) => a.available - b.available).slice(0, 10));

      setLoading(false);
    };

    load();
  }, []);

  if (loading) return <div>Đang tải...</div>;

  const today = new Date().toDateString();
  const todayOrders = orders.filter((o) => new Date(o.createdAt).toDateString() === today);
  const todayRevenue = todayOrders
    .filter((o) => o.status?.code === 'COMPLETED')
    .reduce((sum, o) => sum + o.totalAmount, 0);

  const pendingCount = orders.filter((o) => o.status?.code === 'PENDING').length;
  const completedCount = orders.filter((o) => o.status?.code === 'COMPLETED').length;
  const totalRevenue = orders
    .filter((o) => o.status?.code === 'COMPLETED')
    .reduce((sum, o) => sum + o.totalAmount, 0);

  const cardStyle: React.CSSProperties = {
    border: '1px solid #eee',
    borderRadius: 8,
    padding: 20,
    flex: 1,
  };

  return (
    <div>
      <h1 style={{ marginBottom: 24 }}>Tổng quan</h1>

      <div style={{ display: 'flex', gap: 16, marginBottom: 32 }}>
        <div style={cardStyle}>
          <p style={{ color: '#888', marginBottom: 6 }}>Doanh thu hôm nay</p>
          <p style={{ fontSize: 24, fontWeight: 700 }}>{todayRevenue.toLocaleString('vi-VN')}₫</p>
        </div>
        <div style={cardStyle}>
          <p style={{ color: '#888', marginBottom: 6 }}>Tổng doanh thu (đã hoàn tất)</p>
          <p style={{ fontSize: 24, fontWeight: 700 }}>{totalRevenue.toLocaleString('vi-VN')}₫</p>
        </div>
        <div style={cardStyle}>
          <p style={{ color: '#888', marginBottom: 6 }}>Đơn chờ xử lý</p>
          <p style={{ fontSize: 24, fontWeight: 700, color: pendingCount > 0 ? '#f39c12' : '#111' }}>
            {pendingCount}
          </p>
        </div>
        <div style={cardStyle}>
          <p style={{ color: '#888', marginBottom: 6 }}>Đơn hoàn tất</p>
          <p style={{ fontSize: 24, fontWeight: 700, color: '#27ae60' }}>{completedCount}</p>
        </div>
      </div>

      <div style={{ display: 'flex', gap: 24 }}>
        <div style={{ flex: 1 }}>
          <h3 style={{ marginBottom: 12 }}>Đơn hàng gần đây</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {orders.slice(0, 8).map((o) => (
              <Link
                key={o.id}
                to={`/orders/${o.id}`}
                style={{
                  textDecoration: 'none',
                  color: '#111',
                  display: 'flex',
                  justifyContent: 'space-between',
                  padding: 10,
                  border: '1px solid #eee',
                  borderRadius: 6,
                }}
              >
                <span>{o.code}</span>
                <span>{o.totalAmount.toLocaleString('vi-VN')}₫</span>
              </Link>
            ))}
          </div>
          {pendingCount > 0 && (
            <p style={{ marginTop: 12 }}>
              <Link to="/admin/orders">Xem {pendingCount} đơn đang chờ xử lý →</Link>
            </p>
          )}
        </div>

        <div style={{ flex: 1 }}>
          <h3 style={{ marginBottom: 12 }}>Sản phẩm sắp hết hàng</h3>
          {lowStockVariants.length === 0 ? (
            <p style={{ color: '#888' }}>Không có sản phẩm nào sắp hết hàng.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {lowStockVariants.map((v, i) => (
                <div
                  key={i}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    padding: 10,
                    border: '1px solid #eee',
                    borderRadius: 6,
                  }}
                >
                  <span>
                    {v.productName} <span style={{ color: '#888', fontSize: 13 }}>({v.sku})</span>
                  </span>
                  <span style={{ color: v.available === 0 ? '#c0392b' : '#f39c12', fontWeight: 600 }}>
                    Còn {v.available}
                  </span>
                </div>
              ))}
            </div>
          )}
          <p style={{ marginTop: 12 }}>
            <Link to="/admin/purchase-orders">Tạo phiếu nhập kho →</Link>
          </p>
        </div>
      </div>
    </div>
  );
}