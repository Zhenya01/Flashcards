package com.zhenya.flashcards.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_modules_add.*


class ModuleAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_add)
        addModuleButton.setOnClickListener(View.OnClickListener {
            if (addModule(it)) {
                val nextIntent = Intent(this, CharacterAddActivity::class.java)
                nextIntent.putExtra("module",inputModuleName.text.toString())
                nextIntent.putExtra("category", intent?.extras?.getString("Category").toString())
                startActivity(nextIntent)

            }
        })
    }

    override fun onBackPressed() {
        val nextIntent = Intent(this, ModulesViewActivity::class.java)
        startActivity(nextIntent)
    }



    private val context = this

    var db = DataBaseHandler(context)

    fun addModule(view: View): Boolean {
        if (inputModuleName.text.toString().isNotEmpty() && inputDescription.text.toString()
                .isNotEmpty()
        ) {
            var module = Modules(
                name = inputModuleName.text.toString(),
                description = inputDescription.text.toString(),
                category = intent?.extras?.getString("Category").toString()
            )
            if (!db.insertDataModule(module)) {
                return false
            }
            return true
        } else {
            Toast.makeText(context, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}