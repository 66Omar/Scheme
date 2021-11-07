package com.scheme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheme.ui.adapters.LectureListAdapter2
import com.scheme.utilities.NotificationSchedule
import com.scheme.viewModels.LectureSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LectureSelectionFragment : Fragment() {

    private val viewModel: LectureSelectionViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.selection_list, container, false)

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.LecDocFragmentTitle)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }


        val recyclerView: RecyclerView = root.findViewById(R.id.list_rec)
        val adapter = LectureListAdapter2()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        viewModel.lecturesList.observe(viewLifecycleOwner, {items ->
            adapter.setList(items)
        })

        adapter.itemClicked.observe(viewLifecycleOwner, { lecture ->
            val direction = LectureSelectionFragmentDirections.actionSelectionToHome()
            viewModel.update(lecture)
            NotificationSchedule.scheduleLectureNotifications(requireContext(), lecture)
            adapter.itemClicked.removeObservers(viewLifecycleOwner)
            Toast.makeText(requireContext(), "Lecture Added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(direction)
        })

        return root
    }
}