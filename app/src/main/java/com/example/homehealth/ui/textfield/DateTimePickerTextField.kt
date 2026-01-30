@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.homehealth.ui.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerTextField(
    label: String,
    value: String,
    onDateTimeSelected: (String) -> Unit
) {
    val now = remember { Calendar.getInstance() }

    var selectedDateMillis by remember { mutableStateOf(now.timeInMillis) }
    var selectedHour by remember {
        mutableStateOf(roundTo30(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).first)
    }
    var selectedMinute by remember {
        mutableStateOf(roundTo30(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)).second)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // ---- Date Picker Dialog ----
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= startOfTodayMillis()
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateMillis = it
                        showDatePicker = false
                        showTimePicker = true
                    }
                }) {
                    Text("Next")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ---- Time Picker Dialog ----
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(onClick = {
                    val rounded = roundTo30(timePickerState.hour, timePickerState.minute)
                    selectedHour = rounded.first
                    selectedMinute = rounded.second
                    showTimePicker = false

                    onDateTimeSelected(
                        formatDateTime(selectedDateMillis, selectedHour, selectedMinute)
                    )
                }) {
                    Text("Confirm")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false, // 🔑 DO NOT use readOnly
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatDateTime(dateMillis: Long, hour: Int, minute: Int): String {
    val cal = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    return SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        .format(cal.time)
}

fun startOfTodayMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun roundTo30(hour: Int, minute: Int): Pair<Int, Int> {
    return if (minute < 30) Pair(hour, 0) else Pair(hour, 30)
}