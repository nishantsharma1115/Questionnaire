package com.nishant.questionnaire.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.nishant.questionnaire.databinding.SingleAdLayoutBinding
import com.nishant.questionnaire.databinding.SingleQuestionLayoutBinding
import com.nishant.questionnaire.model.Item
import com.nishant.questionnaire.util.QuestionDiffUtil

class QuestionAdapter(
    val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val questions = ArrayList<Item>()

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    class QuestionViewHolder(val binding: SingleQuestionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Item) {
            binding.question = question
        }
    }

    class AdViewHolder(private val binding: SingleAdLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
//            val rnd = Random
//            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
//            binding.hexCode = color
            try {
                val adRequest = AdRequest.Builder().build()
                binding.adView.loadAd(adRequest)
            } catch (exception: Exception) {
                Log.d("Exception in AdMob", exception.message.toString())
            }
        }
    }

    fun setData(newQuestions: List<Item>) {
        val diffCallback = QuestionDiffUtil(questions, newQuestions)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        questions.clear()
        questions.addAll(newQuestions)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ONE) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = SingleQuestionLayoutBinding.inflate(inflater, parent, false)
            QuestionViewHolder(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding = SingleAdLayoutBinding.inflate(inflater, parent, false)
            AdViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentQuestion = questions[position]
        if (position % 4 == 0 && position != 0) {
            (holder as AdViewHolder).bind()
        } else {
            val questionHolder = (holder as QuestionViewHolder)
            questionHolder.bind(currentQuestion)
            questionHolder.binding.questionCardView.setOnClickListener {
                onClick(currentQuestion.link)
            }
        }
    }

    override fun getItemCount(): Int = questions.size

    override fun getItemViewType(position: Int): Int {
        return if (position % 4 == 0 && position != 0) {
            VIEW_TYPE_TWO
        } else {
            VIEW_TYPE_ONE
        }
    }

}