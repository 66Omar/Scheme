package com.scheme

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.scheme.ui.adapters.SectionsPagerAdapter
import com.scheme.viewModels.MainFragmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment: Fragment() {
    private val tabTitles = intArrayOf(R.string.tab_text_1, R.string.tab_text_2)
    private lateinit var snackbar: Snackbar
    private val viewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val root = inflater.inflate(R.layout.fragment_main, container, false)

        val toolbar: Toolbar = root.findViewById(R.id.my_toolbar)
        val viewPager: ViewPager2 = root.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = root.findViewById(R.id.tabs)
        val fab: FloatingActionButton = root.findViewById(R.id.mainfab)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.setText(tabTitles[position])
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> if (!fab.isShown) {
                        fab.show()
                    }
                    1 -> fab.hide()
                }
            }

        })

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val direction = MainFragmentDirections.actionHomeToSelection()
            findNavController().navigate(direction)
        }

        if (viewModel.shouldSetup()) {
            val direction = MainFragmentDirections.actionHomeToSettings()
            findNavController().navigate(direction)
        }

        else if (viewModel.shouldCompleteSetup()) {
            val dialog = SetupDialog()
            dialog.show(childFragmentManager, "setupDialog")
        }
        viewModel.shouldUpdate().observe(viewLifecycleOwner, { currentVersion ->

                if (viewModel.savedVersion != null && currentVersion != viewModel.savedVersion
                    && currentVersion != null) {
                    snackbar = Snackbar.make(
                        root,
                        getString(R.string.UpdateAvailable),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("UPDATE") { viewModel.update(currentVersion) }
                        .setActionTextColor(Color.GREEN)
                    snackbar.show()
                }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.utility.collect {
                when (it) {
                    is MainFragmentViewModel.MainFragmentUtils.DisplayError -> {
                        Toast.makeText(requireActivity(), it.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentVersion.value = null
    }
}
