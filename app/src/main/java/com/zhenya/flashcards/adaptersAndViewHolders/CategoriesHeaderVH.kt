package com.zhenya.flashcards.adaptersAndViewHolders

import android.Manifest
import android.app.Activity
import com.zhenya.flashcards.activities.ModuleAddActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import com.zhenya.flashcards.activities.ModulesListActivity
import com.zhenya.flashcards.activities.ModulesViewActivity
import kotlinx.android.synthetic.main.layout_categories_header.view.*

class CategoriesHeaderVH(val catName: String, val isShow: Boolean, val context: Context): Item<GroupieViewHolder>(), ExpandableItem {

    private val REQUEST_PERMISSION = 100

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2


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
                checkCameraPermission()
                (context as ModulesViewActivity).showDialog(catName)
            }
        }
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }

}