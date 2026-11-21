package com.ute.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.studentmvvm.R
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.ui.adapter.StudentAdapter
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory

class StudentListFragment : Fragment() {

    // Backing Property: Quản lý an toàn vòng đời ViewBinding của Fragment
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    // activityViewModels kèm Factory dự phòng đảm bảo không bao giờ bị NoSuchMethodException
    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext().applicationContext)
        val repository = StudentRepository(database.studentDao())
        StudentViewModelFactory(repository)
    }

    private lateinit var adapter: StudentAdapter
    private var fullStudentList: List<Student> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onStudentClick = { student -> showStudentDetailDialog(student) },
            onDeleteClick = { student -> confirmDelete(student) }
        )

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StudentListFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        // Quan sát danh sách sinh viên tự động từ Room DB
        viewModel.allStudents.observe(viewLifecycleOwner) { list ->
            fullStudentList = list
            filterStudents(binding.edtSearch.text.toString())
            binding.tvStudentCountBadge.text = "${list.size} sinh viên"
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterStudents(query: String) {
        val trimmed = query.trim().lowercase()
        val filtered = if (trimmed.isEmpty()) {
            fullStudentList
        } else {
            fullStudentList.filter {
                it.fullName.lowercase().contains(trimmed) ||
                it.studentCode.lowercase().contains(trimmed) ||
                it.email.lowercase().contains(trimmed)
            }
        }
        adapter.submitList(filtered)
        binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            AddStudentDialogFragment().show(childFragmentManager, "ADD_STUDENT_DIALOG")
        }

        binding.btnSync.setOnClickListener {
            viewModel.syncFromCloud()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sync -> {
                    viewModel.syncFromCloud()
                    true
                }
                else -> false
            }
        }

        binding.edtSearch.addTextChangedListener { editable ->
            filterStudents(editable?.toString() ?: "")
        }
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác Nhận Xóa")
            .setMessage("Bạn có chắc muốn xóa sinh viên ${student.fullName} khỏi CSDL Room?")
            .setPositiveButton("Xóa") { _, _ -> viewModel.delete(student) }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showStudentDetailDialog(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Thông Tin Sinh Viên")
            .setMessage(
                "ID: ${student.id}\n" +
                "Họ Tên: ${student.fullName}\n" +
                "MSSV: ${student.studentCode}\n" +
                "Email: ${student.email}\n" +
                "Điểm GPA: ${student.gpa}\n" +
                "Chuyên Ngành: ${student.major}"
            )
            .setPositiveButton("Đóng", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Bắt buộc giải phóng ViewBinding để chống rò rỉ bộ nhớ (Memory Leak)
        _binding = null
    }
}
