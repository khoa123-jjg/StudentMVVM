
package com.ute.studentmvvm.repository

import com.ute.studentmvvm.data.StudentDao
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.network.StudentApiService
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDao: StudentDao,
    private val apiService: StudentApiService
) {

    // Quan sát danh sách sinh viên trong Room
    val allStudents: Flow<List<Student>> =
        studentDao.getAllStudents()

    // Thêm sinh viên
    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
    }

    // Cập nhật sinh viên
    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    // Xóa sinh viên
    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }

    // Tìm sinh viên theo mã
    suspend fun getStudentByCode(code: String): Student? {
        return studentDao.getStudentByCode(code)
    }

    // Đồng bộ dữ liệu từ API về Room
    suspend fun syncStudentsFromApi(): Result<Int> {
        return try {

            val apiStudents = apiService.getStudents()

            val students = apiStudents.map { dto ->

                Student(
                    id = 1000 + dto.id,
                    fullName = dto.name,
                    studentCode = "API%04d".format(dto.id),
                    email = dto.email,
                    gpa = 0.0,
                    major = "Dữ liệu từ API"
                )

            }

            studentDao.insertAll(students)

            Result.success(students.size)

        } catch (exception: Exception) {

            Result.failure(exception)

        }
    }
}
