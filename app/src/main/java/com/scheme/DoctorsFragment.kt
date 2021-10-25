package com.scheme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scheme.ui.adapters.SelectionItemAdapter
import com.scheme.viewModels.DoctorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.selection_list, container, false)

        val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.DocFragmentTitle)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val viewModel = ViewModelProvider(this).get(
            DoctorViewModel::class.java
        )
        val recyclerView: RecyclerView = root.findViewById(R.id.list_rec)
        val adapter = SelectionItemAdapter()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.itemClicked.observe(viewLifecycleOwner, {doctor ->
            val direction = DoctorsFragmentDirections.actionDoctorsToLectures(viewModel.lectureTitle, doctor)
            findNavController().navigate(direction)
            adapter.itemClicked.removeObservers(viewLifecycleOwner)
        })

        viewModel.getList()?.observe(viewLifecycleOwner, { items -> adapter.setList(items) })

        return root
    }
}