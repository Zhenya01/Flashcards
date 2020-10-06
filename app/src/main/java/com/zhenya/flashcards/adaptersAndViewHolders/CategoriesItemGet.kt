package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.StartActivity
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.others.module
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.layout_categories.view.*

class CategoriesItemGet(var data:List<String>, val category:String, val context: Context) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.layout_categories
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val db = DataBaseHandler(context)
        val words = data[2].split(" * ")
        var existsSQL = "INSERT INTO Characters (hanzi,pinyin,eng) VALUES"
        var absentSQL = "INSERT INTO Characters (hanzi,pinyin,eng) VALUES"
        val checker = "INSERT INTO Characters (hanzi,pinyin,eng) VALUES"
        for(i in words){
            val sections = i.split(" & ")
            val bool = db.findCharacter(sections[0])
            if(bool){
                existsSQL += "('${sections[0]}','${sections[1]}','${sections[2]}'),"
                Log.d("AppCustom", "absent - $absentSQL")
            }
            else {
                absentSQL += "('${sections[0]}','${sections[1]}','${sections[2]}'),"
                Log.d("AppCustom", "exist - $existsSQL")
            }
        }
        if(absentSQL.takeLast(1) == ",") absentSQL = absentSQL.substring(0, absentSQL.length - 1)
        if(existsSQL.takeLast(1) == ",") existsSQL = existsSQL.substring(0, existsSQL.length - 1)

        module = data[1]
        viewHolder.itemView.categoryTV.text = category
        viewHolder.itemView.delBtnCategories.visibility = View.GONE
        viewHolder.itemView.frontView.setOnClickListener {
            if (db.findModule(module, category)) {
                showDialog(context, existsSQL, absentSQL, checker)
            } else {
                insertModule(existsSQL, absentSQL, true, checker)
            }
        }
    }
    fun showDialog(context: Context, existSQL:String, absentSQL:String, checker: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_insert_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val delOldBtn = dialog.findViewById<Button>(R.id.delOldBtn)
        val leaveBothBtn = dialog.findViewById<Button>(R.id.leaveBothBtn)
        val mergeBtn = dialog.findViewById<Button>(R.id.mergeBtn)


        delOldBtn.setOnClickListener {
            DataBaseHandler(context).deleteDataModules(Modules(module, data[3], category))
            insertModule(existSQL, absentSQL, true, checker)
            dialog.dismiss()
        }

        leaveBothBtn.setOnClickListener {
            showDialogName(existSQL, absentSQL, checker)
            dialog.dismiss()
        }
        mergeBtn.setOnClickListener {
            insertModule(existSQL, absentSQL, false, checker)
            dialog.dismiss()
        }
        dialog.show()
    }
    fun insertModule(existSQL:String, absentSQL:String, doExists:Boolean, checker:String){
        val db = DataBaseHandler(context)
        if(absentSQL != checker) db.runQuery(absentSQL)
        if(doExists && existSQL != checker) db.runQuery(existSQL)
        db.runQuery("UPDATE Characters SET module = '$module', category = '$category' WHERE category IS NULL AND module IS NULL")
        db.insertDataModule(
            Modules(
                name = module,
                description = data[3],
                category = category
            )
        )
        Toast.makeText(context, "Модуль добавлен", Toast.LENGTH_SHORT).show()
        val nextIntent = Intent(context, StartActivity::class.java)
        context.startActivity(nextIntent)
    }
    private fun showDialogName(existSQL:String, absentSQL:String, checker: String) {
        val dialog = Dialog(this.context)
        val db = DataBaseHandler(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_name_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val changeBtn = dialog.findViewById(R.id.changeNameBtn) as Button
        val columnEnterModuleName = dialog.findViewById(R.id.columnEnterModuleName) as TextView

        changeBtn.setOnClickListener {
            if (columnEnterModuleName.text.toString().isNotEmpty()) {
                if(!db.findModule(columnEnterModuleName.text.toString(), category)){
                    module = columnEnterModuleName.text.toString()
                    insertModule(existSQL, absentSQL, true, checker)
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(context, "Название уже занято (есть модуль с таким названием)", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Заполните поле", Toast.LENGTH_LONG).show()
            }

        }

        dialog.show()
    }
}