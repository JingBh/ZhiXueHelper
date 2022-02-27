package top.jingbh.zhixuehelper.ui.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.data.exam.ExamPaper
import top.jingbh.zhixuehelper.data.util.CustomRequestQueue
import top.jingbh.zhixuehelper.databinding.FragmentPaperDetailsBinding
import top.jingbh.zhixuehelper.ui.util.OSSGlideUrl
import top.jingbh.zhixuehelper.ui.util.emitDigits
import javax.inject.Inject

@AndroidEntryPoint
class PaperDetailsFragment : Fragment() {
    @Inject
    lateinit var requestQueue: CustomRequestQueue

    private lateinit var binding: FragmentPaperDetailsBinding

    private val viewModel: PaperDetailsViewModel by viewModels()

    private lateinit var paper: ExamPaper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        paper = arguments?.getSerializable(ARGUMENT_PAPER) as ExamPaper
        viewModel.initSetPaper(paper)

        binding = FragmentPaperDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState
                .map { it.isLoading }
                .distinctUntilChanged()
                .collect {
                    binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                }
        }

        lifecycleScope.launch {
            viewModel.paper
                .distinctUntilChangedBy { it.id }
                .collectLatest { paper ->
                    this@PaperDetailsFragment.paper = paper

                    binding.paperScoreMine.text = paper.userScore.emitDigits()
                    binding.paperScoreMax.text = " / ${paper.fullScore.emitDigits()}"

                    val assignedScore = paper.userLevel
                    if (assignedScore != null) {
                        binding.paperAssignedScoreMine.text = assignedScore.getScore().emitDigits()
                        binding.paperAssignedScoreDetails.text = "($assignedScore)"

                        binding.paperAssignedScoreCard.visibility = View.VISIBLE
                        binding.paperScoreArrow.visibility = View.VISIBLE
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.sheetImages
                .distinctUntilChanged()
                .collectLatest { images ->
                    binding.paperOriginalList.run {
                        removeAllViews()
                        images.forEach { uri ->
                            val imageView = ImageView(context)

                            addView(
                                imageView,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )

                            Glide.with(this)
                                .load(OSSGlideUrl.of(uri))
                                .placeholder(R.drawable.placeholder)
                                .into(imageView)
                        }
                    }

                    binding.paperOriginalCard.visibility =
                        if (images.isEmpty()) View.GONE else View.VISIBLE
                }
        }

        binding.paperAnswersCard.setOnClickListener {
            val action = ExamDetailsFragmentDirections.paperDetailsToAnalysis(paper)
            findNavController().navigate(action)
        }
    }

    companion object {
        const val ARGUMENT_PAPER = "top.jingbh.zhixuehelper.argument.PAPER"
    }
}
