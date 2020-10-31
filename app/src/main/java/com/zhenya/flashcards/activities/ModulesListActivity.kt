package com.zhenya.flashcards.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhenya.flashcards.BuildConfig
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.adaptersAndViewHolders.ModulesAdapter
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_modules_list.*
import kotlinx.android.synthetic.main.layout_camera_dialog.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ModulesListActivity : AppCompatActivity() {
    private lateinit var cat: MutableList<String>
    lateinit var categoryNext: String
    private lateinit var spinner: Spinner
    lateinit var currentPhotoPath: String
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
            }

            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.data
            }
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {

                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    // If there is error while creating the File, it will be null
                    null
                }

                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    intent.putExtra("path", currentPhotoPath)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("PHOTO_${timestamp}",".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }
    fun showDialog(category:String) {
        categoryNext = category
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_camera_dialog)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.photoBtn.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        dialog.galleryBtn.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
        dialog.show()

    }



}