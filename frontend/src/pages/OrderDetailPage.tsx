import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import type { Order } from '../api/orderApi';
import ReviewForm from '../components/ReviewForm';

interface OrderItem {
  id: number;
  price: number;
  quantity: number;
  variant: {
    sku: string;
    product: { id: number; name: string };
  };
}

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Chờ xác nhận', color: '#f39c12' },
  CONFIRMED: { label: 'Đã xác nhận', color: '#2980b9' },
  SHIPPING: { label: 'Đang giao hàng', color: '#8e44ad' },
  COMPLETED: { label: 'Hoàn tất', color: '#27ae60' },
  CANCELLED: { label: 'Đã hủy', color: '#c0392b' },
};

export default function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState<Order | null>(null);
  const [items, setItems] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [reviewedIds, setReviewedIds] = useState<number[]>([]);

  useEffect(() => {
    if (!id) return;
    Promise.all([orderApi.getById(Number(id)), orderApi.getItems(Number(id))]).then(
      ([orderRes, itemsRes]) => {
        setOrder(orderRes.data);
        setItems(itemsRes.data as OrderItem[]);
        setLoading(false);
      }
    );
  }, [id]);

  if (loading) return <div style={{ padding: 24 }}>Đang tải...</div>;
  if (!order) return <div style={{ padding: 24 }}>Không tìm thấy đơn hàng</div>;

  const statusInfo = order.status ? STATUS_LABELS[order.status.code] : null;

  return (
    <div style={{ maxWidth: 700, margin: '0 auto', padding: 24 }}>
      <p style={{ marginBottom: 16 }}>
        <Link to="/my-orders">← Quay lại đơn hàng của tôi</Link>
      </p>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1>Đơn hàng #{order.code}</h1>
        {statusInfo && (
          <span
            style={{
              fontSize: 13,
              padding: '4px 12px',
              borderRadius: 12,
              background: statusInfo.color + '20',
              color: statusInfo.color,
              fontWeight: 600,
            }}
          >
            {statusInfo.label}
          </span>
        )}
      </div>

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 16, marginBottom: 20 }}>
        <h3 style={{ marginBottom: 8 }}>Thông tin giao hàng</h3>
        <p>{order.receiverName} - {order.receiverPhone}</p>
        <p>{order.shippingAddress}</p>
      </div>

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 16, marginBottom: 20 }}>
        <h3 style={{ marginBottom: 12 }}>Sản phẩm</h3>
        {items.map((item) => (
          <div key={item.id} style={{ marginBottom: 14, paddingBottom: 14, borderBottom: '1px solid #f5f5f5' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <div>
                <Link to={`/products/${item.variant.product.id}`} style={{ color: '#111', textDecoration: 'none' }}>
                  {item.variant.product.name}
                </Link>
                <div style={{ fontSize: 13, color: '#888' }}>
                  SKU: {item.variant.sku} × {item.quantity}
                </div>
              </div>
              <div>{(item.price * item.quantity).toLocaleString('vi-VN')}₫</div>
            </div>

            {order.status?.code === 'COMPLETED' && !reviewedIds.includes(item.id) && (
              <ReviewForm
                orderItemId={item.id}
                productName={item.variant.product.name}
                onSuccess={() => setReviewedIds([...reviewedIds, item.id])}
              />
            )}
            {reviewedIds.includes(item.id) && (
              <p style={{ color: 'green', fontSize: 13, marginTop: 6 }}>✓ Đã gửi đánh giá, cảm ơn bạn!</p>
            )}
          </div>
        ))}
      </div>

      <div style={{ border: '1px solid #eee', borderRadius: 8, padding: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
          <span>Tạm tính</span>
          <span>{order.subtotal.toLocaleString('vi-VN')}₫</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
          <span>Phí vận chuyển</span>
          <span>{order.shippingFee.toLocaleString('vi-VN')}₫</span>
        </div>
        {order.discountAmount > 0 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6, color: '#27ae60' }}>
            <span>Giảm giá</span>
            <span>-{order.discountAmount.toLocaleString('vi-VN')}₫</span>
          </div>
        )}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            fontWeight: 700,
            fontSize: 18,
            marginTop: 10,
            paddingTop: 10,
            borderTop: '1px solid #eee',
          }}
        >
          <span>Tổng cộng</span>
          <span>{order.totalAmount.toLocaleString('vi-VN')}₫</span>
        </div>
      </div>
    </div>
  );
}