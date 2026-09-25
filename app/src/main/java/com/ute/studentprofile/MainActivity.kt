package com.ute.studentprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.ute.studentprofile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo dữ liệu hiển thị ban đầu
        displayStudent(name = "Nguyễn Văn A", gpa = 3.65, email = "nguyenvana@ute.edu.vn")
        processAvatarUri(null)

        // Cài đặt các sự kiện cho Bài 4
        setupValidationListeners()
    }

    /**
     * BÀI 4: Thiết lập sự kiện Validate và Lắng nghe gõ phím thời gian thực
     */
    private fun setupValidationListeners() {
        // 1. Lắng nghe sự kiện người dùng gõ từng ký tự vào ô nhập điểm (Realtime Validation)
        binding.edtGpaInput.doOnTextChanged { text, start, before, count ->
            val input = text?.toString()?.trim().orEmpty()

            if (input.isNotEmpty()) {
                // Tự động xóa thông báo lỗi đỏ cũ khi người dùng bắt đầu sửa
                binding.edtGpaInput.error = null

                // Xem trước xếp loại học lực tương ứng thời gian thực
                val tempScore = input.toDoubleOrNull()
                if (tempScore != null && tempScore in 0.0..4.0) {
                    binding.tvPreviewRanking.text = "Dự kiến: ${tempScore.toAcademicRanking()}"
                    binding.tvPreviewRanking.show() // Dùng extension
                } else {
                    binding.tvPreviewRanking.gone()
                }
            } else {
                binding.tvPreviewRanking.gone()
            }
        }

        // 2. Validate điểm GPA với setOnClickListener (Nguyên tắc phòng thủ)
        binding.btnUpdateGpa.setOnClickListener {
            val rawInput = binding.edtGpaInput.trimmedText()

            // Bước 1: Chuyển đổi an toàn - trả về null nếu chuỗi là chữ hoặc rỗng (tránh crash)
            val newGpa = rawInput.toDoubleOrNull()

            // Bước 2: Kiểm tra điều kiện hợp lệ (từ 0.0 đến 4.0)
            if (newGpa == null || newGpa !in 0.0..4.0) {
                // Hiển thị icon cảnh báo và thông điệp lỗi ngay trên EditText
                binding.edtGpaInput.error = "Vui lòng nhập GPA hợp lệ (0.0 - 4.0)"
                binding.edtGpaInput.requestFocus()
                toast("Điểm số không hợp lệ, vui lòng kiểm tra lại!")
                return@setOnClickListener // Dừng thực thi
            }

            // Bước 3: Nếu dữ liệu hợp lệ: Xóa thông báo lỗi và cập nhật
            binding.edtGpaInput.error = null
            updateStudentScore(newGpa)
        }

        // Nút cập nhật hồ sơ chung
        binding.btnUpdate.setOnClickListener {
            toast("Hồ sơ đã ở trạng thái mới nhất!")
        }
    }

    /**
     * Cập nhật điểm GPA vào giao diện sau khi đã validate hợp lệ
     */
    private fun updateStudentScore(newGpa: Double) {
        binding.tvGpa.text = "Điểm tích lũy: $newGpa (${newGpa.toAcademicRanking()})"
        binding.edtGpaInput.text?.clear()
        binding.tvPreviewRanking.gone()
        toast("Cập nhật GPA thành công!")
    }

    // — Gom nhóm thao tác hiển thị với 'with(binding)' ———
    private fun displayStudent(name: String, gpa: Double, email: String) {
        with(binding) {
            tvName.text = name
            tvGpa.text = "Điểm tích lũy: $gpa (${gpa.toAcademicRanking()})"
            tvEmail.text = email
            btnUpdate.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    // — Cấu hình Intent hoặc View mới với 'apply' ———
    private fun openDetailActivity(studentId: String) {
        val detailIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("KEY_STUDENT_ID", studentId)
            putExtra("KEY_TIMESTAMP", System.currentTimeMillis())
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(detailIntent)
    }

    // — Kiểm tra Null Safety với Safe Call ?.let ———
    private fun processAvatarUri(avatarUri: Uri?) {
        avatarUri?.let { validUri ->
            binding.imgAvatar.setImageURI(validUri)
            binding.tvAvatarStatus.text = "Đã tải ảnh đại diện!"
            toast("Ảnh đã được cập nhật")
        } ?: run {
            binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar)
        }
    }

    // — Chèn hành động phụ (Side-Effects) với 'also' ———
    private fun calculateAndAudit(rawScore: Double): Double {
        return (rawScore * 10.0 / 4.0)
            .also { finalScore ->
                Log.d("STUDENT_AUDIT", "Điểm hệ 10 quy đổi: $finalScore")
            }
            .also {
                toast("Đã tính xong điểm: $it")
            }
    }

    // Extension/hàm tiện ích hiển thị Toast nhanh
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}