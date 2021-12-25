package com.nishant.questionnaire.repositories

import com.nishant.questionnaire.api.RetrofitInstance
import com.nishant.questionnaire.model.QuestionResponse
import com.nishant.questionnaire.util.Constants
import com.nishant.questionnaire.util.Resource

class QuestionRepository {

    suspend fun getQuestions(): Resource<QuestionResponse> {
        return try {
            val response = RetrofitInstance.api.getQuestions(
                Constants.API_KEY,
                "desc",
                "activity",
                "stackoverflow"
            )
            Resource.Success(response)
        } catch (exception: Exception) {
            Resource.Error(exception.message.toString())
        }
    }
}