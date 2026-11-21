
package com.ute.studentmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    // Danh sách sinh viên tự động cập nhật khi Room thay đổi
    val students = repository.allStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Thêm sinh viên
    fun insertStudent(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
        }
    }

    // Cập nhật sinh viên
    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    // Xóa sinh viên
    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // Tìm sinh viên theo mã
    fun getStudentByCode(
        code: String,
        onResult: (Student?) -> Unit
    ) {
        viewModelScope.launch {
            val student = repository.getStudentByCode(code)
            onResult(student)
        }
    }

    // Đồng bộ dữ liệu từ API
    fun syncStudents(
        onResult: (Result<Int>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.syncStudentsFromApi()
            onResult(result)
        }
    }
}
