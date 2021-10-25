package com.scheme

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.scheme.models.DayEvent
import com.scheme.utilities.AlarmUtils
import com.scheme.utilities.NotificationSender
import com.scheme.viewModels.TwoViewModel
import com.scheme.views.TimetableView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList


@AndroidEntryPoint
class Tab2 : Fragment() {
    private val FIRST_REMINDERS_ADVANCE = 1800000 //30 minutes
    private val SECOND_REMINDER_ADVANCE = 900000 //15 minutes

    private lateinit var twoViewModel: TwoViewModel
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
        twoViewModel = ViewModelProvider(this).get(TwoViewModel::class.java)

        twoViewModel.storedEvents?.observe(viewLifecycleOwner, { items ->
            if (twoViewModel.savedSection != null) {
            timetableView.showEvents(items, activity)
            lifecycleScope.launch(Dispatchers.Default) {
                scheduleNotifications(items)
                }
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

    private fun scheduleNotifications(Events: List<DayEvent>) {
        val ids = ArrayList<Int>()

        for (item in Events) {
            if (item.auto == "user") {
                for (i in 0..2) {
                    val id = item.id + SECOND_REMINDER_ADVANCE * i
                    var timeLeft = item.timeLeft
                    if (i == 1) {
                        timeLeft -= FIRST_REMINDERS_ADVANCE.toLong()
                    }
                    if (i == 2) {
                        timeLeft -= SECOND_REMINDER_ADVANCE.toLong()
                    }
                    if (timeLeft >= 0) {
                        val notificationIntent =
                            Intent(context, NotificationSender::class.java)
                        notificationIntent.putExtra(NotificationSender.NOTIFICATION_ID, id)
                        notificationIntent.putExtra(
                            NotificationSender.NOTIFICATION,
                            getNotification(
                                item.task,
                                item.beginning.string,
                                id,
                                i,
                                System.currentTimeMillis() + timeLeft,
                            )
                        )
                        val pendingIntent =
                            PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        val alarmManager =
                            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeLeft] =
                            pendingIntent
                        ids.add(id)
                    }
                }
            }
        }
        AlarmUtils.saveIdsInPreferences(context, ids)
    }

    private fun getNotification(
        eventName: String,
        beginning: String,
        id: Int,
        state: Int,
        time: Long,
    ): Notification {
        val builder = NotificationCompat.Builder(requireContext(), App.CHANNEL_TWO)
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java),
            0
        )
        builder.setContentIntent(pendingIntent)
        if (state == 0) {
            builder.setContentTitle("Event started")
        } else {
            builder.setContentTitle("Upcoming event")
        }
        builder.setDefaults(Notification.DEFAULT_ALL)
        var message = ""
        if (state == 0) {
            message = "Event $eventName is happening now\nStarted at: $beginning"
        }
        if (state == 1) {
            message = "You have an upcoming event: $eventName\nStarts at: $beginning"

        }
        if (state == 2) {
            message = "You have an upcoming event: $eventName\nStarts at: $beginning"
        }
        builder.setWhen(time)
        builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
        )
        builder.setContentText(message)

        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_MAX
        return builder.build()
    }


}