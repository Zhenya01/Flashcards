package com.zhenya.flashcards.activities

import com.zhenya.flashcards.adaptersAndViewHolders.CategoriesHeaderVH
import com.zhenya.flashcards.adaptersAndViewHolders.ModuleShowVH
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.zhenya.flashcards.*
import kotlinx.android.synthetic.main.activity_modules_view.*
import kotlinx.android.synthetic.main.layout_camera_dialog.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ModulesViewActivity : AppCompatActivity() {
    lateinit var uri: Uri
    lateinit var categoryNext: String
    lateinit var currentPhotoPath: String
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_view)
        val adapter = GroupAdapter<GroupieViewHolder>()
        addFAB.setOnClickListener {
            showDialogCreate(adapter)
        }
        modulesView.adapter = adapter

        modulesView.layoutManager = LinearLayoutManager(this)
        val db = DataBaseHandler(this)
        val categories = db.readDataCategories()
        val modulesMap: MutableMap<Int, List<Modules>> = mutableMapOf()
        for(i in 0 until categories.size) {
            val set = db.readDataModules(categories[i])
            if (set.size > 0) {
                modulesMap[i] = emptyList()
                for (j in set) {
                    modulesMap[i] = modulesMap[i]!! + j
                    Log.d("AppCustom", "${modulesMap.size}, $modulesMap")
                }
            }
        }
        for(i in 0 until (categories.size)) {
            ExpandableGroup(CategoriesHeaderVH(categories[i], true, this), false).apply {
                add(Section().apply {
                    addAll(genItemList(modulesMap[i],adapter,this@apply))
                })
                adapter.add(this)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.editCategories).isVisible = true
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.editCategories) {
            val nextIntent = Intent(this, CategoriesListActivity::class.java)
            startActivity(nextIntent)
            Toast.makeText(this, "Edit Categories Clicked", Toast.LENGTH_LONG).show()
            return true
        }

        return super.onOptionsItemSelected(item)

    }
    private fun showDialogCreate(adapter: GroupAdapter<GroupieViewHolder>) {
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
                if(db.insertDataCategories(columnContent.text.toString())){
                    adapter.add(ExpandableGroup(CategoriesHeaderVH(columnContent.text.toString(), true, this)))
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
    private fun genItemList(listModules:List<Modules>?, adapter: GroupAdapter<GroupieViewHolder>, section: Section):List<ModuleShowVH> {
        var list:List<ModuleShowVH> = emptyList()

        if (!listModules.isNullOrEmpty()) {
            for (i in listModules) {
                list = list + ModuleShowVH(i,this, adapter,section)
            }
        }

        Log.d("AppCustom","$list")
        return list
    }
    override fun onBackPressed() {
        val nextIntent = Intent(this, StartActivity::class.java)
        startActivity(nextIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                uri = Uri.parse(currentPhotoPath)
            }

            else if (requestCode == REQUEST_PICK_IMAGE) {
                uri = data?.data!!

            }
            val nextIntent = Intent(this, CameraActivity::class.java)
            nextIntent.putExtra("fileUri", uri.toString())
            startActivity(nextIntent)
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
                Log.d("AppCustom", photoFile.toString())
                photoFile?.absolutePath?.let { it1 -> Log.d("AppCustom", it1) }

                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
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