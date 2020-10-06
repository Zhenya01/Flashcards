package com.zhenya.flashcards.adaptersAndViewHolders

import android.graphics.Color
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.layout_training_header_item.view.*

class HeaderItemVH(val bgcolor:String, val category:String) : Item<GroupieViewHolder>(){
    override fun getLayout() = R.layout.layout_training_header_item
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.trainingTypeCV.setBackgroundColor(Color.parseColor(bgcolor.toString()))
        viewHolder.itemView.trainingTypeTV.text = category
    }
}
