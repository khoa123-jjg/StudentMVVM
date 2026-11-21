package com.ute.studentmvvm.network

import com.ute.studentmvvm.model.Student

data class StudentDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String
)

fun StudentDto.toEntity(): Student {
    return Student(
        id = 1000 + id,
        fullName = name,
        studentCode = "API%04d".format(id),
        email = email,
        gpa = 0.0,
        major = "Dữ liệu từ API"
    )
}
