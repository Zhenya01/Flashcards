package com.zhenya.flashcards.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhenya.flashcards.adaptersAndViewHolders.CategoriesItem
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_categories_list.*


class CategoriesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val uri= intent?.data?.path?.toUri()
        Log.d("AppCustom","$uri")
        var bitmap: Bitmap? = null
        try {
            // Works with content://, file://, or android.resource:// URIs
            val inputStream: InputStream? = uri?.let { contentResolver.openInputStream(it) }
            val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
            Log.d("AppCustom","$inputAsString, ${inputAsString?.length}")
            Toast.makeText(this, inputAsString, Toast.LENGTH_LONG).show()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File doesn't exist", Toast.LENGTH_SHORT).show()
            // Inform the user that things have gone horribly wrong
        }*/

        //Toast.makeText(this, file.readText(),Toast.LENGTH_LONG).show()
        /*Toast.makeText(this, intent?.scheme.toString(),Toast.LENGTH_LONG).show()*/
        setContentView(R.layout.activity_categories_list)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val list = DataBaseHandler(this).readDataCategories()
        if (!list.contains("false")) {
            list.forEach { adapter.add(CategoriesItem(it, this, adapter)) }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        categoriesView.adapter = adapter
        categoriesView.layoutManager = LinearLayoutManager(this)
        addFAB_categories.setOnClickListener { showDialogCreate(adapter) }
    }

    fun showDialogCreate(adapter: GroupAdapter<GroupieViewHolder>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_edit_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val columnContent = dialog.findViewById<EditText>(R.id.ColumnContent)
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        yesBtn.text = "Добавить"
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        yesBtn.setOnClickListener {
            val db = DataBaseHandler(this)
            if (columnContent.text.toString().isNotEmpty()) {
                if(DataBaseHandler(this).insertDataCategories(columnContent.text.toString())){
                    adapter.add(CategoriesItem(columnContent.text.toString(), this, adapter))
                }
                else{Toast.makeText(this, "Такая категория уже существует", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Заполните поле", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    override fun onBackPressed() {
        val nextIntent = Intent(this, ModulesViewActivity::class.java)
        startActivity(nextIntent)
    }

}





