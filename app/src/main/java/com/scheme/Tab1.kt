package com.scheme


import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.scheme.models.Lecture
import com.scheme.ui.adapters.LectureListAdapter
import com.scheme.viewModels.OneViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scheme.utilities.NotificationSchedule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Tab1 : Fragment() {

    private lateinit var image: ImageView
    private lateinit var adapter: LectureListAdapter
    private lateinit var oneViewModel: OneViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tab1, container, false)
        image = root.findViewById(R.id.imageView3)

        oneViewModel = ViewModelProvider(this).get(OneViewModel::class.java)
        val viewPager: ViewPager2 = requireActivity().findViewById(R.id.view_pager)
        val mRecyclerView = root.findViewById<RecyclerView>(R.id.rec2)
        adapter = LectureListAdapter()

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        oneViewModel.stored?.observe(viewLifecycleOwner, { items ->
            if (oneViewModel.savedSection != null) {
                checkCount(items)
                adapter.setList(items)
                lifecycleScope.launch(Dispatchers.Default) {
                    NotificationSchedule.scheduleLectureNotifications(requireContext(), items)
                }
            }
        })

        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = mLayoutManager
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.mainfab)
        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE && viewPager.currentItem == 0) {
                    fab.show()
                } else {
                    if (viewPager.currentItem == 0) fab.show()
                }
            }
        })
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tab1_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val direction = MainFragmentDirections.actionHomeToSettings()
                if (findNavController().currentDestination?.id == R.id.view_pager_fragment) {
                    findNavController().navigate(direction)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        return when (item.itemId) {
            0 -> {
                val lecture = adapter.getItem(item.groupId)
                if (lecture != null) {
                    oneViewModel.delete(lecture)
                }
                true
            }
            1 -> {
                val lecture = adapter.getItem(item.groupId)
                val direction = MainFragmentDirections.homeToLectureSelection(lecture!!.lecture, lecture.doctor)
                findNavController().navigate(direction)
                true
            }
            2 -> {
                val lecture = adapter.getItem(item.groupId)
                val direction = MainFragmentDirections.homeToDoctors(lecture!!.lecture)
                findNavController().navigate(direction)
                true
            }

            else -> true
        }
    }

    private fun checkCount(items: List<Lecture>?) {
        if (items != null && items.isNotEmpty()) {
            image.visibility = View.INVISIBLE
        } else {
            if (items != null) {
                image.visibility = View.VISIBLE
            } else {
                image.visibility = View.INVISIBLE
            }
        }
    }

}