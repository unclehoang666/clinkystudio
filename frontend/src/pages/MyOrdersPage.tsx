import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import type { Order } from '../api/orderApi';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Chờ xác nhận', color: '#f39c12' },
  CONFIRMED: { label: 'Đã xác nhận', color: '#2980b9' },
  SHIPPING: { label: 'Đang giao hàng', color: '#8e44ad' },
  COMPLETED: { label: 'Hoàn tất', color: '#27ae60' },
  CANCELLED: { label: 'Đã hủy', color: '#c0392b' },
};

export default function MyOrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    orderApi
      .getMyOrders()
      .then((res) => setOrders(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24 }}>
      <h1 style={{ marginBottom: 24 }}>Đơn hàng của tôi</h1>

      {orders.length === 0 ? (
        <div>
          <p>Bạn chưa có đơn hàng nào.</p>
          <Link to="/products">Bắt đầu mua sắm</Link>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {orders.map((order) => {
            const statusInfo = order.status ? STATUS_LABELS[order.status.code] : null;
            return (
              <Link
                key={order.id}
                to={`/orders/${order.id}`}
                style={{
                  textDecoration: 'none',
                  color: '#111',
                  border: '1px solid #eee',
                  borderRadius: 8,
                  padding: 16,
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <div>
                  <div style={{ fontWeight: 600, marginBottom: 4 }}>Đơn hàng #{order.code}</div>
                  <div style={{ fontSize: 13, color: '#888' }}>
                    {new Date(order.createdAt).toLocaleString('vi-VN')}
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontWeight: 700, marginBottom: 4 }}>
                    {order.totalAmount.toLocaleString('vi-VN')}₫
                  </div>
                  {statusInfo && (
                    <span
                      style={{
                        fontSize: 12,
                        padding: '3px 10px',
                        borderRadius: 12,
                        background: statusInfo.color + '20',
                        color: statusInfo.color,
                        fontWeight: 600,
                      }}
                    >
                      {statusInfo.label}
                    </span>
                  )}
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}