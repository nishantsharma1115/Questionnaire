package com.nishant.questionnaire.api

import com.nishant.questionnaire.model.QuestionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionApi {
    @GET("questions/")
    suspend fun getQuestions(
        @Query("key") key: String,
        @Query("order") order: String,
        @Query("sort") sort: String,
        @Query("site") site: String
    ): QuestionResponse
}