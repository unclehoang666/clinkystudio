import { useState } from 'react';
import { reviewApi } from '../api/reviewApi';

interface Props {
  orderItemId: number;
  productName: string;
  onSuccess: () => void;
}

export default function ReviewForm({ orderItemId, productName, onSuccess }: Props) {
  const [rating, setRating] = useState(5);
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await reviewApi.create({ orderItemId, rating, content: content || undefined });
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ border: '1px solid #eee', borderRadius: 8, padding: 14, marginTop: 8 }}>
      <p style={{ marginBottom: 8, fontWeight: 600 }}>Đánh giá: {productName}</p>

      <div style={{ marginBottom: 8 }}>
        {[1, 2, 3, 4, 5].map((star) => (
          <span
            key={star}
            onClick={() => setRating(star)}
            style={{ cursor: 'pointer', fontSize: 22, color: star <= rating ? '#f39c12' : '#ddd' }}
          >
            ★
          </span>
        ))}
      </div>

      <textarea
        placeholder="Chia sẻ cảm nhận của bạn về sản phẩm..."
        value={content}
        onChange={(e) => setContent(e.target.value)}
        rows={3}
        style={{ width: '100%', padding: 8, boxSizing: 'border-box', marginBottom: 8 }}
      />

      {error && <p style={{ color: 'red', marginBottom: 8 }}>{error}</p>}

      <button
        type="submit"
        disabled={submitting}
        style={{ padding: '6px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}
      >
        {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
      </button>
    </form>
  );
}