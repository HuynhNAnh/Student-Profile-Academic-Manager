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
import com.ute.studentprofile.model.Student

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // BÀI 5: Đối tượng sinh viên hiện tại (Data Class bất biến)
    private var currentStudent = Student(
        id = "22505120005",
        name = "Nguyễn Văn An",
        className = "22CT111",
        email = "an.nv@ute.udn.vn",
        gpa = 3.75
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BÀI 5: Gán toàn bộ thông tin ban đầu từ model lên giao diện
        bindStudentData(currentStudent)

        // Khởi tạo ảnh đại diện mặc định
        processAvatarUri(null)

        // Cài đặt các bộ lắng nghe sự kiện
        setupEventListeners()
    }

    /**
     * BÀI 5: Hàm gán toàn bộ thông tin từ model lên các Views giao diện
     */
    private fun bindStudentData(student: Student) {
        with(binding) {
            tvStudentName.text = student.name
            tvStudentDetails.text = "MSSV: ${student.id}  •  Lớp: ${student.className}"
            tvStudentEmail.text = "Email: ${student.email}"
            tvGpaBadge.text = "${student.gpa} GPA  •  ${student.gpa.toAcademicRanking()}"
            edtGpaInput.setText(student.gpa.toString())

            // Kiểm tra Computed Property: Sinh viên tiêu biểu / Vinh danh
            if (student.isHonorStudent) {
                tvHonorBadge.show()
            } else {
                tvHonorBadge.gone()
            }
        }
    }

    /**
     * Cài đặt các sự kiện (Bài 4 & Bài 5)
     */
    private fun setupEventListeners() {
        // 1. BÀI 4: Lắng nghe sự kiện người dùng gõ phím (Realtime Validation)
        binding.edtGpaInput.doOnTextChanged { text, _, _, _ ->
            val input = text?.toString()?.trim().orEmpty()

            if (input.isNotEmpty()) {
                // Tự động xóa thông báo lỗi đỏ cũ khi người dùng bắt đầu sửa
                binding.edtGpaInput.error = null

                // Xem trước xếp loại học lực tương ứng thời gian thực
                val tempScore = input.toDoubleOrNull()
                if (tempScore != null && tempScore in 0.0..4.0) {
                    binding.tvPreviewRanking.text = "Dự kiến: ${tempScore.toAcademicRanking()}"
                    binding.tvPreviewRanking.show()
                } else {
                    binding.tvPreviewRanking.gone()
                }
            } else {
                binding.tvPreviewRanking.gone()
            }
        }

        // 2. BÀI 4 & 5: Validate điểm GPA và cập nhật bất biến với copy()
        binding.btnUpdateGpa.setOnClickListener {
            val rawInput = binding.edtGpaInput.trimmedText()

            // Bước 1: Chuyển đổi an toàn (phòng thủ crash với toDoubleOrNull)
            val newGpa = rawInput.toDoubleOrNull()

            // Bước 2: Kiểm tra điều kiện hợp lệ (từ 0.0 đến 4.0)
            if (newGpa == null || newGpa !in 0.0..4.0) {
                binding.edtGpaInput.error = "Vui lòng nhập GPA hợp lệ (0.0 - 4.0)"
                binding.edtGpaInput.requestFocus()
                toast("Điểm số không hợp lệ, vui lòng kiểm tra lại!")
                return@setOnClickListener
            }

            // Bước 3: Nếu dữ liệu hợp lệ: Xóa thông báo lỗi
            binding.edtGpaInput.error = null

            // BÀI 5: Tính Bất biến (Immutability) - Tạo đối tượng mới bằng copy()
            currentStudent = currentStudent.copy(gpa = newGpa)

            // Đồng bộ dữ liệu mới lên Views
            bindStudentData(currentStudent)
            binding.tvPreviewRanking.gone()

            toast("Đã cập nhật GPA cho sinh viên: ${currentStudent.name}")
        }

        // Nút cập nhật hồ sơ chung
        binding.btnUpdate.setOnClickListener {
            toast("Hồ sơ đang hoạt động bình thường!")
        }
    }

    // — Gom nhóm thao tác hiển thị với 'with(binding)' (Bài 3) ———
    private fun displayStudent(name: String, gpa: Double, email: String) {
        with(binding) {
            tvStudentName.text = name
            tvStudentEmail.text = "Email: $email"
            tvGpaBadge.text = "$gpa GPA  •  ${gpa.toAcademicRanking()}"
            btnUpdate.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    // — Cấu hình Intent với 'apply' (Bài 3) ———
    private fun openDetailActivity(studentId: String) {
        val detailIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("KEY_STUDENT_ID", studentId)
            putExtra("KEY_TIMESTAMP", System.currentTimeMillis())
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(detailIntent)
    }

    // — Kiểm tra Null Safety với Safe Call ?.let (Bài 3) ———
    private fun processAvatarUri(avatarUri: Uri?) {
        avatarUri?.let { validUri ->
            binding.imgAvatar.setImageURI(validUri)
            binding.tvAvatarStatus.text = "Đã tải ảnh đại diện!"
            toast("Ảnh đã được cập nhật")
        } ?: run {
            binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar)
        }
    }

    // — Chèn hành động phụ (Side-Effects) với 'also' (Bài 3) ———
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