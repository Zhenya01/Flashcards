package com.zhenya.flashcards.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wajahatkarim3.easyflipview.EasyFlipView.OnFlipAnimationListener
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_learn_cards.*
import kotlinx.android.synthetic.main.activity_learn_check_cards.*
import kotlinx.android.synthetic.main.layout_card_back.*
import kotlinx.android.synthetic.main.layout_card_back.view.*
import kotlinx.android.synthetic.main.layout_card_front.*
import kotlinx.android.synthetic.main.layout_card_front.view.*


class LearnCardsActivity : AppCompatActivity() {
    lateinit var trName: String
    lateinit var wordList: MutableList<Character>
    lateinit var question: String
    var right = 0
    var wrong = 0
    var isLast = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trName = intent.extras!!.getString("trName").toString().toLowerCase()
        wordList = DataBaseHandler(this).readDataCharacters(intent.extras!!.getInt("Module id"))

        Log.d("AppCustom", "${wordList.size}")
        if (wordList.isEmpty()) {
            Toast.makeText(
                this,
                "В этом модуле нет иероглифов. Добавьте иероглифы или выберите другой модуль",
                Toast.LENGTH_LONG
            ).show()
            this.finish()
        } else {
            setContentView(R.layout.activity_learn_cards)
            wordList.shuffle()
            question = genCards(wordList, trName)
            flipCardCards.apply {
                elementFront.text = question
                hanziBack.text = wordList[0].hanzi
                pinYinBack.text = wordList[0].pinyin
                translationBack.text = wordList[0].eng
            }
            wordList.removeAt(0)
            correctButton.setOnClickListener {
                right ++
                cardBackgroundCards.setBackgroundColor(Color.parseColor("#00C800"))
                correctButton.visibility = View.GONE
                wrongButton.visibility = View.GONE
                Handler(this@LearnCardsActivity.mainLooper).postDelayed({ flipCardCards.flipTheView() }, 1000L)

            }
            wrongButton.setOnClickListener {
                wrong ++
                cardBackgroundCards.setBackgroundColor(Color.parseColor("#FF1C1C"))
                correctButton.visibility = View.GONE
                wrongButton.visibility = View.GONE
                Handler(this@LearnCardsActivity.mainLooper).postDelayed({ flipCardCards.flipTheView() }, 1000L)
            }
        }
        flipCardCards.onFlipListener = OnFlipAnimationListener { flipView, newCurrentSide ->
            if (newCurrentSide.name == "BACK_SIDE") {
                correctButton.visibility = View.VISIBLE
                wrongButton.visibility = View.VISIBLE
                if (wordList.isNotEmpty()) {
                    correctButton.visibility = View.VISIBLE
                    wrongButton.visibility = View.VISIBLE
                    question = genCards(wordList, trName)
                    elementFront.text = question


                } else if (wordList.size == 0 && isLast) {
                    elementFront.text = "Тренировка завершена"
                    correctButton.visibility = View.VISIBLE
                    wrongButton.visibility = View.VISIBLE
                    isLast = false
                } else {
                    finishBtnCards.visibility = View.VISIBLE
                    correctButton.visibility = View.GONE
                    wrongButton.visibility = View.GONE

                }
            } else {

                if (wordList.isNotEmpty()) {
                    correctButton.visibility = View.GONE
                    wrongButton.visibility = View.GONE
                    cardBackgroundCards.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    flipCardCards.apply {
                        hanziBack.text = wordList[0].hanzi
                        pinYinBack.text = wordList[0].pinyin
                        translationBack.text = wordList[0].eng
                    }
                    wordList.removeAt(0)
                } else {
                    correctButton.visibility = View.GONE
                    wrongButton.visibility = View.GONE
                    flipCardCards.isAutoFlipBack = false
                    cardBackgroundCards.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    hanziBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F)
                    hanziBack.text = "Статистика:"
                    translationBack.visibility = View.GONE
                    turnImage.visibility = View.VISIBLE
                    pinYinBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23F)
                    pinYinBack.text = "Всего карточек решено: ${right + wrong} \n \n Правильно: $right \n \nОшибок: $wrong"
                    finishBtnCards.setOnClickListener {
                        val nextIntent = Intent(this, StartActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
            }
        }
    }

    fun genCards(wordsList: MutableList<Character>, trName: String): String {
        lateinit var question: String

        when (trName) {
            "иероглиф" -> question = wordsList[0].hanzi.trim().toLowerCase()
            "транскрипция" -> question = wordsList[0].pinyin.trim().toLowerCase()
            "перевод" -> question = wordsList[0].eng.trim().toLowerCase()
        }

        return question
    }

}
