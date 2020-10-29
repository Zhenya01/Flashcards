package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.ModuleAddActivity
import android.content.Context
import android.content.Intent
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import com.zhenya.flashcards.activities.CameraActivity
import kotlinx.android.synthetic.main.layout_categories_header.view.*

class CategoriesHeaderVH(private val catName: String, val isShow: Boolean, val context: Context): Item<GroupieViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout() = R.layout.layout_categories_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getRotatedIconResId() =
        if (expandableGroup.isExpanded)
            R.drawable.ic_baseline_keyboard_arrow_up_24
        else
            R.drawable.ic_baseline_keyboard_arrow_down_24

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.expandable_category_title.text = catName
        viewHolder.itemView.item_expandable_category_icon.setImageResource(getRotatedIconResId())

        viewHolder.itemView.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.item_expandable_category_icon.setImageResource(getRotatedIconResId())
        }
        if(isShow){
            viewHolder.itemView.addModuleBtn.visibility = View.VISIBLE
            viewHolder.itemView.addByPhotoBtn.visibility = View.VISIBLE
            viewHolder.itemView.addModuleBtn.setOnClickListener {
                val nextIntent = Intent(context, ModuleAddActivity::class.java)
                nextIntent.putExtra("Category", catName)
                context.startActivity(nextIntent)
            }
            viewHolder.itemView.addByPhotoBtn.setOnClickListener {
                val nextIntent = Intent(context, CameraActivity::class.java)
                nextIntent.putExtra("Category", catName)
                context.startActivity(nextIntent)
            }
        }
    }
}