package com.scheme


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.scheme.viewModels.TwoViewModel
import com.scheme.views.TimetableView
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class Tab2 : Fragment() {

    private val twoViewModel: TwoViewModel by viewModels()
    private lateinit var timetableView: TimetableView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_tab2, container, false)
        timetableView = root.findViewById(R.id.mytable)

        twoViewModel.storedEvents?.observe(viewLifecycleOwner, { items ->
            if (twoViewModel.savedSection != null) {
            timetableView.showEvents(items, activity)
            }
        })


        timetableView.setOnEventSelected { event ->
            timetableView.setOnEventSelected(null)
            val direction = MainFragmentDirections.actionHomeToEdit(event)
            findNavController().navigate(direction)
        }
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addEvent -> {
                val direction = MainFragmentDirections.actionHomeToEdit(null)
                findNavController().navigate(direction)
            }

            R.id.settings -> {
                val direction = MainFragmentDirections.actionHomeToSettings()
                if (findNavController().currentDestination?.id == R.id.view_pager_fragment) {
                    findNavController().navigate(direction)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tab2_menu, menu)
    }

}