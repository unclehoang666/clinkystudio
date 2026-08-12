import { useEffect, useState } from 'react';
import { employeeApi, positionApi } from '../../api/userApi';
import type { Employee, Position } from '../../api/userApi';

export default function AdminEmployeesPage() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [positions, setPositions] = useState<Position[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const [form, setForm] = useState({
    username: '',
    password: '',
    fullName: '',
    email: '',
    phone: '',
    positionId: '',
    role: 'STAFF',
  });

  const load = () => {
    setLoading(true);
    employeeApi
      .search({ page: 0, size: 100 })
      .then((res) => setEmployees(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    positionApi.getAll().then((res) => setPositions(res.data));
  }, []);

  const openCreateForm = () => {
    setForm({ username: '', password: '', fullName: '', email: '', phone: '', positionId: '', role: 'STAFF' });
    setShowForm(true);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await employeeApi.create({
        ...form,
        positionId: form.positionId ? Number(form.positionId) : undefined,
      });
      setShowForm(false);
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  const handleToggleStatus = async (emp: Employee) => {
    try {
      await employeeApi.update(emp.id, { status: !emp.status });
      load();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1>Quản lý nhân viên</h1>
        <button onClick={openCreateForm} style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
          + Thêm nhân viên
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24, maxWidth: 500 }}
        >
          <h3 style={{ marginBottom: 16 }}>Thêm nhân viên mới</h3>

          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Username *</label>
            <input
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Mật khẩu *</label>
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
              minLength={6}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Họ tên</label>
            <input
              value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Email</label>
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
              />
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>SĐT</label>
              <input
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
              />
            </div>
          </div>
          <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Chức vụ</label>
              <select
                value={form.positionId}
                onChange={(e) => setForm({ ...form, positionId: e.target.value })}
                style={{ width: '100%', padding: 8 }}
              >
                <option value="">-- Chọn --</option>
                {positions.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Vai trò *</label>
              <select
                value={form.role}
                onChange={(e) => setForm({ ...form, role: e.target.value })}
                style={{ width: '100%', padding: 8 }}
              >
                <option value="STAFF">Nhân viên (STAFF)</option>
                <option value="ADMIN">Quản trị (ADMIN)</option>
              </select>
            </div>
          </div>

          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

          <div style={{ display: 'flex', gap: 8 }}>
            <button type="submit" style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
              Tạo tài khoản
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
            <th style={{ padding: 10 }}>Mã NV</th>
            <th style={{ padding: 10 }}>Họ tên</th>
            <th style={{ padding: 10 }}>Username</th>
            <th style={{ padding: 10 }}>Vai trò</th>
            <th style={{ padding: 10 }}>Chức vụ</th>
            <th style={{ padding: 10 }}>Trạng thái</th>
            <th style={{ padding: 10 }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
              <td style={{ padding: 10 }}>{emp.code}</td>
              <td style={{ padding: 10 }}>{emp.user.fullName || '—'}</td>
              <td style={{ padding: 10 }}>{emp.user.username}</td>
              <td style={{ padding: 10 }}>{emp.user.role.name}</td>
              <td style={{ padding: 10 }}>{emp.position?.name || '—'}</td>
              <td style={{ padding: 10 }}>
                <span style={{ color: emp.status ? '#27ae60' : '#c0392b' }}>
                  {emp.status ? 'Đang làm việc' : 'Đã nghỉ'}
                </span>
              </td>
              <td style={{ padding: 10 }}>
                <button onClick={() => handleToggleStatus(emp)} style={{ cursor: 'pointer' }}>
                  {emp.status ? 'Cho nghỉ' : 'Kích hoạt lại'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}