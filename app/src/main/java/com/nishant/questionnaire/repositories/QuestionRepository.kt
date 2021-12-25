package com.nishant.questionnaire.repositories

import com.nishant.questionnaire.api.RetrofitInstance
import com.nishant.questionnaire.model.QuestionResponse
import com.nishant.questionnaire.util.Constants

class QuestionRepository {
    suspend fun getQuestions(
        successCallBack: (QuestionResponse) -> Unit,
        failureResponse: (String) -> Unit
    ) {
        try {
            val response = RetrofitInstance.api.getQuestions(
                Constants.API_KEY,
                "desc",
                "activity",
                "stackoverflow"
            )
            successCallBack(response)
        } catch (exception: Exception) {
            failureResponse(exception.message.toString())
        }
    }
}