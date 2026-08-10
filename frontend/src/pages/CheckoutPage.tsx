import { useEffect, useState, FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { cartApi } from '../api/cartApi';
import type { CartItem } from '../api/cartApi';
import { orderApi } from '../api/orderApi';

export default function CheckoutPage() {
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const [form, setForm] = useState({
    receiverName: '',
    receiverPhone: '',
    shippingAddress: '',
    shippingWard: '',
    shippingDistrict: '',
    shippingProvince: '',
    note: '',
    couponCode: '',
  });

  useEffect(() => {
    cartApi
      .getCart()
      .then((res) => {
        setItems(res.data);
        if (res.data.length === 0) navigate('/cart');
      })
      .finally(() => setLoading(false));
  }, [navigate]);

  const total = items.reduce((sum, item) => sum + item.variant.price * item.quantity, 0);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const payload = { ...form, couponCode: form.couponCode || undefined };
      const res = await orderApi.checkout(payload);
      navigate(`/order-success/${res.data.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Đặt hàng thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24, display: 'flex', gap: 40 }}>
      <form onSubmit={handleSubmit} style={{ flex: 1.3 }}>
        <h1 style={{ marginBottom: 24 }}>Thông tin giao hàng</h1>

        <div style={{ marginBottom: 14 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Họ tên người nhận *</label>
          <input
            name="receiverName"
            value={form.receiverName}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: 14 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Số điện thoại *</label>
          <input
            name="receiverPhone"
            value={form.receiverPhone}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: 14 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Địa chỉ chi tiết *</label>
          <input
            name="shippingAddress"
            value={form.shippingAddress}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ display: 'flex', gap: 10, marginBottom: 14 }}>
          <input
            name="shippingWard"
            placeholder="Phường/Xã"
            value={form.shippingWard}
            onChange={handleChange}
            style={{ flex: 1, padding: 8, boxSizing: 'border-box' }}
          />
          <input
            name="shippingDistrict"
            placeholder="Quận/Huyện"
            value={form.shippingDistrict}
            onChange={handleChange}
            style={{ flex: 1, padding: 8, boxSizing: 'border-box' }}
          />
          <input
            name="shippingProvince"
            placeholder="Tỉnh/Thành phố"
            value={form.shippingProvince}
            onChange={handleChange}
            style={{ flex: 1, padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: 14 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Mã giảm giá (nếu có)</label>
          <input
            name="couponCode"
            value={form.couponCode}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: 14 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Ghi chú</label>
          <textarea
            name="note"
            value={form.note}
            onChange={handleChange}
            rows={3}
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        {error && <p style={{ color: 'red', marginBottom: 14 }}>{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          style={{ width: '100%', padding: 12, fontSize: 16, background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}
        >
          {submitting ? 'Đang xử lý...' : 'Xác nhận đặt hàng'}
        </button>
      </form>

      <div style={{ flex: 1, borderLeft: '1px solid #eee', paddingLeft: 32 }}>
        <h2 style={{ marginBottom: 16 }}>Đơn hàng của bạn</h2>
        {items.map((item) => (
          <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 10 }}>
            <span>
              {item.variant.product.name} x{item.quantity}
            </span>
            <span>{(item.variant.price * item.quantity).toLocaleString('vi-VN')}₫</span>
          </div>
        ))}
        <div
          style={{
            marginTop: 16,
            paddingTop: 16,
            borderTop: '1px solid #eee',
            display: 'flex',
            justifyContent: 'space-between',
            fontWeight: 700,
            fontSize: 18,
          }}
        >
          <span>Tổng cộng</span>
          <span>{total.toLocaleString('vi-VN')}₫</span>
        </div>
        <p style={{ marginTop: 16 }}>
          <Link to="/cart">← Quay lại giỏ hàng</Link>
        </p>
      </div>
    </div>
  );
}