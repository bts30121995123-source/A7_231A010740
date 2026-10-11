
package vn.edu.vhu.ltdd.a7permission;

import android.app.PendingIntent;
import android.content.Intent;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A7_231A010740";
    private static final String CHANNEL_ID = "a7_channel";
    private static final int NOTI_ID = 1001;

    private ImageView imgAnh;
    private TextView tvTrangThai;

    // Xin quyen CAMERA
    private final ActivityResultLauncher<String> xinQuyenCamera =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    duocCap -> {
                        Log.d(TAG, "Kết quả CAMERA: " + duocCap);

                        if (duocCap) {
                            moCamera();
                        } else if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) {
                            hienGiaiThich();
                        } else {
                            hienMoCaiDat();
                        }
                    });

    // Xin quyen thong bao
    private final ActivityResultLauncher<String> xinQuyenThongBao =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    duocCap -> {
                        if (duocCap) {
                            guiThongBao();
                        } else {
                            Toast.makeText(this,
                                    R.string.noti_denied,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    // NC1: Xin hai quyen cung luc
    private final ActivityResultLauncher<String[]> xinHaiQuyen =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    (Map<String, Boolean> ketQua) -> {

                        boolean camera = Boolean.TRUE.equals(
                                ketQua.get(Manifest.permission.CAMERA));

                        boolean thongBao;

                        if (Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.TIRAMISU) {
                            thongBao = Boolean.TRUE.equals(
                                    ketQua.get(
                                            Manifest.permission.POST_NOTIFICATIONS));
                        } else {
                            thongBao = true;
                        }

                        Log.d(TAG, "NC1 - CAMERA: " + camera);
                        Log.d(TAG, "NC1 - THONG BAO: " + thongBao);

                        String thongDiep =
                                "CAMERA: " +
                                        (camera ? "ĐÃ CẤP" : "CHƯA CẤP") +
                                        "\nTHÔNG BÁO: " +
                                        (thongBao ? "ĐÃ CẤP" : "CHƯA CẤP");

                        new AlertDialog.Builder(this)
                                .setTitle("Kết quả NC1")
                                .setMessage(thongDiep)
                                .setPositiveButton("Đóng", null)
                                .show();

                        capNhatTrangThai();
                    });

    // Mo Camera lay anh xem truoc
    private final ActivityResultLauncher<Void> chupAnh =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    (Bitmap bitmap) -> {
                        if (bitmap != null) {
                            imgAnh.setImageBitmap(bitmap);
                            tvTrangThai.setText(getString(
                                    R.string.status_photo,
                                    bitmap.getWidth(),
                                    bitmap.getHeight()));
                        } else {
                            Toast.makeText(this,
                                    R.string.photo_cancelled,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            bars.left, bars.top,
                            bars.right, bars.bottom);
                    return insets;
                });

        imgAnh = findViewById(R.id.imgAnh);
        tvTrangThai = findViewById(R.id.tvTrangThai);

        Button btnChupAnh = findViewById(R.id.btnChupAnh);
        Button btnThongBao = findViewById(R.id.btnThongBao);
        Button btnCaiDat = findViewById(R.id.btnCaiDat);
        Button btnNC1 = findViewById(R.id.btnNC1);

        taoKenhThongBao();

        // Khoi phuc chuc nang chup anh binh thuong
        btnChupAnh.setOnClickListener(
                v -> kiemTraRoiChup());

        btnThongBao.setOnClickListener(
                v -> kiemTraRoiGuiThongBao());

        btnCaiDat.setOnClickListener(
                v -> moCaiDatUngDung());

        // Nut NC1
        btnNC1.setOnClickListener(
                v -> kiemTraHaiQuyen());
    }

    @Override
    protected void onResume() {
        super.onResume();
        capNhatTrangThai();
    }

    // NC1: Kiem tra va xin hai quyen
    private void kiemTraHaiQuyen() {
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            xinHaiQuyen.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
            });

        } else {
            // Android 12 tro xuong khong can xin POST_NOTIFICATIONS
            xinHaiQuyen.launch(new String[]{
                    Manifest.permission.CAMERA
            });
        }
    }

    // LUONG CAMERA

    private void kiemTraRoiChup() {
        int trangThai = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);

        if (trangThai == PackageManager.PERMISSION_GRANTED) {
            moCamera();
        } else if (shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA)) {
            hienGiaiThich();
        } else {
            xinQuyenCamera.launch(Manifest.permission.CAMERA);
        }
    }

    private void hienGiaiThich() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rationale_title)
                .setMessage(R.string.rationale_camera)
                .setPositiveButton(
                        R.string.agree,
                        (d, w) -> xinQuyenCamera.launch(
                                Manifest.permission.CAMERA))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void hienMoCaiDat() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.blocked_title)
                .setMessage(R.string.blocked_camera)
                .setPositiveButton(
                        R.string.open_settings,
                        (d, w) -> moCaiDatUngDung())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void moCamera() {
        chupAnh.launch(null);
    }

    private void moCaiDatUngDung() {
        Intent i = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        i.setData(Uri.fromParts(
                "package", getPackageName(), null));

        startActivity(i);
    }

    // LUONG THONG BAO

    private void kiemTraRoiGuiThongBao() {
        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.TIRAMISU) {
            guiThongBao();
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            guiThongBao();
        } else {
            xinQuyenThongBao.launch(
                    Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void taoKenhThongBao() {
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel kenh =
                    new NotificationChannel(
                            CHANNEL_ID,
                            getString(R.string.channel_name),
                            NotificationManager.IMPORTANCE_DEFAULT);

            kenh.setDescription(
                    getString(R.string.channel_desc));

            getSystemService(NotificationManager.class)
                    .createNotificationChannel(kenh);
        }
    }

    private void guiThongBao() {

        private void guiThongBao() {

            // NC3: Tao Intent de mo lai MainActivity
            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            // NC3: Tao PendingIntent khi nguoi dung bam thong bao
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT |
                            PendingIntent.FLAG_IMMUTABLE
            );

            // Tao thong bao
            NotificationCompat.Builder b =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(
                                    android.R.drawable.ic_dialog_info)
                            .setContentTitle(
                                    getString(R.string.noti_title))
                            .setContentText(
                                    getString(R.string.noti_text))
                            .setPriority(
                                    NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            try {
                NotificationManagerCompat.from(this)
                        .notify(NOTI_ID, b.build());

                Log.d(TAG, "NC3: Da gui thong bao");

            } catch (SecurityException e) {
                Log.w(TAG, "Thieu quyen gui thong bao", e);
            }
        }

        // HIEN THI TRANG THAI

    private void capNhatTrangThai() {
        boolean camera = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        boolean thongBao =
                Build.VERSION.SDK_INT <
                        Build.VERSION_CODES.TIRAMISU
                        || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED;

        String s = getString(
                R.string.status_format,
                camera ? getString(R.string.granted)
                        : getString(R.string.denied),
                thongBao ? getString(R.string.granted)
                        : getString(R.string.denied));

        tvTrangThai.setText(s);
        Log.d(TAG, s.replace("\n", " | "));
    }
}
