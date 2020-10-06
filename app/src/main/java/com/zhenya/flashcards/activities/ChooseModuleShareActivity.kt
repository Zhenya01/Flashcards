package com.zhenya.flashcards.activities

import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import com.zhenya.flashcards.adaptersAndViewHolders.CategoriesItemGet
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_choose_words.*
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class ChooseModuleShareActivity : AppCompatActivity() {
    fun importData(data: Uri): List<String> {
        val scheme = data.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme) {
            try {
                val cr = this.contentResolver
                val ins = cr.openInputStream(data) ?: return emptyList()
                var buf = listOf<String>()
                val reader = BufferedReader(InputStreamReader(ins))
                var str: String?
                while (reader.readLine().also { str = it } != null) {
                    buf = buf.plus(str.toString())
                    Log.d("AppCustom", "str - $str")
                }
                ins.close()
                Log.d("AppCustom", "$buf")
                return buf
                // perform your data import here…

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return emptyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_words)
        val data = intent.data
        if (data != null) {
            intent.data = null
            val dataList = importData(data)
            try {
                if (dataList.isEmpty() || dataList[0] != "This is Flashcards Zhenya app file") {
                    Toast.makeText(this, "Неверный или пустой файл. Попробуйте другой файл", Toast.LENGTH_LONG).show()
                    val nextIntent = Intent(this, StartActivity::class.java)
                    startActivity(nextIntent)
                    this.finish()
                }


            } catch (e: Exception) {
                Toast.makeText(this, "Bad data", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            val adapter = GroupAdapter<GroupieViewHolder>()
            wordChooseList.adapter = adapter
            wordChooseList.layoutManager = LinearLayoutManager(this)
            val db = DataBaseHandler(this)
            val categories = db.readDataCategories()
            for (i in categories) {
                adapter.add(CategoriesItemGet(dataList, i,this))
            }
        }

    }
}