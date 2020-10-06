package com.zhenya.flashcards.adaptersAndViewHolders

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.layout_categories.view.*

class CategoriesItem(
    var category: String,
    val context: Context,
    val adapter: GroupAdapter<GroupieViewHolder>
) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.layout_categories
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val categoryTV = viewHolder.itemView.categoryTV
        viewHolder.itemView.categoryTV.text = category
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(this.context, "$category clicked", Toast.LENGTH_SHORT).show()
            showDialogEdit(this.category, categoryTV, this) }
        viewHolder.itemView.delBtnCategories.setOnClickListener {
            showDialogDelete(this)
        }
    }
    fun showDialogDelete(item: CategoriesItem){
        val dialog = Dialog(this.context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog_delete)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        yesBtn.setOnClickListener {
            val db = DataBaseHandler(this.context)
            adapter.remove(item)
            db.deleteDataCategories(category)
            dialog.dismiss()

        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showDialogEdit(content: String, categoryTV: TextView, item: CategoriesItem) {
        val dialog = Dialog(this.context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_edit_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val columnContent = dialog.findViewById<EditText>(R.id.ColumnContent)
        columnContent.setText(content)
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        yesBtn.setOnClickListener {
            val db = DataBaseHandler(this.context)
            if (columnContent.text.toString().isNotEmpty()) {
                categoryTV.text = columnContent.text
                val cat = item.category
                item.category = columnContent.text.toString()
                db.editDataCategories(cat)
            } else {
                Toast.makeText(this.context, "Заполните поле", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}