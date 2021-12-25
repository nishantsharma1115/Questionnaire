package com.nishant.questionnaire.ui.fragments.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nishant.questionnaire.adapter.FilterAdapter
import com.nishant.questionnaire.databinding.BottomSheetFilterBinding
import com.nishant.questionnaire.model.Item
import com.nishant.questionnaire.viewmodels.QuestionViewModel

class BottomSheetFilter(
    private val questionViewModel: QuestionViewModel? = null,
    private val allTags: List<Item> = emptyList(),
    val filterItemClicked: (String) -> Unit,
    val onClearFilter: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionViewModel?.getAllTags(allTags)
        questionViewModel?.getAllTagsStatus?.observe(this) { allTags ->
            val adapter = FilterAdapter { tag ->
                filterItemClicked(tag)
                dismiss()
            }
            binding.rvTags.adapter = adapter
            binding.rvTags.layoutManager = LinearLayoutManager(context)
            binding.rvTags.setHasFixedSize(true)
            adapter.submitList(allTags)
        }
        binding.btnClear.setOnClickListener {
            onClearFilter()
            dismiss()
        }
    }
}