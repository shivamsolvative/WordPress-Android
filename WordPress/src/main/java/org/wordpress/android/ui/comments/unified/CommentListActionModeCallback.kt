package org.wordpress.android.ui.comments.unified

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.appcompat.view.ActionMode.Callback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import org.wordpress.android.R
import org.wordpress.android.ui.comments.unified.UnifiedCommentListViewModel.ActionModeUiModel
import org.wordpress.android.ui.comments.unified.UnifiedCommentListViewModel.ActionUiModel
import org.wordpress.android.ui.utils.UiString.UiStringText

class CommentListActionModeCallback(
    private val viewModel: UnifiedCommentListViewModel,
    private val activityViewModel: UnifiedCommentActivityViewModel
) : Callback,
        LifecycleOwner {
    private lateinit var lifecycleRegistry: LifecycleRegistry
    override fun onCreateActionMode(
        actionMode: ActionMode,
        menu: Menu
    ): Boolean {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(ON_START)
        val inflater = actionMode.menuInflater
        inflater.inflate(R.menu.menu_comments_cab, menu)

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { uiState ->
                when (val uiModel = uiState.actionModeUiModel) {
                    is ActionModeUiModel.Hidden -> {
                        actionMode.finish()
                        activityViewModel.onActionModeToggled(false)
                    }
                    is ActionModeUiModel.Visible -> {
                        activityViewModel.onActionModeToggled(true)
                        val approveItem = menu.findItem(R.id.menu_approve)
                        val unaproveItem = menu.findItem(R.id.menu_unapprove)
                        val spamItem = menu.findItem(R.id.menu_spam)
                        val unspamItem = menu.findItem(R.id.menu_unspam)
                        val trashItem = menu.findItem(R.id.menu_trash)
                        val untrashItem = menu.findItem(R.id.menu_untrash)
                        val deleteItem = menu.findItem(R.id.menu_delete)

                        setItemEnabled(approveItem, uiModel.approveActionUiModel)
                        setItemEnabled(unaproveItem, uiModel.unparoveActionUiModel)
                        setItemEnabled(spamItem, uiModel.spamActionUiModel)
                        setItemEnabled(unspamItem, uiModel.unspamActionUiModel)
                        setItemEnabled(trashItem, uiModel.trashActionUiModel)
                        setItemEnabled(untrashItem, uiModel.unTrashActionUiModel)
                        setItemEnabled(deleteItem, uiModel.deleteActionUiModel)


//                        val editItemUiModel = uiModel.editActionUiModel

//                        if (editItemUiModel.isVisible) {
//                            editItem.isVisible = true
//
//                            editItem.actionView.let { actionView ->
//                                actionView.setOnClickListener {
//                                    onActionItemClicked(actionMode, editItem)
//                                }
//                                TooltipCompat.setTooltipText(actionView, editItem.title)
//                            }
//
//                            val editItemBadge = editItem.actionView.findViewById<TextView>(R.id.customize_icon_count)
//                            if (editItemUiModel.isCounterBadgeVisible) {
//                                editItemBadge.visibility = View.VISIBLE
//                                editItemBadge.text = editItemUiModel.counterBadgeValue.toString()
//                            } else {
//                                editItemBadge.visibility = View.GONE
//                            }
//                        } else {
//                            editItem.isVisible = false
//                        }

                        if (uiModel.actionModeTitle is UiStringText) {
                            actionMode.title = uiModel.actionModeTitle.text
                        }
                    }
                }
            }
        }
        return true
    }

    private fun setItemEnabled(menuItem: MenuItem, actionUiModel: ActionUiModel) {
        menuItem.isVisible = actionUiModel.isVisible
        menuItem.isEnabled = actionUiModel.isEnabled
        if (menuItem.icon != null) {
            // must mutate the drawable to avoid affecting other instances of it
            val icon = menuItem.icon.mutate()
            icon.alpha = if (actionUiModel.isEnabled) 255 else 128
            menuItem.icon = icon
        }
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(
        mode: ActionMode,
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.menu_approve -> {
                viewModel.performBatchApprove()
                true
            }
//            R.id.mnu_edit_item -> {
//                viewModel.performEditAction()
//                true
//            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        viewModel.clearSelection()

        lifecycleRegistry.handleLifecycleEvent(ON_STOP)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}

