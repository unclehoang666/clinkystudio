import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import type { Order } from '../api/orderApi';

export default function OrderSuccessPage() {
  const { id } = useParams();
  const [order, setOrder] = useState<Order | null>(null);

  useEffect(() => {
    if (!id) return;
    orderApi.getById(Number(id)).then((res) => setOrder(res.data));
  }, [id]);

  return (
    <div style={{ maxWidth: 500, margin: '80px auto', padding: 24, textAlign: 'center' }}>
      <div style={{ fontSize: 48, marginBottom: 16 }}>✅</div>
      <h1 style={{ marginBottom: 12 }}>Đặt hàng thành công!</h1>
      {order && (
        <>
          <p style={{ color: '#666', marginBottom: 8 }}>
            Mã đơn hàng: <strong>{order.code}</strong>
          </p>
          <p style={{ fontSize: 20, fontWeight: 700, marginBottom: 24 }}>
            {order.totalAmount.toLocaleString('vi-VN')}₫
          </p>
        </>
      )}
      <div style={{ display: 'flex', gap: 12, justifyContent: 'center' }}>
        <Link to="/my-orders" style={{ padding: '10px 20px', border: '1px solid #111', textDecoration: 'none', color: '#111' }}>
          Xem đơn hàng
        </Link>
        <Link to="/products" style={{ padding: '10px 20px', background: '#111', color: '#fff', textDecoration: 'none' }}>
          Tiếp tục mua sắm
        </Link>
      </div>
    </div>
  );
}