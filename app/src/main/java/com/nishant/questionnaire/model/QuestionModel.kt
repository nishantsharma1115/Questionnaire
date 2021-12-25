package com.nishant.questionnaire.model

import com.nishant.questionnaire.model.Item

data class QuestionModel(
    val viewType: Int = 0,
    val item: Item
)