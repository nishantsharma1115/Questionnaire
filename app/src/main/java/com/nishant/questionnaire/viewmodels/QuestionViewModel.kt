package com.nishant.questionnaire.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.questionnaire.model.Item
import com.nishant.questionnaire.model.QuestionResponse
import com.nishant.questionnaire.repositories.QuestionRepository
import com.nishant.questionnaire.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val questionRepository: QuestionRepository = QuestionRepository(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var _getQuestionStatus = MutableLiveData<Resource<QuestionResponse>>()
    val getQuestionStatus: LiveData<Resource<QuestionResponse>> = _getQuestionStatus
    fun getQuestions() {
        _getQuestionStatus.postValue(Resource.Loading())
        viewModelScope.launch(dispatcher) {
            val apiResult = questionRepository.getQuestions()
            _getQuestionStatus.postValue(apiResult)
        }
    }

    private var _getAverageViewCount = MutableLiveData<String>()
    val getAverageViewCount: LiveData<String> = _getAverageViewCount

    private var _getAverageAnsCount = MutableLiveData<String>()
    val getAverageAnsCount: LiveData<String> = _getAverageAnsCount

    fun calculateAverages(list: List<Item>) = viewModelScope.launch(dispatcher) {
        var viewCount = 0
        var ansCount = 0
        list.forEach {
            viewCount += it.view_count
            ansCount += it.answer_count
        }
        val averageViewCount = viewCount / list.size
        val averageAnsCount = ansCount / list.size
        _getAverageViewCount.postValue(averageViewCount.toString())
        _getAverageAnsCount.postValue(averageAnsCount.toString())
    }

    private var _getAllTagsStatus = MutableLiveData<List<String>>()
    val getAllTagsStatus: LiveData<List<String>> = _getAllTagsStatus
    fun getAllTags(list: List<Item>) {
        val allTags: MutableSet<String> = HashSet()
        list.forEach { item ->
            item.tags.forEach {
                allTags.add(it)
            }
        }
        _getAllTagsStatus.postValue(allTags.toList())
    }
}