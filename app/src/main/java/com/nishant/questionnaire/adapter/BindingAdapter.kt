package com.nishant.questionnaire.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("setPostedOn")
fun TextView.setPostedOn(epoch: String) {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
//    val calender = Calendar.getInstance()
//    calender.timeInMillis = epoch.toLong()
//    val date = sdf.format(calender.time)
    val date = Date(epoch.toLong() * 1000)
    val postedDate = sdf.format(date)

    this.text = "Posted on: $postedDate"
}

@BindingAdapter("setQuestionImage")
fun CircleImageView.setQuestionImage(url: String) {
    this.load(url)
}