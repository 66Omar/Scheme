package com.scheme

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.scheme.viewModels.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class Settings : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var years: List<String>
    private lateinit var faculties: List<String>
    private lateinit var universities: List<String>
    private lateinit var sections: List<String>
    private val listener = NavController.OnDestinationChangedListener { _, _, _ ->
        refresh()
    }
    private lateinit var toast: Toast
    private lateinit var snackbar: Snackbar
    private var nameText: TextView? = null
    private var sectionText: TextView? = null
    private var universityText: TextView? = null
    private var facultyText: TextView? = null
    private var yearText: TextView? = null
    private var seatText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        nameText = root.findViewById(R.id.name)
        sectionText = root.findViewById(R.id.section)
        universityText = root.findViewById(R.id.university)
        facultyText = root.findViewById(R.id.faculty)
        yearText = root.findViewById(R.id.year)
        seatText = root.findViewById(R.id.seat)
        val toolbar: Toolbar = root.findViewById(R.id.settings_toolbar)
        val nameCard: CardView = root.findViewById(R.id.namecard)
        val sectionCard: CardView = root.findViewById(R.id.sectioncard)
        val univesityCard: CardView = root.findViewById(R.id.univesitycard)
        val yearCard: CardView = root.findViewById(R.id.yearcard)
        val facultyCard: CardView = root.findViewById(R.id.facultycard)
        val seatCard: CardView = root.findViewById(R.id.seatcard)

        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        if (viewModel.shouldSetup()) {
            val dialog = SetupDialog()
            dialog.show(childFragmentManager, "setupDialog")
        }

        lifecycleScope.launchWhenStarted {
            viewModel.utility.collect {
                when (it) {
                    is SettingsViewModel.SettingsUtils.DisplayError -> {
                        showError(root, it.msg)
                    }
                }
            }
        }

        nameCard.setOnClickListener { showDialog(0, null) }

        univesityCard.setOnClickListener {
            if (::universities.isInitialized) { showDialog(1, universities) }
        }
        facultyCard.setOnClickListener {
            val validation: Boolean = viewModel.validateFaculty()
            if (::faculties.isInitialized && validation) { showDialog(2, faculties) }
            else { if (!validation) { showToast() } }
        }

        yearCard.setOnClickListener {
            val validation: Boolean = viewModel.validateYear()
            if (::years.isInitialized && validation) { showDialog(3, years) }
            else { if (!validation) { showToast() } }
        }

        sectionCard.setOnClickListener {
            val validation: Boolean = viewModel.validateSection()
            if (::sections.isInitialized && validation) { showDialog(4, sections) }
            else { if (!validation) { showToast() } }
        }

        seatCard.setOnClickListener { showDialog(5, null) }

        return root
    }

    private fun subscribe() {
        viewModel.availableUni().removeObservers(viewLifecycleOwner)
        viewModel.availableYears().removeObservers(viewLifecycleOwner)
        viewModel.availableFaculty().removeObservers(viewLifecycleOwner)
        viewModel.availableSections()?.removeObservers(viewLifecycleOwner)

        viewModel.availableFaculty().observe(viewLifecycleOwner, { items -> faculties = items })
        viewModel.availableUni().observe(viewLifecycleOwner, { items -> universities = items })
        viewModel.availableYears().observe(viewLifecycleOwner, { items -> years = items })
        viewModel.availableSections()?.observe(viewLifecycleOwner, { items -> sections = items})
    }

    private fun refresh() {
        nameText?.text = viewModel.name
        sectionText?.text = viewModel.section
        universityText?.text = viewModel.university
        facultyText?.text = viewModel.faculty
        yearText?.text = viewModel.year
        seatText?.text = viewModel.seat
        subscribe()
    }

    private fun showDialog(card: Int, items: List<String>?) {
        val direction = SettingsDirections.actionGlobalToDialog(card, items?.toTypedArray())
        findNavController().navigate(direction)
    }

    private fun showError(layout: View, text: String) {
         snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE)
            .setAction("RETRY") { subscribe() }
            .setActionTextColor(Color.RED)

        snackbar.show()
    }

    private fun showToast() {
        if (::toast.isInitialized) { toast.cancel() }
        toast = Toast.makeText(requireContext(), "More information required before you can set this field!", Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onPause() {
        super.onPause()
        findNavController().removeOnDestinationChangedListener(listener)
        if (::snackbar.isInitialized && snackbar.isShown) {
            snackbar.dismiss()
        }
    }


    override fun onResume() {
        super.onResume()
        findNavController().addOnDestinationChangedListener(listener)
    }


}