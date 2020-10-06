package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.ChooseWordsActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.layout_training_type.view.*

class TrainingItem(var name: String, val description: String, val bgcolor: String, val type: String, val context: Context) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.layout_training_type
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.studyCatTV.text = name
        viewHolder.itemView.studyCatTV.setBackgroundColor(Color.parseColor(bgcolor))
        viewHolder.itemView.studyDescTV.text = description
        viewHolder.itemView.setOnClickListener {
            val nextIntent = Intent(context, ChooseWordsActivity::class.java)
            nextIntent.putExtra("Training type", type)
            nextIntent.putExtra("Training name", name)
            context.startActivity(nextIntent)
        }
    }

    override fun getSpanSize(spanCount: Int, position: Int) = (spanCount/3)
}