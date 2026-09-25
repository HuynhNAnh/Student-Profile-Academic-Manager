package com.ute.studentprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ute.studentprofile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Demo hiển thị dữ liệu
        displayStudent(name = "Huỳnh Ngọc Anh", gpa = 3.65, email = "anhsieu572@gmail.com")
        processAvatarUri(null)

        // Sự kiện click nút cập nhật
        binding.btnUpdate.setOnClickListener {
            calculateAndAudit(3.65)
        }
    }

    // — 1. Gom nhóm thao tác hiển thị với 'with(binding)' ———
    private fun displayStudent(name: String, gpa: Double, email: String) {
        // Bên trong with(binding), mọi View thuộc binding đều là 'this'
        with(binding) {
            tvName.text = name
            tvGpa.text = "Điểm tích lũy: $gpa"
            tvEmail.text = email
            btnUpdate.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    // — 2. Cấu hình Intent hoặc View mới với 'apply' ———
    private fun openDetailActivity(studentId: String) {
        val detailIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("KEY_STUDENT_ID", studentId)
            putExtra("KEY_TIMESTAMP", System.currentTimeMillis())
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(detailIntent)
    }

    // — 3. Kiểm tra Null Safety với Safe Call ?.let ———
    private fun processAvatarUri(avatarUri: Uri?) {
        // Khối lệnh chỉ chạy khi avatarUri KHÁC NULL
        avatarUri?.let { validUri ->
            binding.imgAvatar.setImageURI(validUri)
            binding.tvAvatarStatus.text = "Đã tải ảnh đại diện!"
            toast("Ảnh đã được cập nhật")
        } ?: run {
            // Chạy khi avatarUri == null
            binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar)
        }
    }

    // — 4. Chèn hành động phụ (Side-Effects) với 'also' ———
    private fun calculateAndAudit(rawScore: Double): Double {
        return (rawScore * 10.0 / 4.0)
            .also { finalScore ->
                Log.d("STUDENT_AUDIT", "Điểm hệ 10 quy đổi: $finalScore")
            }
            .also {
                toast("Đã tính xong điểm: $it")
            }
    }

    // Hàm tiện ích hiển thị Toast
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}