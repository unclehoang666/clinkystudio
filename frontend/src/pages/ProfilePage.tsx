import { useEffect, useState } from 'react';
import { userApi } from '../api/userApi';
import type { UserProfile } from '../api/userApi';

export default function ProfilePage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [form, setForm] = useState({ fullName: '', email: '', phone: '', gender: '', dateOfBirth: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [pwMessage, setPwMessage] = useState('');
  const [pwError, setPwError] = useState('');

  useEffect(() => {
    userApi.getMe().then((res) => {
      setProfile(res.data);
      setForm({
        fullName: res.data.fullName || '',
        email: res.data.email || '',
        phone: res.data.phone || '',
        gender: res.data.gender || '',
        dateOfBirth: res.data.dateOfBirth || '',
      });
      setLoading(false);
    });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');
    setSaving(true);
    try {
      const res = await userApi.updateMe(form);
      setProfile(res.data);
      setMessage('Cập nhật thông tin thành công!');
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPwError('');
    setPwMessage('');

    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setPwError('Mật khẩu xác nhận không khớp!');
      return;
    }

    try {
      await userApi.changePassword(pwForm.currentPassword, pwForm.newPassword);
      setPwMessage('Đổi mật khẩu thành công!');
      setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err: any) {
      setPwError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: 24 }}>
      <h1 style={{ marginBottom: 24 }}>Tài khoản của tôi</h1>

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24 }}>
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
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Số điện thoại</label>
            <input
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Giới tính</label>
              <select
                value={form.gender}
                onChange={(e) => setForm({ ...form, gender: e.target.value })}
                style={{ width: '100%', padding: 8 }}
              >
                <option value="">-- Chọn --</option>
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
                <option value="Khác">Khác</option>
              </select>
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Ngày sinh</label>
              <input
                type="date"
                value={form.dateOfBirth}
                onChange={(e) => setForm({ ...form, dateOfBirth: e.target.value })}
                style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
              />
            </div>
          </div>

          {message && <p style={{ color: 'green', marginBottom: 12 }}>{message}</p>}
          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

          <button type="submit" disabled={saving} style={{ padding: '8px 20px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
            {saving ? 'Đang lưu...' : 'Lưu thay đổi'}
          </button>
        </form>
      </div>

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 20 }}>
        <h3 style={{ marginBottom: 16 }}>Đổi mật khẩu</h3>
        <form onSubmit={handleChangePassword}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Mật khẩu hiện tại</label>
            <input
              type="password"
              value={pwForm.currentPassword}
              onChange={(e) => setPwForm({ ...pwForm, currentPassword: e.target.value })}
              required
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Mật khẩu mới</label>
            <input
              type="password"
              value={pwForm.newPassword}
              onChange={(e) => setPwForm({ ...pwForm, newPassword: e.target.value })}
              required
              minLength={6}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Xác nhận mật khẩu mới</label>
            <input
              type="password"
              value={pwForm.confirmPassword}
              onChange={(e) => setPwForm({ ...pwForm, confirmPassword: e.target.value })}
              required
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>

          {pwMessage && <p style={{ color: 'green', marginBottom: 12 }}>{pwMessage}</p>}
          {pwError && <p style={{ color: 'red', marginBottom: 12 }}>{pwError}</p>}

          <button type="submit" style={{ padding: '8px 20px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
            Đổi mật khẩu
          </button>
        </form>
      </div>
    </div>
  );
}