import { useEffect, useState } from 'react';
import { reviewApi } from '../api/reviewApi';
import type { Review } from '../api/reviewApi';

export default function ProductReviews({ productId }: { productId: number }) {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    reviewApi
      .getByProduct(productId)
      .then((res) => setReviews(res.data.content))
      .finally(() => setLoading(false));
  }, [productId]);

  const avgRating = reviews.length > 0 ? reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length : 0;

  if (loading) return <p style={{ color: '#888' }}>Đang tải đánh giá...</p>;

  return (
    <div style={{ marginTop: 40 }}>
      <h3 style={{ marginBottom: 12 }}>
        Đánh giá sản phẩm {reviews.length > 0 && `(${reviews.length})`}
      </h3>

      {reviews.length > 0 && (
        <p style={{ marginBottom: 16, fontSize: 18 }}>
          ⭐ {avgRating.toFixed(1)} / 5
        </p>
      )}

      {reviews.length === 0 ? (
        <p style={{ color: '#888' }}>Chưa có đánh giá nào cho sản phẩm này.</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {reviews.map((r) => (
            <div key={r.id} style={{ borderBottom: '1px solid #eee', paddingBottom: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <strong>{r.user?.fullName || r.user?.username || 'Khách hàng'}</strong>
                <span style={{ fontSize: 13, color: '#888' }}>
                  {new Date(r.createdAt).toLocaleDateString('vi-VN')}
                </span>
              </div>
              <div style={{ marginBottom: 6 }}>{'⭐'.repeat(r.rating)}</div>
              {r.content && <p style={{ color: '#444' }}>{r.content}</p>}

              {r.replyContent && (
                <div style={{ marginTop: 8, padding: 10, background: '#f7f7f7', borderRadius: 6 }}>
                  <div style={{ fontWeight: 600, fontSize: 13, marginBottom: 4 }}>Phản hồi từ shop:</div>
                  <p style={{ fontSize: 14, color: '#555' }}>{r.replyContent}</p>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}