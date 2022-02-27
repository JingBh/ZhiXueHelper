package top.jingbh.zhixuehelper.ui.auth

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import top.jingbh.zhixuehelper.databinding.FragmentLoginChooseBinding
import top.jingbh.zhixuehelper.databinding.ItemLoginMethodBinding
import top.jingbh.zhixuehelper.ui.util.VerticalSpaceItemDecoration
import top.jingbh.zhixuehelper.ui.util.dpToPx
import top.jingbh.zhixuehelper.util.PACKAGE_ZHIXUEAPP_PARENT
import top.jingbh.zhixuehelper.util.PACKAGE_ZHIXUEAPP_STUDENT
import javax.inject.Inject

@AndroidEntryPoint
class ChooseFragment : Fragment() {
    @Inject
    lateinit var packageManager: PackageManager

    private lateinit var binding: FragmentLoginChooseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listAdapter = Adapter()
        binding.list.apply {
            adapter = listAdapter
            addItemDecoration(VerticalSpaceItemDecoration(dpToPx(24)))
        }

        val methods = LoginMethod.getAvailableMethods(packageManager)
        listAdapter.submitList(methods)
    }

    private inner class ViewHolder(private val binding: ItemLoginMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(loginMethod: LoginMethod) {
            binding.name.text = loginMethod.getName(context.resources)
            binding.help.text = loginMethod.getHelp(context.resources)
            binding.iconApp.setImageDrawable(loginMethod.getIcon(context))
            loginMethod.getNavigationDestination().also { destination ->
                when {
                    loginMethod == LoginMethod.IMPORT_STUDENT -> {
                        val action =
                            ChooseFragmentDirections.chooseImport(PACKAGE_ZHIXUEAPP_STUDENT)
                        binding.root.setOnClickListener { view ->
                            view.findNavController().navigate(action)
                        }
                    }
                    loginMethod == LoginMethod.IMPORT_PARENT -> {
                        val action =
                            ChooseFragmentDirections.chooseImport(PACKAGE_ZHIXUEAPP_PARENT)
                        binding.root.setOnClickListener { view ->
                            view.findNavController().navigate(action)
                        }
                    }
                    destination != 0 -> {
                        binding.root.setOnClickListener { view ->
                            view.findNavController().navigate(destination)
                        }
                    }
                    else -> {
                        binding.iconEnter.visibility = View.GONE
                    }
                }
            }
        }
    }

    private inner class Adapter :
        ListAdapter<LoginMethod, ViewHolder>(object : DiffUtil.ItemCallback<LoginMethod>() {
            override fun areItemsTheSame(oldItem: LoginMethod, newItem: LoginMethod): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LoginMethod, newItem: LoginMethod): Boolean {
                return oldItem == newItem
            }
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemLoginMethodBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }
}
