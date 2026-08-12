# Clicky — E-commerce Website (Quần áo, Phụ kiện & Merchandise)

Dự án cá nhân xây dựng một hệ thống thương mại điện tử full-stack hoàn chỉnh, mô phỏng một shop bán quần áo có cả kênh bán online lẫn bán tại quầy (POS), kèm hệ thống quản trị nội bộ cho admin/nhân viên.

## 🧩 Tính năng chính

### Khách hàng
- Đăng ký / đăng nhập (JWT)
- Xem danh sách & chi tiết sản phẩm, lọc theo danh mục, tìm kiếm
- Chọn biến thể sản phẩm theo thuộc tính (màu sắc, kích thước...) — mô hình thuộc tính động (EAV), dễ mở rộng sang nhiều loại sản phẩm khác nhau
- Giỏ hàng, checkout, áp mã giảm giá
- Theo dõi đơn hàng, xem lịch sử
- Đánh giá sản phẩm sau khi mua
- Danh sách sản phẩm yêu thích (wishlist)
- Quản lý thông tin cá nhân, đổi mật khẩu

### Quản trị (Admin / Staff)
- Dashboard tổng quan: doanh thu, đơn chờ xử lý, sản phẩm sắp hết hàng
- Quản lý Category, Brand, Attribute, Product (kèm nhiều biến thể)
- Quản lý đơn hàng, cập nhật trạng thái (Pending → Confirmed → Shipping → Completed)
- Quản lý kho: nhà cung cấp, phiếu nhập kho (xác nhận phiếu mới thực sự cộng tồn kho)
- Quản lý nhân viên (tài khoản ADMIN/STAFF riêng biệt, phân quyền theo vai trò)
- Quản lý khách hàng, xem lịch sử mua hàng
- Bán hàng tại quầy (POS) — tạo đơn trực tiếp, tự động hoàn tất

## 🛠️ Tech Stack

**Backend**
- Java 17, Spring Boot 4
- Spring Security + JWT (xác thực, phân quyền theo vai trò ADMIN / STAFF / CUSTOMER)
- Spring Data JPA (Hibernate)
- SQL Server

**Frontend**
- React 18 + TypeScript
- React Router (routing lồng nhau, bảo vệ route theo vai trò)
- Axios (kèm interceptor tự động đính kèm JWT token)

## 📐 Điểm nhấn kỹ thuật

- **Mô hình thuộc tính sản phẩm động (EAV)**: thay vì cột `color`/`size` cố định, hệ thống dùng `attributes` + `attribute_values` + `product_variant_attributes`, cho phép mở rộng sang nhiều loại sản phẩm (phụ kiện, merchandise...) mà không cần sửa schema.
- **Quản lý tồn kho 2 giai đoạn**: khi khách đặt hàng, số lượng được tạm giữ (`reserved_quantity`); chỉ khi đơn hàng hoàn tất mới thực sự trừ vào `quantity` — tránh tình trạng bán vượt tồn kho khi nhiều người đặt cùng lúc.
- **Kiến trúc Controller → Service → Repository** rõ ràng, tách biệt HTTP layer khỏi business logic.
- **Snapshot dữ liệu tại thời điểm giao dịch**: giá sản phẩm trong đơn hàng, địa chỉ giao hàng được lưu snapshot ngay tại thời điểm đặt hàng, không phụ thuộc vào dữ liệu gốc thay đổi sau này.

## 🚀 Cài đặt & chạy thử

### Yêu cầu
- JDK 17+, Maven
- Node.js 18+
- SQL Server

### Backend
```bash
cd backend
# Cấu hình kết nối DB trong src/main/resources/application.properties
./mvnw spring-boot:run
```
Server chạy tại `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
App chạy tại `http://localhost:5173`

### Database
Chạy script `database/clicky_schema.sql` trong SQL Server Management Studio để khởi tạo schema, sau đó seed dữ liệu cơ bản (roles, order statuses) theo hướng dẫn trong `database/seed-data.sql`.

## 📸 Ảnh chụp màn hình

_(thêm ảnh chụp các trang chính: trang chủ, chi tiết sản phẩm, giỏ hàng, checkout, admin dashboard...)_

## 📋 Ghi chú / Hướng phát triển tiếp theo

- Thanh toán online (VNPay/Momo) thay vì chỉ COD
- Ca làm việc nhân viên (mở ca/kết ca, chấm công)
- Tối ưu Dashboard bằng API thống kê riêng thay vì tính toán ở frontend
- Chuyển giao diện sang Tailwind CSS, responsive cho mobile
