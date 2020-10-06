package com.zhenya.flashcards.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_modules_add.*


class ModulesUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules_add)

        val id = intent.extras?.getInt("id")!!.toInt()
        val name = intent.extras?.getString("name").toString()
        val description = intent.extras?.getString("description").toString()
        inputModuleName.setText(name)
        inputDescription.setText(description)
        addModuleButton.text = "Изменить"



        addModuleButton.setOnClickListener {
            val module = Modules(id, inputModuleName.text.toString(), inputDescription.text.toString())
            val db = DataBaseHandler(this)
            if (inputModuleName.text.toString().isNotEmpty() && inputDescription.text.toString().isNotEmpty()){
                db.editDataModules(module)
                val nextIntent = Intent(this, ModulesViewActivity::class.java)
                startActivity(nextIntent)
            }else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
            }
        }

    }
    override fun onBackPressed() {
        val nextIntent = Intent(this, ModulesViewActivity::class.java)
        startActivity(nextIntent)
    }
}