import { useEffect, useState } from 'react';
import axiosClient from '../../api/axiosClient';

interface Category {
  id: number;
  code: string;
  name: string;
  description: string | null;
  status: boolean;
  parent: { id: number; name: string } | null;
}

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState<Category | null>(null);
  const [form, setForm] = useState({ name: '', description: '', parentId: '' });
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    axiosClient
      .get('/categories', { params: { page: 0, size: 100 } })
      .then((res) => setCategories(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const openCreateForm = () => {
    setEditing(null);
    setForm({ name: '', description: '', parentId: '' });
    setShowForm(true);
    setError('');
  };

  const openEditForm = (cat: Category) => {
    setEditing(cat);
    setForm({ name: cat.name, description: cat.description || '', parentId: cat.parent?.id.toString() || '' });
    setShowForm(true);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    const payload = {
      name: form.name,
      description: form.description || null,
      parentId: form.parentId ? Number(form.parentId) : null,
    };
    try {
      if (editing) {
        await axiosClient.put(`/categories/${editing.id}`, payload);
      } else {
        await axiosClient.post('/categories', payload);
      }
      setShowForm(false);
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  const handleToggleStatus = async (id: number) => {
    try {
      await axiosClient.patch(`/categories/${id}/status`);
      load();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1>Quản lý danh mục</h1>
        <button onClick={openCreateForm} style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
          + Thêm danh mục
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24, maxWidth: 500 }}
        >
          <h3 style={{ marginBottom: 16 }}>{editing ? 'Sửa danh mục' : 'Thêm danh mục mới'}</h3>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Tên danh mục *</label>
            <input
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Danh mục cha (nếu có)</label>
            <select
              value={form.parentId}
              onChange={(e) => setForm({ ...form, parentId: e.target.value })}
              style={{ width: '100%', padding: 8 }}
            >
              <option value="">Không có (danh mục gốc)</option>
              {categories
                .filter((c) => c.id !== editing?.id)
                .map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
            </select>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Mô tả</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              rows={3}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>
          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}
          <div style={{ display: 'flex', gap: 8 }}>
            <button type="submit" style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
              {editing ? 'Lưu thay đổi' : 'Tạo mới'}
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
            <th style={{ padding: 10 }}>Danh mục cha</th>
            <th style={{ padding: 10 }}>Trạng thái</th>
            <th style={{ padding: 10 }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {categories.map((cat) => (
            <tr key={cat.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
              <td style={{ padding: 10 }}>{cat.code}</td>
              <td style={{ padding: 10 }}>{cat.name}</td>
              <td style={{ padding: 10 }}>{cat.parent?.name || '—'}</td>
              <td style={{ padding: 10 }}>
                <span style={{ color: cat.status ? '#27ae60' : '#c0392b' }}>
                  {cat.status ? 'Hoạt động' : 'Ngừng hoạt động'}
                </span>
              </td>
              <td style={{ padding: 10, display: 'flex', gap: 10 }}>
                <button onClick={() => openEditForm(cat)} style={{ cursor: 'pointer' }}>
                  Sửa
                </button>
                <button onClick={() => handleToggleStatus(cat.id)} style={{ cursor: 'pointer' }}>
                  {cat.status ? 'Ngừng' : 'Kích hoạt'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}