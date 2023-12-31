package com.letranbaosuong.locationreminderapp.locationreminders.reminderslist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.letranbaosuong.locationreminderapp.R
import com.letranbaosuong.locationreminderapp.authentication.AuthenticationActivity
import com.letranbaosuong.locationreminderapp.base.BaseFragment
import com.letranbaosuong.locationreminderapp.base.NavigationCommand
import com.letranbaosuong.locationreminderapp.databinding.FragmentRemindersBinding
import com.letranbaosuong.locationreminderapp.locationreminders.ReminderDescriptionActivity
import com.letranbaosuong.locationreminderapp.utils.setDisplayHomeAsUpEnabled
import com.letranbaosuong.locationreminderapp.utils.setTitle
import com.letranbaosuong.locationreminderapp.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    // Use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reminders, container, false
        )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))
        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        initRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            addReminder()
        }
    }

    override fun onResume() {
        super.onResume()
        // Load reminders
        _viewModel.loadReminders()
    }

    private fun addReminder() {
        // Use NavigationCommand to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(ReminderListFragmentDirections.toSaveReminder())
        )
    }

    private fun initRecyclerView() {
        val adapter = RemindersListAdapter {
            // Navigate to the details reminder
            startActivity(
                ReminderDescriptionActivity.newIntent(requireContext(), it)
            )
        }
        // Setup recycler view
        binding.remindersRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val activity = requireActivity()
                            val intent =
                                Intent(requireContext(), AuthenticationActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        } else {
                            Snackbar.make(
                                requireView(),
                                getString(R.string.logout_unsuccessful),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // Display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }
}