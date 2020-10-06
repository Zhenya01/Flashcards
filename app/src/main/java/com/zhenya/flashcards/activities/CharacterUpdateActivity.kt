package com.zhenya.flashcards.activities
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.ChineseHandler
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_character_add.*


class CharacterUpdateActivity : AppCompatActivity() {
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_add)
        val id = intent.extras?.getInt("id")!!.toInt()
        val hanzi = intent.extras?.getString("hanzi").toString()
        val pinyin = intent.extras?.getString("pinyin").toString()
        val eng = intent.extras?.getString("eng").toString()
        val module = intent.extras?.getString("module").toString()

        this.title = module
        inputCharacter.setText(hanzi)
        inputPinYin.setText(pinyin)
        inputTranslation.setText(eng)
        addCharacterButton.text = "Изменить"
        finishButton.visibility = View.GONE

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


        addCharacterButton.setOnClickListener {
            val character = Character(id, inputCharacter.text.toString(), inputPinYin.text.toString(), inputTranslation.text.toString(), module, "")
            val db = DataBaseHandler(this)
            if (inputCharacter.text.toString().isNotEmpty() && inputPinYin.text.toString().isNotEmpty() && inputTranslation.text.toString().isNotEmpty()) {
                db.editDataCharacters(character)
                val nextIntent = Intent(this, CharactersListActivity::class.java)
                nextIntent.putExtra("module", module)
                startActivity(nextIntent)
            }else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun toneButtonClicked(view: View) {
        var string = inputPinYin.text.toString()
        if (string.isNotEmpty()) {
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