import { useEffect, useState } from 'react';
import axiosClient from '../../api/axiosClient';

interface Category {
  id: number;
  name: string;
}
interface Brand {
  id: number;
  name: string;
}
interface Product {
  id: number;
  code: string;
  name: string;
  status: boolean;
  isGiveaway: boolean;
  category: Category | null;
  brand: Brand | null;
}
interface AttributeValue {
  id: number;
  value: string;
  attribute: { id: number; name: string };
}
interface Attribute {
  id: number;
  name: string;
}

interface VariantForm {
  sku: string;
  price: string;
  quantity: string;
  attributeValueIds: number[];
}

export default function AdminProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [attributes, setAttributes] = useState<Attribute[]>([]);
  const [attributeValues, setAttributeValues] = useState<Record<number, AttributeValue[]>>({});
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const [form, setForm] = useState({
    name: '',
    description: '',
    categoryId: '',
    brandId: '',
    isGiveaway: false,
  });
  const [variants, setVariants] = useState<VariantForm[]>([
    { sku: '', price: '', quantity: '', attributeValueIds: [] },
  ]);

  const load = () => {
    setLoading(true);
    axiosClient
      .get('/products', { params: { page: 0, size: 100 } })
      .then((res) => setProducts(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    axiosClient.get('/categories/all-active').then((res) => setCategories(res.data));
    axiosClient.get('/brands/all-active').then((res) => setBrands(res.data));
    axiosClient.get('/attributes').then(async (res) => {
      setAttributes(res.data);
      const entries = await Promise.all(
        res.data.map(async (attr: Attribute) => {
          const valRes = await axiosClient.get('/attribute-values', { params: { attributeId: attr.id } });
          return [attr.id, valRes.data] as [number, AttributeValue[]];
        })
      );
      setAttributeValues(Object.fromEntries(entries));
    });
  }, []);

  const openCreateForm = () => {
    setForm({ name: '', description: '', categoryId: '', brandId: '', isGiveaway: false });
    setVariants([{ sku: '', price: '', quantity: '', attributeValueIds: [] }]);
    setShowForm(true);
    setError('');
  };

  const addVariantRow = () => {
    setVariants([...variants, { sku: '', price: '', quantity: '', attributeValueIds: [] }]);
  };

  const removeVariantRow = (index: number) => {
    setVariants(variants.filter((_, i) => i !== index));
  };

  const updateVariant = (index: number, field: keyof VariantForm, value: any) => {
    const next = [...variants];
    next[index] = { ...next[index], [field]: value };
    setVariants(next);
  };

  const toggleVariantAttrValue = (index: number, attrValueId: number) => {
    const current = variants[index].attributeValueIds;
    const next = current.includes(attrValueId)
      ? current.filter((id) => id !== attrValueId)
      : [...current, attrValueId];
    updateVariant(index, 'attributeValueIds', next);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (variants.some((v) => !v.sku || !v.price)) {
      setError('Mỗi biến thể cần có SKU và giá!');
      return;
    }

    const payload = {
      name: form.name,
      description: form.description || null,
      categoryId: form.categoryId ? Number(form.categoryId) : null,
      brandId: form.brandId ? Number(form.brandId) : null,
      isGiveaway: form.isGiveaway,
      variants: variants.map((v) => ({
        sku: v.sku,
        price: Number(v.price),
        quantity: Number(v.quantity) || 0,
        attributes: v.attributeValueIds.map((id) => ({ attributeValueId: id })),
      })),
    };

    try {
      await axiosClient.post('/products', payload);
      setShowForm(false);
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  const handleToggleStatus = async (id: number) => {
    try {
      await axiosClient.patch(`/products/${id}/status`);
      load();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1>Quản lý sản phẩm</h1>
        <button onClick={openCreateForm} style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
          + Thêm sản phẩm
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24 }}
        >
          <h3 style={{ marginBottom: 16 }}>Thêm sản phẩm mới</h3>

          <div style={{ display: 'flex', gap: 16, marginBottom: 12 }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Tên sản phẩm *</label>
              <input
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
                style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: 16, marginBottom: 12 }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Danh mục</label>
              <select
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                style={{ width: '100%', padding: 8 }}
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: 4 }}>Thương hiệu</label>
              <select
                value={form.brandId}
                onChange={(e) => setForm({ ...form, brandId: e.target.value })}
                style={{ width: '100%', padding: 8 }}
              >
                <option value="">-- Chọn thương hiệu --</option>
                {brands.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Mô tả</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              rows={3}
              style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
            />
          </div>

          <label style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 20 }}>
            <input
              type="checkbox"
              checked={form.isGiveaway}
              onChange={(e) => setForm({ ...form, isGiveaway: e.target.checked })}
            />
            Đây là hàng tri ân / quà tặng (không bán)
          </label>

          <h4 style={{ marginBottom: 12 }}>Biến thể sản phẩm</h4>
          {variants.map((variant, index) => (
            <div key={index} style={{ border: '1px solid #eee', borderRadius: 6, padding: 14, marginBottom: 12 }}>
              <div style={{ display: 'flex', gap: 12, marginBottom: 10 }}>
                <input
                  placeholder="SKU *"
                  value={variant.sku}
                  onChange={(e) => updateVariant(index, 'sku', e.target.value)}
                  style={{ flex: 1, padding: 8 }}
                />
                <input
                  placeholder="Giá *"
                  type="number"
                  value={variant.price}
                  onChange={(e) => updateVariant(index, 'price', e.target.value)}
                  style={{ flex: 1, padding: 8 }}
                />
                <input
                  placeholder="Số lượng"
                  type="number"
                  value={variant.quantity}
                  onChange={(e) => updateVariant(index, 'quantity', e.target.value)}
                  style={{ flex: 1, padding: 8 }}
                />
                {variants.length > 1 && (
                  <button type="button" onClick={() => removeVariantRow(index)} style={{ color: '#c0392b' }}>
                    Xóa
                  </button>
                )}
              </div>

              {attributes.map((attr) => (
                <div key={attr.id} style={{ marginBottom: 6 }}>
                  <span style={{ fontSize: 13, color: '#888', marginRight: 8 }}>{attr.name}:</span>
                  {(attributeValues[attr.id] || []).map((val) => (
                    <label key={val.id} style={{ marginRight: 12, fontSize: 13 }}>
                      <input
                        type="checkbox"
                        checked={variant.attributeValueIds.includes(val.id)}
                        onChange={() => toggleVariantAttrValue(index, val.id)}
                        style={{ marginRight: 4 }}
                      />
                      {val.value}
                    </label>
                  ))}
                </div>
              ))}
            </div>
          ))}

          <button type="button" onClick={addVariantRow} style={{ marginBottom: 20, padding: '6px 12px' }}>
            + Thêm biến thể
          </button>

          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

          <div style={{ display: 'flex', gap: 8 }}>
            <button type="submit" style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
              Tạo sản phẩm
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
            <th style={{ padding: 10 }}>Danh mục</th>
            <th style={{ padding: 10 }}>Thương hiệu</th>
            <th style={{ padding: 10 }}>Trạng thái</th>
            <th style={{ padding: 10 }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
              <td style={{ padding: 10 }}>{p.code}</td>
              <td style={{ padding: 10 }}>
                {p.name}
                {p.isGiveaway && (
                  <span style={{ marginLeft: 8, fontSize: 11, background: '#27ae60', color: '#fff', padding: '2px 6px', borderRadius: 4 }}>
                    Quà tặng
                  </span>
                )}
              </td>
              <td style={{ padding: 10 }}>{p.category?.name || '—'}</td>
              <td style={{ padding: 10 }}>{p.brand?.name || '—'}</td>
              <td style={{ padding: 10 }}>
                <span style={{ color: p.status ? '#27ae60' : '#c0392b' }}>
                  {p.status ? 'Đang bán' : 'Ngừng bán'}
                </span>
              </td>
              <td style={{ padding: 10 }}>
                <button onClick={() => handleToggleStatus(p.id)} style={{ cursor: 'pointer' }}>
                  {p.status ? 'Ngừng bán' : 'Kích hoạt'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}