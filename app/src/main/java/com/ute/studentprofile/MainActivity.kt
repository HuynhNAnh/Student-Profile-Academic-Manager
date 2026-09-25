package com.ute.studentprofile

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.ute.studentprofile.databinding.ActivityMainBinding
import com.ute.studentprofile.model.Student
import com.ute.studentprofile.utils.gone
import com.ute.studentprofile.utils.show
import com.ute.studentprofile.utils.toAcademicRanking
import com.ute.studentprofile.utils.toast
import com.ute.studentprofile.utils.trimmedText

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Dữ liệu sinh viên mặc định
    private val defaultStudent = Student(
        id = "2415053122202",
        name = "Huỳnh Ngọc Anh",
        className = "24T2",
        email = "anhsieu572@gmail.com",
        gpa = 3.75
    )

    // Dữ liệu sinh viên hiện tại (được quản lý bất biến)
    private var currentStudent = defaultStudent

    companion object {
        private const val KEY_STUDENT = "KEY_STUDENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Khôi phục trạng thái nếu vừa xoay màn hình (State Retention)
        savedInstanceState?.getSerializable(KEY_STUDENT)?.let {
            @Suppress("DEPRECATION")
            currentStudent = it as Student
        }

        // 2. Hiển thị dữ liệu lên giao diện
        bindStudentData(currentStudent)
        processAvatarUri(null)

        // 3. Khởi tạo các sự kiện tương tác
        setupEventListeners()
    }

    /**
     * Bảo toàn dữ liệu sinh viên khi xoay ngang / dọc màn hình
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_STUDENT, currentStudent)
    }

    /**
     * Gán toàn bộ thông tin từ Model lên các Views giao diện
     */
    private fun bindStudentData(student: Student) {
        with(binding) {
            tvStudentName.text = student.name
            tvStudentDetails.text = "MSSV: ${student.id}  •  Lớp: ${student.className}"
            tvStudentEmail.text = "Email: ${student.email}"
            tvGpaBadge.text = "${student.gpa} GPA  •  ${student.gpa.toAcademicRanking()}"
            edtGpaInput.setText(student.gpa.toString())

            // Hiển thị huy hiệu vinh danh dựa trên Computed Property
            if (student.isHonorStudent) {
                tvHonorBadge.show()
            } else {
                tvHonorBadge.gone()
            }
        }
    }

    /**
     * Thiết lập các sự kiện tương tác người dùng
     */
    private fun setupEventListeners() {
        // Lắng nghe gõ phím thời gian thực (Realtime Validation & Preview)
        binding.edtGpaInput.doOnTextChanged { text, _, _, _ ->
            val input = text?.toString()?.trim().orEmpty()
            if (input.isNotEmpty()) {
                // Tự động xóa cảnh báo lỗi cũ
                binding.edtGpaInput.error = null

                // Xem trước xếp loại học lực tương ứng
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

        // Nút chính: Cập nhật điểm GPA (Validate phòng thủ & Copy bất biến)
        binding.btnUpdateGpa.setOnClickListener {
            val gpa = binding.edtGpaInput.trimmedText().toDoubleOrNull()

            if (gpa == null || gpa !in 0.0..4.0) {
                binding.edtGpaInput.error = "GPA phải từ 0.0 đến 4.0"
                binding.edtGpaInput.requestFocus()
                toast("Điểm số không hợp lệ, vui lòng kiểm tra lại!")
                return@setOnClickListener
            }

            binding.edtGpaInput.error = null

            // Cập nhật Model theo nguyên tắc Immutability
            currentStudent = currentStudent.copy(gpa = gpa)
            bindStudentData(currentStudent)
            binding.tvPreviewRanking.gone()

            toast("Đã cập nhật GPA thành công!")
        }

        // Nút phụ: Khôi phục dữ liệu mặc định (Reset)
        binding.btnReset.setOnClickListener {
            currentStudent = defaultStudent
            bindStudentData(currentStudent)
            binding.edtGpaInput.error = null
            binding.tvPreviewRanking.gone()

            toast("Đã khôi phục dữ liệu mặc định!")
        }
    }

    /**
     * Xử lý ảnh đại diện với Safe Call ?.let
     */
    private fun processAvatarUri(avatarUri: Uri?) {
        avatarUri?.let { validUri ->
            binding.imgAvatar.setImageURI(validUri)
        } ?: run {
            binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar)
        }
    }
}