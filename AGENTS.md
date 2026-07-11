# SSO System Specification

## Overview

Xây dựng hệ thống SSO (Single Sign-On) đơn giản cho phép các ứng dụng bên thứ 3 (Client App) tích hợp đăng nhập thông qua hệ thống SSO của mình.

## Mục tiêu

- Đơn giản nhưng đủ dùng, không phức tạp như Google OAuth
- Phù hợp với sinh viên năm 4 có thể triển khai trong thời gian học tập

---

## Các đối tượng trong hệ thống

### 1. Hệ thống SSO (SSO Provider)
- Quản lý người dùng (đăng ký, đăng nhập, logout)
- Cung cấp form login cho các Client App
- Xác thực và ủy quyền

### 2. Người dùng cuối (End User)
- Đăng ký tài khoản trên hệ thống SSO
- Dùng 1 tài khoản để đăng nhập nhiều Client App khác nhau

### 3. Client App (Ứng dụng bên thứ 3)
- Các web/app muốn tích hợp SSO
- Cần đăng ký với hệ thống để có `client_id` và `client_secret`
- Tự quản lý session sau khi nhận thông tin user

---

## Flow hoạt động

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   User       │         │  Client App  │         │  SSO Server  │
│  (Browser)   │         │              │         │              │
└──────┬───────┘         └──────┬───────┘         └──────┬───────┘
       │                        │                        │
       │  1. Click "Login SSO" │                        │
       │───────────────────────>│                        │
       │                        │                        │
       │  2. Redirect với       │                        │
       │     client_id,         │                        │
       │     redirect_uri,      │                        │
       │     state, scope       │                        │
       │<──────────────────────│                        │
       │                        │                        │
       │  3. Form Login         │                        │
       │     (trên SSO server)  │                        │
       │───────────────────────>│                        │
       │                        │                        │
       │  4. User login thành công                       │
       │───────────────────────>│                        │
       │                        │                        │
       │  5. Redirect về        │                        │
       │     redirect_uri        │                        │
       │     kèm ?code=xxx      │                        │
       │<───────────────────────│                        │
       │                        │                        │
       │                        │  6. POST /oauth/token  │
       │                        │     code + client_id   │
       │                        │     + client_secret    │
       │                        │───────────────────────>│
       │                        │                        │
       │                        │  7. User info JSON    │
       │                        │<───────────────────────│
       │                        │                        │
       │  8. Redirect với       │                        │
       │     thông tin user      │                        │
       │<───────────────────────│                        │
       │                        │                        │
       │  9. Client tự tạo     │                        │
       │     session            │                        │
```

---

## Chi tiết các bước

### Bước 1-2: Redirect to SSO Authorization

**Request:**
```
GET /oauth/authorize
  ?client_id=app_abc123
  &redirect_uri=https://app.example.com/callback
  &scope=profile email
  &state=random_state_string
```

### Bước 3: User Login

- Hiển thị form login trên SSO server
- User nhập email/password
- Xác thực thông tin đăng nhập

### Bước 4-5: Authorization Code

- Sau khi login thành công, tạo authorization code
- Redirect về Client App:
```
GET {redirect_uri}
  ?code=auth_code_xyz
  &state=random_state_string
```

### Bước 6: Token Exchange (Code đổi User Info)

**Request:**
```
POST /oauth/token
Content-Type: application/json

{
  "code": "auth_code_xyz",
  "client_id": "app_abc123",
  "client_secret": "client_secret_xyz"
}
```

### Bước 7: Response - User Info

```json
{
  "user_id": "123",
  "email": "user@example.com",
  "name": "Nguyen Van A",
  "avatar": "https://sso.example.com/avatars/123.jpg",
  "scopes": ["profile", "email"]
}
```

**Lưu ý:** Không trả `access_token` hoặc `refresh_token`. Client App tự quản lý session của mình.

---

## Scopes

- Client App đăng ký scopes mà mình cần truy cập
- Scopes được Admin cấu hình trong hệ thống
- Ví dụ scopes: `profile`, `email`, `avatar`

---

## Đặc điểm quan trọng

1. **Không có PKCE** - Đơn giản hóa flow
2. **Không trả access_token/refresh_token** - Client tự tạo session riêng
3. **Code one-time use** - Mỗi code chỉ đổi được 1 lần
4. **Code có thời hạn** - Ví dụ: 5-10 phút
5. **SSO không quản lý session của Client App**
6. **Không cần quản lý permissions giữa SSO và Client App**

---

## Những thứ đã loại bỏ (so với spec ban đầu)

- ~~Quản lý permissions giữa SSO và Client App~~
- ~~Refresh token~~
- ~~Access token~~
- ~~PKCE~~
- ~~Admin dashboard~~ (sẽ làm sau)
- ~~Frontend cho trang login SSO~~ (sẽ làm sau)

---

## Những thứ sẽ làm (Ưu tiên)

1. **Models:**
   - Client App (client_id, client_secret, redirect_uri, allowed_scopes)
   - Authorization Code (code, client_id, user_id, expires_at)

2. **Authorization Endpoint** (`GET /oauth/authorize`)
   - Validate client_id, redirect_uri
   - Hiển thị form login
   - Tạo code và redirect

3. **Token Exchange Endpoint** (`POST /oauth/token`)
   - Validate code
   - Trả user info theo scopes

4. **Client App Registration** (Admin endpoint - tạm thời)
   - Tạo client_id, client_secret
   - Cấu hình redirect_uri, scopes

---

## Owner

- Người dùng đã chốt spec: **xjanua**
- Ngày chốt: 2026-07-12
