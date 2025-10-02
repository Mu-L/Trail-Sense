package com.kylecorry.trail_sense.tools.packs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.andromeda.fragments.observe
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.luna.coroutines.onIO
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.FragmentItemListBinding
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.tools.packs.domain.Pack
import com.kylecorry.trail_sense.tools.packs.domain.PackItem
import com.kylecorry.trail_sense.tools.packs.domain.PackService
import com.kylecorry.trail_sense.tools.packs.domain.sort.CategoryPackItemSort
import com.kylecorry.trail_sense.tools.packs.domain.sort.PackedPercentPackItemSort
import com.kylecorry.trail_sense.tools.packs.domain.sort.WeightPackItemSort
import com.kylecorry.trail_sense.tools.packs.infrastructure.PackRepo
import com.kylecorry.trail_sense.tools.packs.ui.commands.ExportPackingListCommand
import com.kylecorry.trail_sense.tools.packs.ui.mappers.PackItemAction
import com.kylecorry.trail_sense.tools.packs.ui.mappers.PackItemListItemMapper
import com.kylecorry.trail_sense.shared.Units
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Double.max
import kotlin.math.floor

class PackItemListFragment : BoundFragment<FragmentItemListBinding>() {

    private val itemRepo by lazy { PackRepo.getInstance(requireContext()) }
    private val formatService by lazy { FormatService.getInstance(requireContext()) }
    private val packService = PackService()
    private var items by state(emptyList<PackItem>())
    private val prefs by lazy { UserPreferences(requireContext()) }

    private val listMapper by lazy { PackItemListItemMapper(requireContext(), this::handleAction) }

