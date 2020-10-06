package com.zhenya.flashcards.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.adaptersAndViewHolders.ModulesAdapter
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_modules_list.*


class ModulesListActivity : AppCompatActivity() {
    private lateinit var cat: MutableList<String>
    private lateinit var spinner: Spinner
    private fun getModulesData(): MutableList<Modules>? {
        val db = DataBaseHandler(this)
        val el = db.readDataCategories()
        if (el.isEmpty()) {
            val nextIntent = Intent(this, CategoriesListActivity::class.java)
            startActivity(nextIntent)
            return null
        }
        val modulesList = db.readDataModules(el[0])
        modulesList.sortBy { it.name }
        return modulesList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_list)
        val db = DataBaseHandler(this)
        /*val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
*/
        val el = db.readDataCategories()
        if (el.isEmpty()) {
            val nextIntent = Intent(this, CategoriesListActivity::class.java)
            startActivity(nextIntent)
            finish()
        }

        modulesView1.layoutManager = LinearLayoutManager(this)
        modulesView1.adapter = ModulesAdapter(this, getModulesData())
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        spinner = findViewById<Spinner>(R.id.category_spinner)
        cat = DataBaseHandler(this).readDataCategories()
        cat.add(0, "Редактировать")
        val arrayadapter = ArrayAdapter(this, R.layout.layout_spinner_item, cat)
        spinner.adapter = arrayadapter
        spinner.setSelection(1)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "Редактировать") {
                    val nextIntent = Intent(
                        this@ModulesListActivity,
                        CategoriesListActivity::class.java
                    )
                    startActivity(nextIntent)
                    Toast.makeText(
                        this@ModulesListActivity,
                        "Пожалуйста заполните все поля",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ModulesListActivity,
                        "$selectedItem selected",
                        Toast.LENGTH_SHORT
                    ).show()
                    val newList =
                        DataBaseHandler(this@ModulesListActivity).readDataModules(selectedItem)
                    (modulesView1.adapter!! as ModulesAdapter).updateList(newList)

                }
            }
        }
    }

    /*fun showDialogCreate(spinner: Spinner){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_edit_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val columnContent = dialog.findViewById<EditText>(R.id.ColumnContent)
        val yesBtn = dialog.findViewById<Button>(R.id.yesBtn)
        yesBtn.text = "Добавить"
        val noBtn = dialog.findViewById<Button>(R.id.noBtn)
        yesBtn.setOnClickListener {
            val db = DataBaseHandler(this)
            if (columnContent.text.toString().isNotEmpty()) {
                if (DataBaseHandler(this).insertDataCategories(columnContent.text.toString())) {
                    cat.plus(columnContent.text.toString())
                    val arrayadapter = ArrayAdapter(this, R.layout.layout_spinner_item, cat)
                    spinner.adapter = arrayadapter
                    val content = columnContent.text.toString()
                    spinner.setSelection(cat.indexOf(content))
                    dialog.dismiss()

                } else {
                    Toast.makeText(this, "Такая категория уже существует", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Заполните поле", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }*/


    override fun onResume() {
        super.onResume()
        val db = DataBaseHandler(this)
        val el = db.readDataCategories()
        if(el.isEmpty()){
            val nextIntent = Intent(this, CategoriesListActivity::class.java)
            startActivity(nextIntent)
            finish()
        }
        val modulesData = getModulesData()

        if(modulesData?.size == 0) {
            noModulesText.visibility = View.VISIBLE
        }else{
            noModulesText.visibility = View.INVISIBLE
        }
        (modulesView1.adapter!! as ModulesAdapter).updateList(modulesData)
        cat = DataBaseHandler(this).readDataCategories()
        cat.add(0, "Редактировать")
        spinner.adapter = ArrayAdapter(this, R.layout.layout_spinner_item, cat)
        spinner.setSelection(spinner.count - 1)
    }


    fun addModule(view: View){
        val nextIntent = Intent(this, ModuleAddActivity::class.java)
        nextIntent.putExtra("Category", spinner.selectedItem.toString())
        startActivity(nextIntent)
        Log.i("MyApp", "goAddModule function worked")

    }



}