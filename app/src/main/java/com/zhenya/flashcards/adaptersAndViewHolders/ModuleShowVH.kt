package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.CharactersListActivity
import com.zhenya.flashcards.activities.ModulesUpdateActivity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.xwray.groupie.*
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.layout_modules.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ModuleShowVH(var module: Modules, val context: Context, var adapter: GroupAdapter<GroupieViewHolder>, val section: Section): Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.layout_modules
    private var viewBinderHelper:ViewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val share = viewHolder.itemView.shareImg
        val del = viewHolder.itemView.delImg
        val edit = viewHolder.itemView.editImg
        val frontView = viewHolder.itemView.frontview
        viewHolder.itemView.name_text .text = module.name

        viewHolder.itemView.description_text.text = module.description
        share.setOnClickListener{
            createFile(module.id)

        }
        del.setOnClickListener{
            showDialogDelete()
        }

        edit.setOnClickListener {
            val displayText = "${module.name} - ${module.description} edited"
            Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
            Log.d("RecyclerAdapter", displayText)
            val nextIntent = Intent(context, ModulesUpdateActivity::class.java)
            var b = Bundle()
            b.putInt("id", module.id)
            b.putString("name", module.name)
            b.putString("description", module.description)
            nextIntent.putExtras(b)
            context.startActivity(nextIntent)
        }
        frontView.setOnClickListener(View.OnClickListener {
            val displayText = "${module.name} - ${module.description} clicked"
            Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
            Log.d("RecyclerAdapter", displayText)
            val nextIntent = Intent(context, CharactersListActivity::class.java)
            nextIntent.putExtra("module", module.name)
            nextIntent.putExtra("moduleID", module.id)
            nextIntent.putExtra("category", module.category)
            context.startActivity(nextIntent)
        })
    }
    private fun showAlert(item: Item<GroupieViewHolder>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Удаление")
        builder.setMessage(
            "Данное действие приведет к удалению модуля и всех слов, входящих в него. Точно хотите удалить?")
        builder.setPositiveButton("Да") { _, _ ->
            deleteModule(item)
        }
        builder.setNegativeButton("Нет") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        // TODO Размер
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    fun showDialogDelete(){
        val dialog = Dialog(this.context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog_delete)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        yesBtn.setOnClickListener {
            val db = DataBaseHandler(this.context)
            deleteModule(this)
            dialog.dismiss()

        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteModule(item: Item<GroupieViewHolder>){
        DataBaseHandler(context).deleteDataModules(module)
        section.remove(this)

    }
    fun shareFile(context: Context,name: String) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val file = File(context.filesDir.toString() + "/" + "module.fc")
        Toast.makeText(context, context.filesDir.toString() + "/" + "module.fc", Toast.LENGTH_LONG).show()

        if (file.exists()) {
            Toast.makeText(context, file.extension, Toast.LENGTH_SHORT).show()
            intentShareFile.type = "text/plain"
            Toast.makeText(context,intentShareFile.scheme,Toast.LENGTH_LONG).show()
            intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context.applicationContext, context.packageName +".provider", file))
            intentShareFile.putExtra(
                Intent.EXTRA_SUBJECT,
                name
            )
            intentShareFile.putExtra(Intent.EXTRA_TEXT, name)
            context.startActivity(Intent.createChooser(intentShareFile, name))
        }
    }
    fun createFile(id: Int) {
        val db = DataBaseHandler(context)
        val list = db.readDataCharacters(id)
        if (list.isNotEmpty()){
            val module = db.moduleByID(id)
            var text: String = "This is Flashcards Zhenya app file\n${module.name}\n"
            for(i in list){
                text += "${i.hanzi} & ${i.pinyin} & ${i.eng} * "
            }
            text = text.substring(0, text.length - 2)
            text += "\n${module.description}"

            var fos: FileOutputStream? = null
            try {
                Log.d("AppCustom",context.filesDir.toString())

                fos = context.openFileOutput("module.fc", Context.MODE_PRIVATE)
                fos.write(text.toByteArray())
                Toast.makeText(
                    context, "Saved to " + context.filesDir.toString() + "/" + "module.fc",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            shareFile(context, module.name)
        }
        else{
            Toast.makeText(context,"Нельзя отправить пустой модуль", Toast.LENGTH_SHORT).show()
        }
    }

}