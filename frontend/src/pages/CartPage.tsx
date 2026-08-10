import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { cartApi } from '../api/cartApi';
import type { CartItem } from '../api/cartApi';

export default function CartPage() {
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const loadCart = () => {
    setLoading(true);
    cartApi
      .getCart()
      .then((res) => setItems(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleUpdateQuantity = async (variantId: number, quantity: number) => {
    if (quantity <= 0) return;
    try {
      await cartApi.updateQuantity(variantId, quantity);
      loadCart();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  const handleRemove = async (variantId: number) => {
    await cartApi.removeItem(variantId);
    loadCart();
  };

  const total = items.reduce((sum, item) => sum + item.variant.price * item.quantity, 0);

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24 }}>
      <h1 style={{ marginBottom: 24 }}>Giỏ hàng</h1>

      {items.length === 0 ? (
        <div>
          <p>Giỏ hàng của bạn đang trống.</p>
          <Link to="/products">Tiếp tục mua sắm</Link>
        </div>
      ) : (
        <>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {items.map((item) => (
              <div
                key={item.id}
                style={{
                  display: 'flex',
                  gap: 16,
                  alignItems: 'center',
                  border: '1px solid #eee',
                  borderRadius: 8,
                  padding: 12,
                }}
              >
                <div
                  style={{
                    width: 70,
                    height: 70,
                    background: '#f5f5f5',
                    borderRadius: 6,
                    flexShrink: 0,
                  }}
                />
                <div style={{ flex: 1 }}>
                  <Link
                    to={`/products/${item.variant.product.id}`}
                    style={{ fontWeight: 600, textDecoration: 'none', color: '#111' }}
                  >
                    {item.variant.product.name}
                  </Link>
                  <p style={{ fontSize: 13, color: '#888', margin: '4px 0' }}>SKU: {item.variant.sku}</p>
                  <p style={{ fontWeight: 600 }}>{item.variant.price.toLocaleString('vi-VN')}₫</p>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <button onClick={() => handleUpdateQuantity(item.variant.id, item.quantity - 1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => handleUpdateQuantity(item.variant.id, item.quantity + 1)}>+</button>
                </div>

                <button
                  onClick={() => handleRemove(item.variant.id)}
                  style={{ color: '#c0392b', border: 'none', background: 'none', cursor: 'pointer' }}
                >
                  Xóa
                </button>
              </div>
            ))}
          </div>

          <div
            style={{
              marginTop: 24,
              paddingTop: 16,
              borderTop: '1px solid #eee',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <span style={{ fontSize: 18 }}>
              Tổng cộng: <strong>{total.toLocaleString('vi-VN')}₫</strong>
            </span>
            <button
              onClick={() => navigate('/checkout')}
              style={{ padding: '12px 28px', fontSize: 16, background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}
            >
              Đặt hàng
            </button>
          </div>
        </>
      )}
    </div>
  );
}