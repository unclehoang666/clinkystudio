import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productApi } from '../api/productApi';
import type { Product, ProductVariant } from '../api/productApi';
import { cartApi } from '../api/cartApi';
import { useAuth } from '../contexts/AuthContext';

interface VariantAttribute {
  id: number;
  attributeValue: {
    id: number;
    value: string;
    attribute: { id: number; name: string; code: string };
  };
}

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [product, setProduct] = useState<Product | null>(null);
  const [variants, setVariants] = useState<ProductVariant[]>([]);
  const [variantAttrs, setVariantAttrs] = useState<Record<number, VariantAttribute[]>>({});
  const [selectedVariantId, setSelectedVariantId] = useState<number | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!id) return;
    setLoading(true);

    Promise.all([productApi.getById(Number(id)), productApi.getVariants(Number(id))]).then(
      async ([productRes, variantsRes]) => {
        setProduct(productRes.data);
        setVariants(variantsRes.data);

        if (variantsRes.data.length > 0) {
          setSelectedVariantId(variantsRes.data[0].id);
        }

        // Lay thuoc tinh cua tung variant de hien thi lua chon mau/size
        const attrsEntries = await Promise.all(
          variantsRes.data.map(async (v) => {
            const res = await productApi.getVariantAttributes(v.id);
            return [v.id, res.data] as [number, VariantAttribute[]];
          })
        );
        setVariantAttrs(Object.fromEntries(attrsEntries));
        setLoading(false);
      }
    );
  }, [id]);

  const selectedVariant = variants.find((v) => v.id === selectedVariantId);
  const available = selectedVariant ? selectedVariant.quantity - selectedVariant.reservedQuantity : 0;

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!selectedVariantId) return;

    try {
      await cartApi.addItem(selectedVariantId, quantity);
      setMessage('Đã thêm vào giỏ hàng!');
      setTimeout(() => setMessage(''), 2500);
    } catch (err: any) {
      setMessage(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;
  if (!product) return <div style={{ padding: 24 }}>Không tìm thấy sản phẩm</div>;

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', padding: 24, display: 'flex', gap: 40 }}>
      <div
        style={{
          flex: 1,
          aspectRatio: '1 / 1',
          background: '#f5f5f5',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#bbb',
          borderRadius: 8,
        }}
      >
        Ảnh sản phẩm
      </div>

      <div style={{ flex: 1 }}>
        {product.isGiveaway && (
          <span
            style={{
              display: 'inline-block',
              background: '#27ae60',
              color: '#fff',
              fontSize: 12,
              padding: '3px 10px',
              borderRadius: 4,
              marginBottom: 10,
            }}
          >
            Quà tặng
          </span>
        )}
        <h1 style={{ marginBottom: 8 }}>{product.name}</h1>
        {product.brand && <p style={{ color: '#888', marginBottom: 16 }}>{product.brand.name}</p>}

        {selectedVariant && (
          <p style={{ fontSize: 24, fontWeight: 700, marginBottom: 20 }}>
            {selectedVariant.price.toLocaleString('vi-VN')}₫
          </p>
        )}

        {variants.length > 0 && (
          <div style={{ marginBottom: 20 }}>
            <label style={{ display: 'block', marginBottom: 8, fontWeight: 600 }}>Chọn phiên bản:</label>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              {variants.map((v) => {
                const attrs = variantAttrs[v.id] || [];
                const label = attrs.map((a) => a.attributeValue.value).join(' / ') || v.sku;
                const isOut = v.quantity - v.reservedQuantity <= 0;
                return (
                  <button
                    key={v.id}
                    disabled={isOut}
                    onClick={() => setSelectedVariantId(v.id)}
                    style={{
                      padding: '8px 14px',
                      border: v.id === selectedVariantId ? '2px solid #111' : '1px solid #ccc',
                      background: isOut ? '#f5f5f5' : '#fff',
                      color: isOut ? '#bbb' : '#111',
                      cursor: isOut ? 'not-allowed' : 'pointer',
                      textDecoration: isOut ? 'line-through' : 'none',
                    }}
                  >
                    {label}
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {selectedVariant && (
          <p style={{ color: available > 0 ? '#666' : '#c0392b', marginBottom: 16 }}>
            {available > 0 ? `Còn ${available} sản phẩm` : 'Hết hàng'}
          </p>
        )}

        <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 20 }}>
          <label>Số lượng:</label>
          <input
            type="number"
            min={1}
            max={available}
            value={quantity}
            onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
            style={{ width: 70, padding: 6 }}
          />
        </div>

        <button
          onClick={handleAddToCart}
          disabled={!selectedVariant || available <= 0}
          style={{
            padding: '12px 24px',
            fontSize: 16,
            background: '#111',
            color: '#fff',
            border: 'none',
            cursor: available > 0 ? 'pointer' : 'not-allowed',
            opacity: available > 0 ? 1 : 0.5,
          }}
        >
          Thêm vào giỏ hàng
        </button>

        {message && <p style={{ marginTop: 12, color: message.includes('Đã thêm') ? 'green' : 'red' }}>{message}</p>}

        {product.description && (
          <div style={{ marginTop: 32 }}>
            <h3>Mô tả sản phẩm</h3>
            <p style={{ color: '#555', whiteSpace: 'pre-wrap' }}>{product.description}</p>
          </div>
        )}
      </div>
    </div>
  );
}