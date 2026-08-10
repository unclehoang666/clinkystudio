import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productApi, categoryApi } from '../api/productApi';
import type { Product, Category } from '../api/productApi';
import ProductCard from '../components/ProductCard';

export default function ProductListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [searchInput, setSearchInput] = useState(searchParams.get('q') || '');

  const page = Number(searchParams.get('page') || 0);
  const categoryId = searchParams.get('categoryId') || '';
  const q = searchParams.get('q') || '';

  useEffect(() => {
    categoryApi.getAllActive().then((res) => setCategories(res.data));
  }, []);

  useEffect(() => {
    setLoading(true);
    productApi
      .search({
        page,
        size: 12,
        q: q || undefined,
        categoryId: categoryId ? Number(categoryId) : undefined,
      })
      .then((res) => {
        setProducts(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .finally(() => setLoading(false));
  }, [page, categoryId, q]);

  const updateParams = (updates: Record<string, string>) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value) next.set(key, value);
      else next.delete(key);
    });
    next.set('page', '0');
    setSearchParams(next);
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateParams({ q: searchInput });
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto', padding: 24 }}>
      <h1 style={{ marginBottom: 20 }}>Sản phẩm</h1>

      <div style={{ display: 'flex', gap: 16, marginBottom: 24, flexWrap: 'wrap' }}>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: 8 }}>
          <input
            type="text"
            placeholder="Tìm sản phẩm..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            style={{ padding: 8, width: 240 }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>
            Tìm
          </button>
        </form>

        <select
          value={categoryId}
          onChange={(e) => updateParams({ categoryId: e.target.value })}
          style={{ padding: 8 }}
        >
          <option value="">Tất cả danh mục</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <p>Đang tải...</p>
      ) : products.length === 0 ? (
        <p>Không tìm thấy sản phẩm nào.</p>
      ) : (
        <>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
              gap: 20,
            }}
          >
            {products.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>

          {totalPages > 1 && (
            <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginTop: 32 }}>
              {Array.from({ length: totalPages }).map((_, i) => (
                <button
                  key={i}
                  onClick={() => updateParams({ page: String(i) })}
                  style={{
                    padding: '6px 12px',
                    fontWeight: i === page ? 700 : 400,
                    background: i === page ? '#111' : '#fff',
                    color: i === page ? '#fff' : '#111',
                    border: '1px solid #ccc',
                    cursor: 'pointer',
                  }}
                >
                  {i + 1}
                </button>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}