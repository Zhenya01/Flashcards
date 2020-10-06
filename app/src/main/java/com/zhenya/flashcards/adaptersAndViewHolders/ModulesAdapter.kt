package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.CharactersListActivity
import com.zhenya.flashcards.activities.ModulesListActivity
import com.zhenya.flashcards.activities.ModulesUpdateActivity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_modules_list.*
import kotlinx.android.synthetic.main.layout_modules.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ModulesAdapter(val context: Context, var modulesList: MutableList<Modules>?): RecyclerView.Adapter<ModulesAdapter.ModulesViewHolder>(){

    private var viewBinderHelper:ViewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }



    override fun getItemCount(): Int {
        return modulesList?.size!!.toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModulesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.layout_modules, parent, false)
        return ModulesViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: ModulesViewHolder, position: Int) {
        val id = modulesList!![position].id
        val name = modulesList!![position].name
        val description = modulesList!![position].description
        val category = modulesList!![position].category

        viewBinderHelper.bind(holder.swipeLayout, id.toString())
        holder.bind(context, id, name, description, position, category)
        // TODO Объект модуля вместо 4 параметров
    }

    fun updateList(newList: MutableList<Modules>?) {
        if (newList != null) {
            newList.sortBy { it.name }
        }
        modulesList = newList
        notifyDataSetChanged()
    }

    inner class ModulesViewHolder(view: View): RecyclerView.ViewHolder(view){
        var swipeLayout:SwipeRevealLayout = view.swipelayout
        private var frontview:FrameLayout = view.frontview
        private var share = view.shareImg
        private var edit = view.editImg
        private var del = view.delImg
        private var nametext = view.name_text
        private var descriptiontext = view.description_text

        private fun showAlert(
            position: Int,
            view: View,
            id: Int,
            name: String,
            description: String,
            category: String
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Удаление")
            builder.setMessage(
                "Данное действие приведет к удалению модуля и всех слов, входящих в него. Точно хотите удалить?"
            )
            builder.setPositiveButton("Да", DialogInterface.OnClickListener { dialog, _ ->
                deleteModule(position, id, name, description, category)
            })
            builder.setNegativeButton("Нет", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            builder.setNeutralButton("Отмена", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            // TODO Размер
            val alertDialog:AlertDialog = builder.create()
            alertDialog.show()
        }
        fun deleteModule(
            position: Int,
            id: Int,
            name: String,
            description: String,
            category: String
        ){

            DataBaseHandler(context).deleteDataModules(Modules(id, name, description))
            //modulesList!!.removeAt(position)
            modulesList = DataBaseHandler(context).readDataModules(category)
            val noModulesText = (context as ModulesListActivity).noModulesText
            if(modulesList!!.size == 0) {
                noModulesText.visibility = View.VISIBLE
            }else{
                noModulesText.visibility = View.INVISIBLE
            }
            notifyItemRemoved(adapterPosition)
            //notifyDataSetChanged()
            val displayText = "$name - $description deleted"
            Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
            Log.d("RecyclerAdapter", displayText)
        }

        fun bind(
            context: Context,
            id: Int,
            name: String,
            description: String,
            position: Int,
            category: String
        ) {
            share.setOnClickListener(View.OnClickListener {
                createFile(id)
            })
            del.setOnClickListener(View.OnClickListener {
                showAlert(position,it,id,name,description,category)
            })
            edit.setOnClickListener(View.OnClickListener {
                val displayText = "$name - $description edited"
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
                val nextIntent = Intent(context, ModulesUpdateActivity::class.java)
                var b = Bundle()
                b.putInt("id", id)
                b.putString("name", nametext.text.toString())
                b.putString("description", descriptiontext.text.toString())
                nextIntent.putExtras(b)
                context.startActivity(nextIntent)
            })
            nametext.text = name
            descriptiontext.text = description

            frontview.setOnClickListener(View.OnClickListener {
                val displayText = "$name - $description clicked"
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
                val nextIntent = Intent(context, CharactersListActivity::class.java)
                nextIntent.putExtra("module", name)
                nextIntent.putExtra("moduleID", id)
                nextIntent.putExtra("category", category)
                context.startActivity(nextIntent)
            })

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

                    fos = context.openFileOutput("module.fc", MODE_PRIVATE)
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


}
