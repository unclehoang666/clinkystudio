import { useEffect, useState } from 'react';
import { purchaseOrderApi, supplierApi } from '../../api/warehouseApi';
import type { PurchaseOrder, Supplier } from '../../api/warehouseApi';
import { productApi } from '../../api/productApi';
import type { Product, ProductVariant } from '../../api/productApi';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Chờ xác nhận', color: '#f39c12' },
  CONFIRMED: { label: 'Đã nhập kho', color: '#27ae60' },
  CANCELLED: { label: 'Đã hủy', color: '#c0392b' },
};

interface ItemRow {
  variantId: number;
  label: string;
  quantity: string;
  importPrice: string;
}

export default function AdminPurchaseOrdersPage() {
  const [orders, setOrders] = useState<PurchaseOrder[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');
  const [processingId, setProcessingId] = useState<number | null>(null);

  const [supplierId, setSupplierId] = useState('');
  const [note, setNote] = useState('');
  const [items, setItems] = useState<ItemRow[]>([]);

  const [productQuery, setProductQuery] = useState('');
  const [productResults, setProductResults] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [variantOptions, setVariantOptions] = useState<ProductVariant[]>([]);

  const load = () => {
    setLoading(true);
    purchaseOrderApi
      .search({ page: 0, size: 50 })
      .then((res) => setOrders(res.data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    supplierApi.search({ page: 0, size: 100 }).then((res) => setSuppliers(res.data.content));
  }, []);

  const openCreateForm = () => {
    setSupplierId('');
    setNote('');
    setItems([]);
    setShowForm(true);
    setError('');
  };

  const handleSearchProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!productQuery.trim()) return;
    const res = await productApi.search({ q: productQuery, size: 10 });
    setProductResults(res.data.content);
  };

  const handleSelectProduct = async (p: Product) => {
    setSelectedProduct(p);
    const res = await productApi.getVariants(p.id);
    setVariantOptions(res.data);
  };

  const addItemRow = (variant: ProductVariant) => {
    if (items.some((i) => i.variantId === variant.id)) return;
    setItems([
      ...items,
      { variantId: variant.id, label: `${selectedProduct?.name} - ${variant.sku}`, quantity: '', importPrice: '' },
    ]);
  };

  const updateItem = (variantId: number, field: 'quantity' | 'importPrice', value: string) => {
    setItems(items.map((i) => (i.variantId === variantId ? { ...i, [field]: value } : i)));
  };

  const removeItem = (variantId: number) => {
    setItems(items.filter((i) => i.variantId !== variantId));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (!supplierId) {
      setError('Vui lòng chọn nhà cung cấp!');
      return;
    }
    if (items.length === 0 || items.some((i) => !i.quantity || !i.importPrice)) {
      setError('Vui lòng thêm sản phẩm và nhập đủ số lượng, giá nhập!');
      return;
    }

    try {
      await purchaseOrderApi.create({
        supplierId: Number(supplierId),
        note: note || undefined,
        items: items.map((i) => ({
          variantId: i.variantId,
          quantity: Number(i.quantity),
          importPrice: Number(i.importPrice),
        })),
      });
      setShowForm(false);
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    }
  };

  const handleConfirm = async (id: number) => {
    setProcessingId(id);
    try {
      await purchaseOrderApi.confirm(id);
      load();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    } finally {
      setProcessingId(null);
    }
  };

  const handleCancel = async (id: number) => {
    if (!confirm('Xác nhận hủy phiếu nhập này?')) return;
    setProcessingId(id);
    try {
      await purchaseOrderApi.cancel(id);
      load();
    } catch (err: any) {
      alert(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra');
    } finally {
      setProcessingId(null);
    }
  };

  if (loading) return <div>Đang tải...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1>Phiếu nhập kho</h1>
        <button onClick={openCreateForm} style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
          + Tạo phiếu nhập
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={{ border: '1px solid #eee', borderRadius: 8, padding: 20, marginBottom: 24 }}>
          <h3 style={{ marginBottom: 16 }}>Tạo phiếu nhập kho</h3>

          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Nhà cung cấp *</label>
            <select value={supplierId} onChange={(e) => setSupplierId(e.target.value)} style={{ width: '100%', padding: 8 }}>
              <option value="">-- Chọn nhà cung cấp --</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                </option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Ghi chú</label>
            <input value={note} onChange={(e) => setNote(e.target.value)} style={{ width: '100%', padding: 8, boxSizing: 'border-box' }} />
          </div>

          <h4 style={{ marginBottom: 8 }}>Tìm sản phẩm để thêm vào phiếu</h4>
          <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
            <input
              placeholder="Tìm theo tên sản phẩm..."
              value={productQuery}
              onChange={(e) => setProductQuery(e.target.value)}
              style={{ flex: 1, padding: 8 }}
            />
            <button type="button" onClick={handleSearchProduct} style={{ padding: '8px 16px' }}>
              Tìm
            </button>
          </div>

          {productResults.length > 0 && (
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
              {productResults.map((p) => (
                <button
                  key={p.id}
                  type="button"
                  onClick={() => handleSelectProduct(p)}
                  style={{
                    padding: '6px 12px',
                    border: selectedProduct?.id === p.id ? '2px solid #111' : '1px solid #ccc',
                    background: '#fff',
                    cursor: 'pointer',
                  }}
                >
                  {p.name}
                </button>
              ))}
            </div>
          )}

          {variantOptions.length > 0 && (
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 20 }}>
              {variantOptions.map((v) => (
                <button
                  key={v.id}
                  type="button"
                  onClick={() => addItemRow(v)}
                  style={{ padding: '6px 12px', border: '1px solid #ccc', background: '#f9f9f9', cursor: 'pointer' }}
                >
                  + {v.sku}
                </button>
              ))}
            </div>
          )}

          {items.length > 0 && (
            <div style={{ marginBottom: 20 }}>
              <h4 style={{ marginBottom: 8 }}>Danh sách sản phẩm nhập</h4>
              {items.map((item) => (
                <div key={item.variantId} style={{ display: 'flex', gap: 10, alignItems: 'center', marginBottom: 8 }}>
                  <span style={{ flex: 2 }}>{item.label}</span>
                  <input
                    placeholder="Số lượng"
                    type="number"
                    value={item.quantity}
                    onChange={(e) => updateItem(item.variantId, 'quantity', e.target.value)}
                    style={{ flex: 1, padding: 6 }}
                  />
                  <input
                    placeholder="Giá nhập"
                    type="number"
                    value={item.importPrice}
                    onChange={(e) => updateItem(item.variantId, 'importPrice', e.target.value)}
                    style={{ flex: 1, padding: 6 }}
                  />
                  <button type="button" onClick={() => removeItem(item.variantId)} style={{ color: '#c0392b' }}>
                    Xóa
                  </button>
                </div>
              ))}
            </div>
          )}

          {error && <p style={{ color: 'red', marginBottom: 12 }}>{error}</p>}

          <div style={{ display: 'flex', gap: 8 }}>
            <button type="submit" style={{ padding: '8px 16px', background: '#111', color: '#fff', border: 'none', cursor: 'pointer' }}>
              Tạo phiếu nhập
            </button>
            <button type="button" onClick={() => setShowForm(false)} style={{ padding: '8px 16px' }}>
              Hủy
            </button>
          </div>
        </form>
      )}

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ textAlign: 'left', borderBottom: '2px solid #eee' }}>
            <th style={{ padding: 10 }}>Mã phiếu</th>
            <th style={{ padding: 10 }}>Nhà cung cấp</th>
            <th style={{ padding: 10 }}>Ngày tạo</th>
            <th style={{ padding: 10 }}>Tổng tiền</th>
            <th style={{ padding: 10 }}>Trạng thái</th>
            <th style={{ padding: 10 }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((po) => {
            const statusInfo = STATUS_LABELS[po.status];
            return (
              <tr key={po.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                <td style={{ padding: 10 }}>{po.code}</td>
                <td style={{ padding: 10 }}>{po.supplier.name}</td>
                <td style={{ padding: 10 }}>{new Date(po.createdAt).toLocaleString('vi-VN')}</td>
                <td style={{ padding: 10, fontWeight: 600 }}>{po.totalAmount.toLocaleString('vi-VN')}₫</td>
                <td style={{ padding: 10 }}>
                  <span
                    style={{
                      fontSize: 12,
                      padding: '3px 10px',
                      borderRadius: 12,
                      background: statusInfo.color + '20',
                      color: statusInfo.color,
                      fontWeight: 600,
                    }}
                  >
                    {statusInfo.label}
                  </span>
                </td>
                <td style={{ padding: 10 }}>
                  {po.status === 'PENDING' && (
                    <div style={{ display: 'flex', gap: 8 }}>
                      <button
                        disabled={processingId === po.id}
                        onClick={() => handleConfirm(po.id)}
                        style={{ cursor: 'pointer' }}
                      >
                        Xác nhận nhập kho
                      </button>
                      <button
                        disabled={processingId === po.id}
                        onClick={() => handleCancel(po.id)}
                        style={{ cursor: 'pointer', color: '#c0392b' }}
                      >
                        Hủy
                      </button>
                    </div>
                  )}
                  {po.status !== 'PENDING' && <span style={{ color: '#bbb' }}>—</span>}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}