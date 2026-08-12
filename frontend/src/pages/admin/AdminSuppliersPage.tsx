import { useEffect, useState } from 'react';
import { supplierApi } from '../../api/warehouseApi';
import type { Supplier } from '../../api/warehouseApi';

export default function AdminSuppliersPage() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', phone: '', email: '', address: '' });
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    supplierApi
      .search({ page: 0, size: 100 })
      .then((res) => setSuppliers(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await supplierApi.create(form);
      setShowForm(false);
      setForm({ name: '', phone: '', email: '', address: '' });
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1>Nhà cung cấp</h1>
        <button onClick={() => setShowForm(true)} style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
          + Thêm nhà cung cấp
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24, maxWidth: 500 }}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Tên nhà cung cấp *</label>
            <input
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
            <input
              placeholder="Số điện thoại"
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              style={{ flex: 1, padding: 8 }}
            />
            <input
              placeholder="Email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              style={{ flex: 1, padding: 8 }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <input
              placeholder="Địa chỉ"
              value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}
          <div style={{ display: 'flex', gap: 8 }}>
            <button type="submit" style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
              Tạo mới
            </button>
            <button type="button" onClick={() => setShowForm(false)} style={{ padding: '8px 16px' }}>
              Hủy
            </button>
          </div>
        </form>
      )}

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ textAlign: 'left', borderBottom: '2px solid #eee' }}>
            <th style={{ padding: 10 }}>Mã</th>
            <th style={{ padding: 10 }}>Tên</th>
            <th style={{ padding: 10 }}>SĐT</th>
            <th style={{ padding: 10 }}>Email</th>
          </tr>
        </thead>
        <tbody>
          {suppliers.map((s) => (
            <tr key={s.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
              <td style={{ padding: 10 }}>{s.code}</td>
              <td style={{ padding: 10 }}>{s.name}</td>
              <td style={{ padding: 10 }}>{s.phone || '—'}</td>
              <td style={{ padding: 10 }}>{s.email || '—'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}