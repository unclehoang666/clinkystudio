import { Link } from 'react-router-dom';
import type { Product } from '../api/productApi';

export default function ProductCard({ product }: { product: Product }) {
  return (
    <Link
      to={`/products/${product.id}`}
      style={{
        textDecoration: 'none',
        color: '#111',
        border: '1px solid #eee',
        borderRadius: 8,
        overflow: 'hidden',
        display: 'block',
      }}
    >
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
        {product.isGiveaway && (
          <span
            style={{
              display: 'inline-block',
              background: '#27ae60',
              color: '#fff',
              fontSize: 11,
              padding: '2px 8px',
              borderRadius: 4,
              marginBottom: 6,
            }}
          >
            Quà tặng
          </span>
        )}
        <div style={{ fontWeight: 600, marginBottom: 4 }}>{product.name}</div>
        {product.brand && <div style={{ fontSize: 13, color: '#888' }}>{product.brand.name}</div>}
      </div>
    </Link>
  );
}