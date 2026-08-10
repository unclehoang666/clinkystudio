import axiosClient from '../api/axiosClient';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { productApi } from '../api/productApi';
import type { Product, ProductVariant } from '../api/productApi';
import { cartApi } from '../api/cartApi';
import type { CartItem } from '../api/cartApi';
import { orderApi } from '../api/orderApi';

export default function PosPage() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [variants, setVariants] = useState<ProductVariant[]>([]);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const [customer, setCustomer] = useState({ receiverName: '', receiverPhone: '' });

  const loadCart = () => {
    cartApi.getCart().then((res) => setCartItems(res.data));
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    const res = await productApi.search({ q: query, size: 10 });
    setResults(res.data.content);
    setLoading(false);
  };

  const handleSelectProduct = async (product: Product) => {
    setSelectedProduct(product);
    const res = await productApi.getVariants(product.id);
    setVariants(res.data);
  };

  const handleAddVariant = async (variantId: number) => {
    try {
      await cartApi.addItem(variantId, 1);
      loadCart();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Không thể thêm sản phẩm');
      setTimeout(() => setError(''), 2500);
    }
  };

  const handleUpdateQuantity = async (variantId: number, quantity: number) => {
    if (quantity <= 0) {
      await cartApi.removeItem(variantId);
    } else {
      await cartApi.updateQuantity(variantId, quantity);
    }
    loadCart();
  };

  const total = cartItems.reduce((sum, item) => sum + item.variant.price * item.quantity, 0);

  const handleCheckout = async () => {
    if (cartItems.length === 0) return;
    if (!customer.receiverName.trim() || !customer.receiverPhone.trim()) {
      setError('Vui lòng nhập tên và số điện thoại khách hàng!');
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      const res = await orderApi.checkout({
        receiverName: customer.receiverName,
        receiverPhone: customer.receiverPhone,
        shippingAddress: 'Nhận tại quầy',
        deliveryMethod: 'STORE_PICKUP',
        note: 'Đơn hàng tạo tại quầy (POS)',
      });

      // Don ban tai quay: khach da thanh toan va nhan hang ngay, nen xac nhan hoan tat luon
      await axiosClient.patch(`/orders/${res.data.id}/status`, {
        statusCode: 'CONFIRMED',
        note: 'Xác nhận tại quầy',
      });
      await axiosClient.patch(`/orders/${res.data.id}/status`, {
        statusCode: 'COMPLETED',
        note: 'Khách đã thanh toán và nhận hàng tại quầy',
      });

      navigate(`/orders/${res.data.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi khi tạo đơn hàng');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto', padding: 24, display: 'flex', gap: 32 }}>
      {/* Cột trái: tìm & chọn sản phẩm */}
      <div style={{ flex: 1.3 }}>
        <h1 style={{ marginBottom: 16 }}>Bán hàng tại quầy (POS)</h1>

        <form onSubmit={handleSearch} style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
          <input
            type="text"
            placeholder="Tìm sản phẩm theo tên..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            style={{ flex: 1, padding: 10 }}
          />
          <button type="submit" style={{ padding: '10px 20px' }}>
            Tìm
          </button>
        </form>

        {loading && <p>Đang tìm...</p>}

        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 20 }}>
          {results.map((p) => (
            <div
              key={p.id}
              onClick={() => handleSelectProduct(p)}
              style={{
                padding: 10,
                border: selectedProduct?.id === p.id ? '2px solid #111' : '1px solid #eee',
                borderRadius: 6,
                cursor: 'pointer',
              }}
            >
              {p.name}
            </div>
          ))}
        </div>

        {selectedProduct && (
          <div>
            <h3 style={{ marginBottom: 10 }}>Chọn biến thể: {selectedProduct.name}</h3>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              {variants.map((v) => {
                const available = v.quantity - v.reservedQuantity;
                return (
                  <button
                    key={v.id}
                    disabled={available <= 0}
                    onClick={() => handleAddVariant(v.id)}
                    style={{
                      padding: '10px 14px',
                      border: '1px solid #ccc',
                      background: available > 0 ? '#fff' : '#f5f5f5',
                      color: available > 0 ? '#111' : '#bbb',
                      cursor: available > 0 ? 'pointer' : 'not-allowed',
                    }}
                  >
                    {v.sku} — {v.price.toLocaleString('vi-VN')}₫ (còn {available})
                  </button>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Cột phải: giỏ hàng tạm + thông tin khách + checkout */}
      <div style={{ flex: 1, borderLeft: '1px solid #eee', paddingLeft: 24 }}>
        <h2 style={{ marginBottom: 16 }}>Đơn hàng tại quầy</h2>

        {cartItems.length === 0 ? (
          <p style={{ color: '#888' }}>Chưa có sản phẩm nào, tìm và chọn sản phẩm bên trái.</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
            {cartItems.map((item) => (
              <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontWeight: 600 }}>{item.variant.product.name}</div>
                  <div style={{ fontSize: 12, color: '#888' }}>{item.variant.sku}</div>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <button onClick={() => handleUpdateQuantity(item.variant.id, item.quantity - 1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => handleUpdateQuantity(item.variant.id, item.quantity + 1)}>+</button>
                </div>
              </div>
            ))}
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 700, fontSize: 18, marginBottom: 20 }}>
          <span>Tổng cộng</span>
          <span>{total.toLocaleString('vi-VN')}₫</span>
        </div>

        <div style={{ marginBottom: 12 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Tên khách hàng *</label>
          <input
            value={customer.receiverName}
            onChange={(e) => setCustomer({ ...customer, receiverName: e.target.value })}
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', marginBottom: 4 }}>Số điện thoại *</label>
          <input
            value={customer.receiverPhone}
            onChange={(e) => setCustomer({ ...customer, receiverPhone: e.target.value })}
            style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}
          />
        </div>

        {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

        <button
          onClick={handleCheckout}
          disabled={submitting || cartItems.length === 0}
          style={{
            width: '100%',
            padding: 12,
            fontSize: 16,
            background: '#111',
            color: '#fff',
            border: 'none',
            cursor: 'pointer',
            opacity: cartItems.length === 0 ? 0.5 : 1,
          }}
        >
          {submitting ? 'Đang xử lý...' : 'Xác nhận thanh toán'}
        </button>
      </div>
    </div>
  );
}