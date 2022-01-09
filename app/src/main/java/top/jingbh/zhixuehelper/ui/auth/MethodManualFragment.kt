package top.jingbh.zhixuehelper.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.DialogLoginManualBinding

@AndroidEntryPoint
class MethodManualFragment : DialogFragment() {
    private val loginViewModel: LoginViewModel by activityViewModels()

    private var inputToken: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = DialogLoginManualBinding.inflate(it.layoutInflater)

            binding.textField.editText?.doOnTextChanged { text, _, _, _ ->
                inputToken = text?.toString() ?: ""
            }

            MaterialAlertDialogBuilder(it)
                .setIcon(R.drawable.ic_round_edit_24)
                .setTitle(R.string.method_manual)
                .setView(binding.root)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    if (inputToken.isNotBlank()) {
                        loginViewModel.updateToken(inputToken)
                    } else {
                        Toast.makeText(activity, R.string.input_token_empty, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
