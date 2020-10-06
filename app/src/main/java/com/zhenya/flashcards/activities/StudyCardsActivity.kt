package com.zhenya.flashcards.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_study_cards.*
import kotlinx.android.synthetic.main.layout_card_back.*
import kotlinx.android.synthetic.main.layout_card_back.view.*
import kotlinx.android.synthetic.main.layout_card_front.*

class StudyCardsActivity : AppCompatActivity() {
    private lateinit var trName: String
    private lateinit var wordList: MutableList<Character>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_cards)
        flipCardLearn.isAutoFlipBack = false
        flipCardLearn.hanziBack.visibility = View.GONE

        trName = intent.extras!!.getString("trName").toString().toLowerCase()
        wordList = DataBaseHandler(this).readDataCharacters(intent.extras!!.getInt("Module id"))
        var questions = genCardsStudy(wordList,trName)
        elementFront.text = questions[0]
        pinYinBack.text = questions[1]
        translationBack.text = questions[2]
        wordList.removeAt(0)


        Log.d("AppCustom", "${wordList.size}")
        if (wordList.isEmpty()) {
            Toast.makeText(
                this,
                "В этом модуле нет иероглифов. Добавьте иероглифы или выберите другой модуль",
                Toast.LENGTH_LONG
            ).show()
            this.finish()
        } else {
            flipCardLearn.flipTheView()
            nextFAB.setOnClickListener {
                if(wordList.isNotEmpty()) {
                    questions = genCardsStudy(wordList, trName)
                    wordList.removeAt(0)
                    if (flipCardLearn.isFrontSide) {
                        pinYinBack.text = questions[1]
                        translationBack.text = questions[2]
                        flipCardLearn.flipTheView()
                        elementFront.text = questions[0]
                    }
                    else{
                        elementFront.text = questions[0]
                        pinYinBack.text = questions[1]
                        translationBack.text = questions[2]
                    }
                }
                else{
                    pinYinBack.visibility = View.GONE
                    translationBack.text = "Программа завершена"
                    nextFAB.visibility = View.GONE
                    exitFAB.visibility = View.VISIBLE
                    flipCardLearn.isFlipEnabled = false

                }
            }
            exitFAB.setOnClickListener {
                val nextIntent = Intent(this, StartActivity::class.java)
                startActivity(nextIntent)
            }

        }
    }
    private fun genCardsStudy(wordsList: MutableList<Character>, trName: String): MutableList<String> {
        val questions =  mutableListOf("","","")

        when (trName) {
            "иероглиф" -> {
                questions[0] = wordsList[0].hanzi.trim()
                questions[1] = wordsList[0].pinyin
                questions[2] = wordsList[0].eng

            }
            "транскрипция" -> {
                questions[0] = wordsList[0].pinyin.trim().toLowerCase()
                questions[1] = wordsList[0].hanzi
                questions[2] = wordsList[0].eng
            }
            "перевод" -> {
                questions[0] = wordsList[0].eng.trim().toLowerCase()
                questions[1] = wordsList[0].hanzi
                questions[2] = wordsList[0].pinyin
            }
        }

        return questions
    }
}