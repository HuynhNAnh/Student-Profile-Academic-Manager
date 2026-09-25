package com.ute.studentprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.ute.studentprofile.databinding.ActivityMainBinding
import com.ute.studentprofile.model.Student
import com.ute.studentprofile.utils.gone
import com.ute.studentprofile.utils.show
import com.ute.studentprofile.utils.toAcademicRanking
import com.ute.studentprofile.utils.toRankingColor
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

            // TỰ MỞ RỘNG 1: Đổi màu chữ của Badge động theo ngưỡng xếp loại
            tvGpaBadge.setTextColor(student.gpa.toRankingColor())

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
                binding.edtGpaInput.error = null

                val tempScore = input.toDoubleOrNull()
                if (tempScore != null && tempScore in 0.0..4.0) {
                    binding.tvPreviewRanking.text = "Dự kiến: ${tempScore.toAcademicRanking()}"
                    binding.tvPreviewRanking.setTextColor(tempScore.toRankingColor())
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

        // TỰ MỞ RỘNG 2: Nút Khôi phục kèm Dialog Xác nhận (AlertDialog)
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Xác nhận khôi phục")
                setMessage("Bạn có chắc chắn muốn đặt lại điểm GPA ban đầu (${defaultStudent.gpa}) không?")
                setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton("Đồng ý") { _, _ ->
                    currentStudent = defaultStudent
                    bindStudentData(currentStudent)
                    binding.edtGpaInput.error = null
                    binding.tvPreviewRanking.gone()
                    toast("Đã khôi phục dữ liệu mặc định!")
                }
            }.show()
        }

        // TỰ MỞ RỘNG 3: Nút Gửi Email Báo cáo Kết quả (Implicit Intent)
        binding.btnSendReport.setOnClickListener {
            val subject = "[Báo cáo học tập] Sinh viên ${currentStudent.name} - MSSV ${currentStudent.id}"
            val body = """
                Kính gửi Phòng Đào Tạo và Cố Vấn Học Tập,

                Dưới đây là thông tin báo cáo kết quả học tập của sinh viên:
                • Họ và tên: ${currentStudent.name}
                • Mã số sinh viên (MSSV): ${currentStudent.id}
                • Lớp sinh hoạt: ${currentStudent.className}
                • Điểm trung bình tích lũy (GPA): ${currentStudent.gpa} / 4.0
                • Xếp loại học lực: ${currentStudent.gpa.toAcademicRanking()}
                • Danh hiệu vinh danh: ${if (currentStudent.isHonorStudent) "Sinh viên Vinh danh ⭐" else "Bình thường"}

                Trân trọng,
                ${currentStudent.name}
            """.trimIndent()

            // Mã hóa URI để Gmail và mọi ứng dụng email đều nhận diện đầy đủ To, Subject và Body
            val mailtoUri = Uri.parse(
                "mailto:${currentStudent.email}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
            )

            val emailIntent = Intent(Intent.ACTION_SENDTO, mailtoUri).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(currentStudent.email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                toast("Không tìm thấy ứng dụng gửi Email trên thiết bị!")
            }
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