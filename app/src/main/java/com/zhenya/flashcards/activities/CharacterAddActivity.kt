package com.zhenya.flashcards.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.ChineseHandler
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_character_add.*

class CharacterAddActivity : AppCompatActivity() {
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val module = intent.extras?.getString("module").toString()
        setContentView(R.layout.activity_character_add)
        this.title = module

        inputPinYin.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                try {
                    Log.i("MyApp", "last letter is ${s.length - 1}")
                    if (s.last().toString() == "v") {
                        val string = "${s.slice(0 until (s.length - 1))}ü"
                        inputPinYin.setText(string)
                        inputPinYin.setSelection(s.length)
                    }
                } catch (e:NoSuchElementException){}
            }
        })
        addCharacterButton?.setOnClickListener(View.OnClickListener {
            if (addWord(it, module)) {
                inputCharacter.setText("")
                inputPinYin.setText("")
                inputTranslation.setText("")
                position = 0
            }
        })

        finishButton?.setOnClickListener(View.OnClickListener {
                val nextIntent = Intent(this, CharactersListActivity::class.java)
                nextIntent.putExtra("module", module)
                startActivity(nextIntent)
        })
    }
    override fun onBackPressed() {
        val nextIntent = Intent(this, CharactersListActivity::class.java)
        startActivity(nextIntent)
    }

    private val context = this

    var db = DataBaseHandler(context)

    fun addWord(view: View, module:String): Boolean {
        if (inputCharacter.text.toString().isNotEmpty() && inputPinYin.text.toString().isNotEmpty()
            && inputTranslation.text.toString().isNotEmpty()) {
            var character = Character(
                hanzi = inputCharacter.text.toString(),
                pinyin = inputPinYin.text.toString(),
                eng = inputTranslation.text.toString(),
                module = module,
                category = intent.extras?.get("category") as String
            )
            if (!db.insertDataCharacter(character)) {
                return false
            }
            return true
        } else {
            Toast.makeText(context, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    fun toneButtonClicked(view: View){
        var string = inputPinYin.text.toString()
        if(string.isNotEmpty()) {
            string = string.slice(position until string.length)
            when (view.id) {
                R.id.toneButton1 -> {
                    string += "1"
                }
                R.id.toneButton2 -> {
                    string += "2"
                }
                R.id.toneButton3 -> {
                    string += "3"
                }
                R.id.toneButton4 -> {
                    string += "4"
                }
                R.id.toneButton5 -> {
                    string += "0"
                }
            }
        }
        string = inputPinYin.text.slice(0 until position).toString() + ChineseHandler().toPinyin(
            string
        )
        inputPinYin.setText(string)
        inputPinYin.setSelection(string.length)
    }
}