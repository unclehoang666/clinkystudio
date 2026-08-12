import { useEffect, useState } from 'react';
import { userApi } from '../../api/userApi';
import axiosClient from '../../api/axiosClient';

interface CustomerUser {
  id: number;
  username: string;
  fullName: string | null;
  email: string | null;
  phone: string | null;
  status: boolean;
  createdAt: string;
  role: { code: string; name: string };
}

interface CustomerOrder {
  id: number;
  code: string;
  totalAmount: number;
  createdAt: string;
  status: { code: string; name: string } | null;
}

export default function AdminCustomersPage() {
  const [customers, setCustomers] = useState<CustomerUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState<CustomerUser | null>(null);
  const [orders, setOrders] = useState<CustomerOrder[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(false);

  const load = (q?: string) => {
    setLoading(true);
    userApi
      .search({ q: q || undefined, page: 0, size: 100 })
      .then((res: any) => {
        // Chi hien khach hang (CUSTOMER), an nhan vien/admin khoi danh sach nay
        const onlyCustomers = res.data.content.filter((u: CustomerUser) => u.role.code === 'CUSTOMER');
        setCustomers(onlyCustomers);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    load(query);
  };

  const handleViewDetail = async (customer: CustomerUser) => {
    setSelected(customer);
    setLoadingOrders(true);
    try {
      const res = await axiosClient.get('/orders', { params: { userId: customer.id, page: 0, size: 20 } });
      setOrders(res.data.content);
    } finally {
      setLoadingOrders(false);
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div style={{ display: 'flex', gap: 24 }}>
      <div style={{ flex: selected ? 1.3 : 1 }}>
        <h1 style={{ marginBottom: 20 }}>Quản lý khách hàng</h1>

        <form onSubmit={handleSearch} style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
          <input
            placeholder="Tìm theo tên, username, email..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            style={{ flex: 1, padding: 8 }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>
            Tìm
          </button>
        </form>

        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ textAlign: 'left', borderBottom: '2px solid #eee' }}>
              <th style={{ padding: 10 }}>Họ tên</th>
              <th style={{ padding: 10 }}>Username</th>
              <th style={{ padding: 10 }}>Liên hệ</th>
              <th style={{ padding: 10 }}>Trạng thái</th>
              <th style={{ padding: 10 }}></th>
            </tr>
          </thead>
          <tbody>
            {customers.map((c) => (
              <tr key={c.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                <td style={{ padding: 10 }}>{c.fullName || '—'}</td>
                <td style={{ padding: 10 }}>{c.username}</td>
                <td style={{ padding: 10 }}>
                  <div style={{ fontSize: 13 }}>{c.email || '—'}</div>
                  <div style={{ fontSize: 13, color: '#888' }}>{c.phone || '—'}</div>
                </td>
                <td style={{ padding: 10 }}>
                  <span style={{ color: c.status ? '#27ae60' : '#c0392b' }}>
                    {c.status ? 'Hoạt động' : 'Khóa'}
                  </span>
                </td>
                <td style={{ padding: 10 }}>
                  <button onClick={() => handleViewDetail(c)} style={{ cursor: 'pointer' }}>
                    Xem đơn hàng
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {customers.length === 0 && <p style={{ marginTop: 20, color: '#888' }}>Không tìm thấy khách hàng nào.</p>}
      </div>

      {selected && (
        <div style={{ flex: 1, borderLeft: '1px solid #eee', paddingLeft: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h3>Đơn hàng của {selected.fullName || selected.username}</h3>
            <button onClick={() => setSelected(null)} style={{ cursor: 'pointer' }}>
              ✕
            </button>
          </div>

          {loadingOrders ? (
            <p>Đang tải...</p>
          ) : orders.length === 0 ? (
            <p style={{ color: '#888' }}>Khách hàng chưa có đơn hàng nào.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {orders.map((o) => (
                <div key={o.id} style={{ border: '1px solid #eee', borderRadius: 6, padding: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ fontWeight: 600 }}>{o.code}</span>
                    <span>{o.totalAmount.toLocaleString('vi-VN')}₫</span>
                  </div>
                  <div style={{ fontSize: 13, color: '#888' }}>
                    {new Date(o.createdAt).toLocaleString('vi-VN')} — {o.status?.name || '—'}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}