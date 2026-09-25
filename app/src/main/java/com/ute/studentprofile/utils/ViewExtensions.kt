package com.ute.studentprofile.utils

import android.content.Context
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
 * Tiện ích hiển thị Toast nhanh gọn trong Activity/Context.
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
