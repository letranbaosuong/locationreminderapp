package com.letranbaosuong.locationreminderapp.locationreminders.reminderslist

import com.letranbaosuong.locationreminderapp.R
import com.letranbaosuong.locationreminderapp.base.BaseRecyclerViewAdapter

// Use data binding to show the reminder on the item
class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}