package top.jingbh.zhixuehelper.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.R
import top.jingbh.zhixuehelper.databinding.FragmentLoginImportBinding
import javax.inject.Inject

@AndroidEntryPoint
class MethodImportFragment : Fragment() {
    @Inject
    lateinit var storageManager: StorageManager

    private lateinit var binding: FragmentLoginImportBinding

    private val args: MethodImportFragmentArgs by navArgs()

    private val viewModel: MethodImportViewModel by viewModels()

    private val loginViewModel: LoginViewModel by activityViewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                checkPermissions()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.import_need_permissions)
                    .setMessage(R.string.import_check_permissions_help)
                    .setPositiveButton(R.string.okay, null)
                    .show()
                navigateUp()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isCheckingPermissions().observe(viewLifecycleOwner) { isCheckingPermission ->
            if (isCheckingPermission) {
                binding.progress.visibility = View.VISIBLE
                binding.iconStatus.visibility = View.GONE
            } else {
                binding.progress.visibility = View.GONE
                binding.iconStatus.visibility = View.VISIBLE
            }
        }

        viewModel.getCheckPermissionStatus().observe(viewLifecycleOwner) { status ->
            val drawable = if (status == true) {
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_round_check_24)
            } else AppCompatResources.getDrawable(requireContext(), R.drawable.ic_round_close_24)
            binding.iconStatus.setImageDrawable(drawable)
        }

        viewModel.needsRequestPermission().observe(viewLifecycleOwner) { needsRequestPermission ->
            if (needsRequestPermission) requestPermission()
        }

        viewModel.getFailedMessage().observe(viewLifecycleOwner) { failedMessage ->
            if (viewModel.isCheckingPermissions().value == false && failedMessage != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.import_fail)
                    .setMessage(failedMessage)
                    .setPositiveButton(R.string.okay, null)
                    .setOnDismissListener { navigateUp() }
                    .show()
            }
        }

        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            loginViewModel.updateToken(token)
            navigateUp()
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        val myDataDirPath = requireContext().getExternalFilesDir(null)?.parent
        viewModel.checkPermissions(myDataDirPath, args.packageName)
    }

    private fun requestPermission(rationalShown: Boolean = false) {
        val activity = requireActivity()

        val granted = ContextCompat.checkSelfPermission(activity, PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i(TAG, "Scoped storage enabled")

            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.import_assistance)
                .setMessage(R.string.import_assistance_help)
                .setNegativeButton(R.string.cancel) { _, _ -> navigateUp() }
                .setPositiveButton(R.string.okay) { _, _ -> requestPermissionAlternative() }
                .setOnCancelListener { navigateUp() }
                .show()
        } else if (granted == PackageManager.PERMISSION_GRANTED) {
            viewModel.setFailedMessage(R.string.import_fail_system)
        } else {
            val rationalRequired =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION)
            if (rationalShown || !rationalRequired) {
                requestPermissionLauncher.launch(PERMISSION)
            } else MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.import_request_permissions)
                .setMessage(R.string.import_request_permissions_help)
                .setNegativeButton(R.string.cancel) { _, _ -> navigateUp() }
                .setPositiveButton(R.string.okay) { _, _ ->
                    requestPermission(true)
                }
                .setOnCancelListener { navigateUp() }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissionAlternative() {
        // Workaround adapted from: https://stackoverflow.com/a/67737067
        val rawIntent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

        var uri: Uri = rawIntent.getParcelableExtra("android.provider.extra.INITIAL_URI")!!
        var scheme = uri.toString()
        Log.d(TAG, "INITIAL_URI scheme: $scheme")

        scheme = scheme.replace("/root/", "/document/")
        val startDir = viewModel.getUserDir().value!!
            .replaceBefore("Android", "")
            .replace("/", "%2F")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
        Log.d(TAG, "Altered URI: $uri")

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.setClassName(
            "com.google.android.documentsui",
            "com.android.documentsui.files.FilesActivity"
        )
        startActivity(intent)

        navigateUp()
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }

    companion object {
        private const val TAG = "MethodImportFragment"

        private const val PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }
}
