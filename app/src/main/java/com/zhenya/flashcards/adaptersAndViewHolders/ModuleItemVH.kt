package com.zhenya.flashcards.adaptersAndViewHolders

import android.content.Context
import android.content.Intent
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import com.zhenya.flashcards.activities.*
import kotlinx.android.synthetic.main.layout_modules_choose.view.*

class ModuleItemVH(var name: String, val description: String, val id:Int, val context: Context, val trType:String, val trName:String) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.layout_modules_choose
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.module_name_text.text = name
        viewHolder.itemView.module_description_text.text = description
        viewHolder.itemView.setOnClickListener {
            when(trType){
                "Обучение" -> {
                    val nextIntent = Intent(context, StudyCardsActivity::class.java)
                    nextIntent.putExtra("Module id", id)
                    nextIntent.putExtra("trName", trName)
                    context.startActivity(nextIntent)
                }
                "Карточки с проверкой" -> {
                    val nextIntent = Intent(context, LearnCheckCardsActivity::class.java)
                    nextIntent.putExtra("Module id", id)
                    nextIntent.putExtra("trName", trName)
                    context.startActivity(nextIntent)
                }
                "Карточки" -> {
                    val nextIntent = Intent(context, LearnCardsActivity::class.java)
                    nextIntent.putExtra("Module id", id)
                    nextIntent.putExtra("trName", trName)
                    context.startActivity(nextIntent)
                }
                "Пары" -> {
                    val nextIntent = Intent(context, LearnPairsActivity::class.java)
                    nextIntent.putExtra("Module id", id)
                    nextIntent.putExtra("trName", trName)
                    context.startActivity(nextIntent)
                }
                "Тест" -> {
                    val nextIntent = Intent(context, LearnTestsActivity::class.java)
                    nextIntent.putExtra("Module id", id)
                    nextIntent.putExtra("trName", trName)
                    context.startActivity(nextIntent)
                }
            }
        }
    }
}