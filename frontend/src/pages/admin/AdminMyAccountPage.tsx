import { useEffect, useState } from 'react';
import { userApi, employeeApi } from '../../api/userApi';
import type { UserProfile, Employee } from '../../api/userApi';

export default function AdminMyAccountPage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [form, setForm] = useState({ fullName: '', email: '', phone: '' });
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([userApi.getMe(), employeeApi.getMyProfile().catch(() => null)]).then(
      ([userRes, empRes]) => {
        setProfile(userRes.data);
        if (empRes) setEmployee(empRes.data);
        setForm({
          fullName: userRes.data.fullName || '',
          email: userRes.data.email || '',
          phone: userRes.data.phone || '',
        });
        setLoading(false);
      }
    );
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      const res = await userApi.updateMe(form);
      setProfile(res.data);
      setMessage('Cập nhật thành công!');
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 500 }}>
      <h1 style={{ marginBottom: 24 }}>Tài khoản của tôi</h1>

      {employee && (
        <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24 }}>
          <h3 style={{ marginBottom: 12 }}>Thông tin công việc</h3>
          <p>Mã nhân viên: <strong>{employee.code}</strong></p>
          <p>Chức vụ: <strong>{employee.position?.name || 'Chưa gán'}</strong></p>
          <p>Vai trò: <strong>{profile?.role.name}</strong></p>
          <p>
            Trạng thái:{' '}
            <span style={{ color: employee.status ? '#27ae60' : '#c0392b' }}>
              {employee.status ? 'Đang làm việc' : 'Đã nghỉ'}
            </span>
          </p>
        </div>
      )}

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 20 }}>
        <h3 style={{ marginBottom: 16 }}>Thông tin cá nhân</h3>
        <p style={{ color: '#888', marginBottom: 16 }}>Username: <strong>{profile?.username}</strong></p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Họ tên</label>
            <input
              value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Email</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Số điện thoại</label>
            <input
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>

          {message && <p style={{ color: 'green', marginBottom: 12 }}>{message}</p>}
          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

          <button type="submit" style={{ padding: '8px 20px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
            Lưu thay đổi
          </button>
        </form>
      </div>
    </div>
  );
}