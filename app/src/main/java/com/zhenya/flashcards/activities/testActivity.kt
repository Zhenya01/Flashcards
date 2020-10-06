package com.zhenya.flashcards.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.zhenya.flashcards.R

class testActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun showAlert(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удаление")
        builder.setMessage("Вы собираетесь удалить модуль! Это удалит все слова, входящие в этот " +
        "модуль. Можно изменить информацию о модуле, нажав на синюю кнопку. Точно хотите удалить?")
        builder.setPositiveButton("Да", DialogInterface.OnClickListener{ dialog, _ ->
            dialog.dismiss()
        })
        builder.setNegativeButton("Нет", DialogInterface.OnClickListener{ dialog, _ ->
            dialog.dismiss()
        })
        builder.setNeutralButton("Отмена", DialogInterface.OnClickListener{ dialog, _ ->
            dialog.dismiss()
        })

        val alertDialog:AlertDialog = builder.create()
        alertDialog.show()
    }




}