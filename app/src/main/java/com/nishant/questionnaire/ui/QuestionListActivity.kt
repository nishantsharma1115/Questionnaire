package com.nishant.questionnaire.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.nishant.questionnaire.adapter.QuestionAdapter
import com.nishant.questionnaire.databinding.ActivityQuestionListBinding
import com.nishant.questionnaire.model.Item
import com.nishant.questionnaire.ui.fragments.bottomsheet.BottomSheetFilter
import com.nishant.questionnaire.util.ConnectivityChecker
import com.nishant.questionnaire.util.DebouncingQueryTextListener
import com.nishant.questionnaire.util.Resource
import com.nishant.questionnaire.viewmodels.QuestionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class QuestionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionListBinding
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var adapter: QuestionAdapter
    private lateinit var questionList: List<Item>

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        questionViewModel = ViewModelProvider(this).get(QuestionViewModel::class.java)

        MobileAds.initialize(this)

        if (ConnectivityChecker.hasInternetConnection(this)) {
            questionViewModel.getQuestions()
        } else {
            binding.llCounts.visibility = View.GONE
            Snackbar.make(
                binding.root,
                "No internet Connection!!",
                Snackbar.LENGTH_LONG
            ).show()
        }
        questionViewModel.getQuestionStatus.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data != null) {
                        questionList = response.data.items
                        adapter = QuestionAdapter { url ->
                            showQuestionInBrowser(url)
                        }
                        binding.rvQuestions.adapter = adapter
                        binding.rvQuestions.layoutManager = LinearLayoutManager(applicationContext)
                        binding.rvQuestions.setHasFixedSize(true)
                        updateUI(questionList)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.btnFilter.setOnClickListener {
            supportFragmentManager.let { fragmentManager ->
                BottomSheetFilter(
                    questionViewModel,
                    questionList,
                    filterItemClicked = { tag ->
                        filterQuestion(tag)
                    }
                ) {
                    updateUI(questionList)
                    scrollToTop()
                }.apply {
                    show(fragmentManager, this.tag)
                }
            }
        }

        binding.edtSearch.setOnQueryTextListener(
            DebouncingQueryTextListener(this@QuestionListActivity.lifecycle) { newText ->
                newText?.let { it ->
                    if (it.isEmpty()) {
                        updateUI(questionList)
                    } else {
                        lifecycleScope.launch {
                            executeSearch(newText).collect { list ->
                                updateUI(list)
                            }
                        }
                    }
                }
            }
        )

        questionViewModel.getAverageViewCount.observe(this) { binding.averageViewCount = it }
        questionViewModel.getAverageAnsCount.observe(this) { binding.averageAnsCount = it }
    }

    private fun updateUI(listOfQuestion: List<Item>) {
        if (listOfQuestion.isNotEmpty()) {
            binding.llCounts.visibility = View.VISIBLE
            binding.rvQuestions.visibility = View.VISIBLE
            binding.txtNoQuestionFound.visibility = View.GONE
            adapter.setData(listOfQuestion)
            questionViewModel.calculateAverages(listOfQuestion)
        } else {
            binding.llCounts.visibility = View.GONE
            binding.rvQuestions.visibility = View.GONE
            binding.txtNoQuestionFound.visibility = View.VISIBLE
        }
    }

    private fun executeSearch(input: CharSequence?): Flow<List<Item>> = flow {
        val list = mutableListOf<Item>()
        if (input == null) {
            emit(list)
        } else {
            questionList.forEach {
                if (it.owner.display_name.contains(input) || it.title.contains(input)) {
                    list.add(it)
                }
            }
            emit(list)
        }
    }

    private fun filterQuestion(tag: String) {
        val newList = mutableListOf<Item>()
        questionList.forEach {
            if (it.tags.contains(tag)) {
                newList.add(it)
            }
        }
        updateUI(newList)
    }

    private fun showQuestionInBrowser(url: String) {
        if (ConnectivityChecker.hasInternetConnection(this)) {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            startActivity(openURL)
        } else {
            Snackbar.make(
                binding.root,
                "No internet Connection!!",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun scrollToTop() {
        val smoothScroller: RecyclerView.SmoothScroller =
            object : LinearSmoothScroller(this) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
        smoothScroller.targetPosition = 0
        (binding.rvQuestions.layoutManager as LinearLayoutManager).startSmoothScroll(
            smoothScroller
        )
    }
}