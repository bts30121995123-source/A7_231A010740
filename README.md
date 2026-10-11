# A7_231A010740

## LAB A7 – XIN QUYỀN TRUY CẬP

**Môn học:** Lập trình trên các thiết bị di động  
**Sinh viên:** Trịnh Phạm Xuân Nghi  
**MSSV:** 231A010740  
**Ngôn ngữ:** Java, XML  
**Công cụ:** Android Studio

## 1. Giới thiệu

Ứng dụng được xây dựng nhằm thực hành xin quyền truy cập tài nguyên trên Android, bao gồm quyền CAMERA và POST_NOTIFICATIONS.

## 2. Chức năng chính

- Xin quyền CAMERA và chụp ảnh xem trước
- Hiển thị hộp thoại giải thích khi người dùng từ chối quyền
- Mở Cài đặt ứng dụng để cấp lại quyền
- Xin quyền gửi thông báo trên Android 13 trở lên
- Hiển thị và cập nhật trạng thái quyền

## 3. Bài nâng cao

### NC1 – Xin hai quyền cùng lúc

Sử dụng RequestMultiplePermissions() để xin quyền CAMERA và POST_NOTIFICATIONS cùng lúc, sau đó kiểm tra và hiển thị kết quả của từng quyền.

### NC3 – Mở ứng dụng từ thông báo

Sử dụng PendingIntent và setContentIntent() để mở lại ứng dụng khi người dùng nhấn vào thông báo.

## 4. Kiểm thử

Ứng dụng được chạy thử trên máy ảo Pixel 9, Android 17 (API 37.1).

Các chức năng xin quyền, chụp ảnh, gửi thông báo và hai bài nâng cao đã được kiểm tra.

## 5. Kết quả

Ứng dụng thực hiện được các chức năng chính của Lab A7 và hoàn thành hai bài nâng cao NC1, NC3.