    private var pack: Pack? by state(null)
    private var packId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packId = arguments?.getLong("pack_id") ?: 0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        inBackground {
            loadPack(packId)
        }
    }

    private suspend fun loadPack(packId: Long) {
        onIO {
            pack = itemRepo.getPack(packId)
        }
    }

    private fun setupUI() {
        binding.inventoryList.emptyView = binding.inventoryEmptyText
        observe(itemRepo.getItemsFromPack(packId)) {
            items = it
        }

        binding.addBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_action_inventory_to_createItemFragment,
                bundleOf("pack_id" to packId)
            )
        }

        binding.inventoryListTitle.rightButton.setOnClickListener {
            Pickers.menu(
                binding.inventoryListTitle.rightButton,
                listOf(
                    getString(R.string.sort),
                    getString(R.string.rename),
                    getString(R.string.export),
                    getString(R.string.delete),
                    getString(R.string.clear_amounts)
                )
            ) {
                when (it) {
                    0 -> changeSort()
                    1 -> pack?.let { renamePack(it) }
                    2 -> pack?.let { exportPack(it) }
                    3 -> pack?.let { deletePack(it) }
                    4 -> pack?.let {
                        Alerts.dialog(
                            requireContext(),
                            getString(R.string.clear_amounts),
                            getString(R.string.action_inventory_clear_confirm)
                        ) { cancelled ->
                            if (!cancelled) {
                                inBackground {
                                    withContext(Dispatchers.IO) {
                                        itemRepo.clearPackedAmounts(packId)
                                    }
                                }
                            }
                        }
                    }
                }
                true
            }
        }
    }

    private fun onSortChange(newSort: String) {
        binding.inventoryList.setItems(sorts[newSort]?.sort(items) ?: items, listMapper)
        prefs.packs.packSort = newSort
    }

    private fun renamePack(pack: Pack) {
        Pickers.text(
            requireContext(),
            getString(R.string.rename),
            null,
            pack.name,
            hint = getString(R.string.name)
        ) {
            if (it != null) {
                inBackground {
                    itemRepo.addPack(pack.copy(name = it))
                    this@PackItemListFragment.pack = pack.copy(name = it)
                }
            }
        }
    }

    private fun exportPack(pack: Pack) {
        ExportPackingListCommand(this).execute(pack)
    }

    private fun deletePack(pack: Pack) {
        Alerts.dialog(
            requireContext(),
            getString(R.string.delete_pack),
            pack.name
        ) { cancelled ->
            if (!cancelled) {
                inBackground {
                    withContext(Dispatchers.IO) {
                        itemRepo.deletePack(pack)
                    }
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun onItemCheckboxClicked(item: PackItem) {
        if (!item.isFullyPacked) {
            if (item.desiredAmount == 0.0) {
                setAmount(item, 1.0)
            } else {
                setAmount(item, item.desiredAmount)
            }
        } else {
            setAmount(item, 0.0)
        }
    }

    private fun deleteItem(item: PackItem) {
        inBackground {
            withContext(Dispatchers.IO) {
                itemRepo.deleteItem(item)
            }
        }
    }

    private fun editItem(item: PackItem) {
        val bundle = bundleOf("edit_item_id" to item.id, "pack_id" to packId)
        findNavController().navigate(R.id.action_action_inventory_to_createItemFragment, bundle)
    }

    private fun handleAction(item: PackItem, action: PackItemAction) {
        when (action) {
            PackItemAction.Check -> onItemCheckboxClicked(item)
            PackItemAction.Add -> add(item)
            PackItemAction.Subtract -> subtract(item)
            PackItemAction.Edit -> editItem(item)
            PackItemAction.Delete -> deleteItem(item)
        }
    }

    private fun add(item: PackItem) {
        Pickers.number(
            requireContext(),
            getString(R.string.add),
            null,
            null,
            allowNegative = false,
            hint = getString(R.string.dialog_item_amount)
        ) {
            if (it != null) {
                addAmount(item, it.toDouble())
            }
        }
    }

    private fun subtract(item: PackItem) {
        Pickers.number(
            requireContext(),
            getString(R.string.subtract),
            null,
            null,
            allowNegative = false,
            hint = getString(R.string.dialog_item_amount)
        ) {
            if (it != null) {
                addAmount(item, -it.toDouble())
            }
        }
    }

    private fun addAmount(item: PackItem, amount: Double) {
        inBackground {
            withContext(Dispatchers.IO) {
                itemRepo.addItem(
                    item.copy(amount = max(0.0, item.amount + amount))
                )
            }
        }
    }

    private fun setAmount(item: PackItem, amount: Double) {
        inBackground {
            withContext(Dispatchers.IO) {
                itemRepo.addItem(
                    item.copy(amount = amount)
                )
            }
        }
    }

    private fun changeSort() {
        val options = listOf("category", "percent_asc", "percent_desc", "weight_asc", "weight_desc")
        Pickers.item(
            requireContext(),
            getString(R.string.sort),
            options.map { getSortTitle(it) },
            options.indexOf(prefs.packs.packSort)
        ) {
            if (it != null) {
                onSortChange(options[it])
            }
        }
    }

    private val sorts = mapOf(
        "category" to CategoryPackItemSort(),
        "percent_asc" to PackedPercentPackItemSort(true),
        "percent_desc" to PackedPercentPackItemSort(false),
        "weight_asc" to WeightPackItemSort(true),
        "weight_desc" to WeightPackItemSort(false),
    )

    private fun getSortTitle(sort: String): String {
        return when (sort) {
            "category" -> getString(R.string.category)
            "percent_asc" -> getString(R.string.pack_sort_percent_low_to_high)
            "percent_desc" -> getString(R.string.pack_sort_percent_high_to_low)
            "weight_asc" -> getString(R.string.pack_sort_weight_low_to_high)
            "weight_desc" -> getString(R.string.pack_sort_weight_high_to_low)
            else -> ""
        }
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentItemListBinding {
        return FragmentItemListBinding.inflate(layoutInflater, container, false)
    }

    override fun onUpdate() {
        super.onUpdate()

        effect("pack name", pack?.name, lifecycleHookTrigger.onResume()) {
            binding.inventoryListTitle.title.text = pack?.name
        }

        effect("items", items, lifecycleHookTrigger.onResume()) {
            val totalWeight = packService.getPackWeight(items, prefs.weightUnits)
            val packedPercent = floor(packService.getPercentPacked(items))
            binding.itemWeightOverview.isVisible = totalWeight != null
            binding.totalPackedWeight.text = if (totalWeight != null) {
                formatService.formatWeight(
                    totalWeight,
                    Units.getDecimalPlaces(totalWeight.units),
                    false
                )
            } else {
                ""
            }
            binding.totalPercentPacked.text =
                getString(R.string.percent_packed, formatService.formatPercentage(packedPercent))
            binding.inventoryList.setItems(
                sorts[prefs.packs.packSort]?.sort(items) ?: items,
                listMapper
            )
        }
    }


}