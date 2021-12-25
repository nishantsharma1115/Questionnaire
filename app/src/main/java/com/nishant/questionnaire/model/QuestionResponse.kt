package com.nishant.questionnaire.model

import com.nishant.questionnaire.model.Item

data class QuestionResponse(
    val has_more: Boolean,
    val items: List<Item>,
    val quota_max: Int,
    val quota_remaining: Int
)