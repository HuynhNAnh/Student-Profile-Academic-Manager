package com.ute.studentprofile

import android.view.View
import android.widget.EditText

/**
 * Lấy nội dung chuỗi đã được loại bỏ khoảng trắng thừa ở 2 đầu.
 */
fun EditText.trimmedText(): String = text?.toString()?.trim().orEmpty()

/**
 * Hiển thị View (View.VISIBLE).
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Ẩn View và giải phóng không gian (View.GONE).
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Quy đổi điểm tích lũy hệ 4 sang danh hiệu học lực (Academic Ranking).
 */
fun Double.toAcademicRanking(): String {
    return when {
        this in 3.6..4.0 -> "Xuất sắc"
        this in 3.2..<3.6 -> "Giỏi"
        this in 2.5..<3.2 -> "Khá"
        this in 2.0..<2.5 -> "Trung bình"
        this in 0.0..<2.0 -> "Yếu / Kém"
        else -> "Không xác định"
    }
}
