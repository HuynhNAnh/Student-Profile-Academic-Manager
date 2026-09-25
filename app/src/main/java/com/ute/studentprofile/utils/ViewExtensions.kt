package com.ute.studentprofile.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * Lấy nội dung chuỗi từ EditText đã được cắt khoảng trắng 2 đầu.
 */
fun EditText.trimmedText(): String = text?.toString()?.trim().orEmpty()

/**
 * Hiển thị View (View.VISIBLE).
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Ẩn View (View.GONE).
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Quy đổi điểm tích lũy hệ 4 sang danh hiệu học lực.
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

/**
 * TỰ MỞ RỘNG 1: Quy đổi điểm GPA sang màu sắc động tương ứng với xếp loại
 * - GPA >= 3.6 (Xuất sắc): Màu xanh lá cây (#34B469)
 * - GPA >= 3.2 (Giỏi): Màu xanh dương Cyan (#00BCD4)
 * - GPA >= 2.5 (Khá): Màu cam Amber (#FF9800)
 * - GPA < 2.5 (Trung bình / Yếu): Màu đỏ (#F44336)
 */
fun Double.toRankingColor(): Int {
    return when {
        this >= 3.6 -> Color.parseColor("#34B469")
        this >= 3.2 -> Color.parseColor("#00BCD4")
        this >= 2.5 -> Color.parseColor("#FF9800")
        else -> Color.parseColor("#F44336")
    }
}

/**
 * Tiện ích hiển thị Toast nhanh gọn trong Activity/Context.
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
