import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosClient from '../../api/axiosClient';

interface OrderStatus {
  id: number;
  code: string;
  name: string;
}

interface AdminOrder {
  id: number;
  code: string;
  totalAmount: number;
  receiverName: string;
  receiverPhone: string;
  createdAt: string;
  deliveryMethod: string;
  status: OrderStatus | null;
}

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Chờ xác nhận', color: '#f39c12' },
  CONFIRMED: { label: 'Đã xác nhận', color: '#2980b9' },
  SHIPPING: { label: 'Đang giao hàng', color: '#8e44ad' },
  COMPLETED: { label: 'Hoàn tất', color: '#27ae60' },
  CANCELLED: { label: 'Đã hủy', color: '#c0392b' },
};

const NEXT_STATUS: Record<string, string[]> = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['SHIPPING', 'CANCELLED'],
  SHIPPING: ['COMPLETED', 'CANCELLED'],
  COMPLETED: [],
  CANCELLED: [],
};

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<AdminOrder[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState('');
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const loadOrders = () => {
    setLoading(true);
    axiosClient
      .get('/orders', { params: { page: 0, size: 50 } })
      .then((res) => setOrders(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const handleUpdateStatus = async (orderId: number, statusCode: string) => {
    setUpdatingId(orderId);
    try {
      await axiosClient.patch(`/orders/${orderId}/status`, { statusCode });
      loadOrders();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    } finally {
      setUpdatingId(null);
    }
  };

  const filteredOrders = filterStatus
    ? orders.filter((o) => o.status?.code === filterStatus)
    : orders;

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: 20 }}>Quản lý đơn hàng</h1>

      <div style={{ marginBottom: 20 }}>
        <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)} style={{ padding: 8 }}>
          <option value="">Tất cả trạng thái</option>
          {Object.entries(STATUS_LABELS).map(([code, info]) => (
            <option key={code} value={code}>
              {info.label}
            </option>
          ))}
        </select>
      </div>

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ textAlign: 'left', borderBottom: '2px solid #eee' }}>
            <th style={{ padding: 10 }}>Mã đơn</th>
            <th style={{ padding: 10 }}>Khách hàng</th>
            <th style={{ padding: 10 }}>Ngày tạo</th>
            <th style={{ padding: 10 }}>Hình thức</th>
            <th style={{ padding: 10 }}>Tổng tiền</th>
            <th style={{ padding: 10 }}>Trạng thái</th>
            <th style={{ padding: 10 }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {filteredOrders.map((order) => {
            const statusInfo = order.status ? STATUS_LABELS[order.status.code] : null;
            const nextOptions = order.status ? NEXT_STATUS[order.status.code] || [] : [];
            return (
              <tr key={order.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                <td style={{ padding: 10 }}>
                  <Link to={`/orders/${order.id}`}>{order.code}</Link>
                </td>
                <td style={{ padding: 10 }}>
                  {order.receiverName}
                  <div style={{ fontSize: 12, color: '#888' }}>{order.receiverPhone}</div>
                </td>
                <td style={{ padding: 10 }}>{new Date(order.createdAt).toLocaleString('vi-VN')}</td>
                <td style={{ padding: 10 }}>
                  {order.deliveryMethod === 'STORE_PICKUP' ? 'Tại quầy' : 'Giao hàng'}
                </td>
                <td style={{ padding: 10, fontWeight: 600 }}>{order.totalAmount.toLocaleString('vi-VN')}₫</td>
                <td style={{ padding: 10 }}>
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
                </td>
                <td style={{ padding: 10 }}>
                  {nextOptions.length > 0 ? (
                    <select
                      disabled={updatingId === order.id}
                      value=""
                      onChange={(e) => e.target.value && handleUpdateStatus(order.id, e.target.value)}
                      style={{ padding: 6 }}
                    >
                      <option value="">Chuyển trạng thái...</option>
                      {nextOptions.map((code) => (
                        <option key={code} value={code}>
                          {STATUS_LABELS[code].label}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <span style={{ color: '#bbb' }}>—</span>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {filteredOrders.length === 0 && <p style={{ marginTop: 20, color: '#888' }}>Không có đơn hàng nào.</p>}
    </div>
  );
}