import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { wishlistApi } from '../api/wishlistApi';
import type { WishlistItem } from '../api/wishlistApi';

export default function WishlistPage() {
  const [items, setItems] = useState<WishlistItem[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    wishlistApi
      .getMyWishlist()
      .then((res) => setItems(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const handleRemove = async (productId: number) => {
    await wishlistApi.remove(productId);
    load();
  };

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24 }}>
      <h1 style={{ marginBottom: 24 }}>Sản phẩm yêu thích</h1>

      {items.length === 0 ? (
        <div>
          <p>Bạn chưa có sản phẩm yêu thích nào.</p>
          <Link to="/products">Khám phá sản phẩm</Link>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16 }}>
          {items.map((item) => (
            <div key={item.id} style={{ border: '1px solid #eee', borderRadius: 8, overflow: 'hidden' }}>
              <Link to={`/products/${item.product.id}`} style={{ textDecoration: 'none', color: '#111' }}>
                <div
                  style={{
                    aspectRatio: '1 / 1',
                    background: '#f5f5f5',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: '#bbb',
                  }}
                >
                  Ảnh sản phẩm
                </div>
                <div style={{ padding: 12 }}>
                  <div style={{ fontWeight: 600, marginBottom: 4 }}>{item.product.name}</div>
                  {item.product.brand && <div style={{ fontSize: 13, color: '#888' }}>{item.product.brand.name}</div>}
                </div>
              </Link>
              <button
                onClick={() => handleRemove(item.product.id)}
                style={{ width: '100%', padding: 8, border: 'none', borderTop: '1px solid #eee', background: '#fff', color: '#c0392b', cursor: 'pointer' }}
              >
                Bỏ yêu thích
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}