package com.zhenya.flashcards.activities
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wajahatkarim3.easyflipview.EasyFlipView.OnFlipAnimationListener
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_learn_check_cards.*
import kotlinx.android.synthetic.main.layout_card_back.*
import kotlinx.android.synthetic.main.layout_card_back.view.*
import kotlinx.android.synthetic.main.layout_card_front.*
import kotlinx.android.synthetic.main.layout_card_front.view.*


class LearnCheckCardsActivity : AppCompatActivity() {
    lateinit var trNames: List<String>
    lateinit var wordList: MutableList<Character>
    lateinit var question: List<String>
    lateinit var answer: List<String>
    lateinit var words: List<List<String>>
    var isLast = true
    var right = 0
    var wrong = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trNames = intent.extras!!.getString("trName").toString().toLowerCase().split(" - ")
        wordList = DataBaseHandler(this).readDataCharacters(intent.extras!!.getInt("Module id"))
        Log.d("AppCustom", "${wordList.size}")
        if (wordList.isEmpty()) {
            /*val nextIntent = Intent(this, ChooseWordsActivity::class.java)
            nextIntent.putExtra("Training name", intent.extras!!.getString("trName"))
            nextIntent.putExtra("Training type", "Карточки")
            startActivity(nextIntent)*/
            Toast.makeText(this,"В этом модуле нет иероглифов. Добавьте иероглифы или выберите другой модуль", Toast.LENGTH_LONG).show()
            this.finish()
        } else {
            Log.d("AppCustom", "$trNames")
            setContentView(R.layout.activity_learn_check_cards)
            Log.d("AppCustom", "$trNames")
            wordList.shuffle()
            flipCard.isFlipOnTouch = false
            flipCard.isAutoFlipBack = true
            flipCard.autoFlipBackTime = 3500
            //var words = genCards(wordList, trNames)
            words = genCards(wordList, trNames)
            flipCard.apply {
                elementFront.text = words[0].joinToString()
                hanziBack.text = wordList[0].hanzi
                pinYinBack.text = wordList[0].pinyin
                translationBack.text = wordList[0].eng
            }
            answer = words[1]
            question = words[0]
            wordList.removeAt(0)
            checkBtn.setOnClickListener {
                if (enterAnswer.text.isNotEmpty())
                    flipCard.flipTheView()
            }
            enterAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    flipCard.isFlipOnTouch = s.isNotEmpty()
                }
            })

            flipCard.onFlipListener = OnFlipAnimationListener { flipView, newCurrentSide ->
                if (newCurrentSide.name == "BACK_SIDE") {
                    if (wordList.isNotEmpty()) {
                        checkBtn.hideKeyboard()
                        enterAnswer.visibility = View.GONE
                        checkBtn.visibility = View.GONE
                        words = genCards(wordList, trNames)
                        question = words[0]
                        elementFront.text = question.joinToString()

                        if (enterAnswer.text.toString().toLowerCase().trim() in answer) {
                            cardBackground.setBackgroundColor(Color.parseColor("#00C800"))
                            right ++
                        } else {
                            cardBackground.setBackgroundColor(Color.parseColor("#FF1C1C"))
                            wrong ++
                        }
                        answer = words[1]

                    } else if (wordList.size == 0 && isLast) {
                        checkBtn.hideKeyboard()
                        enterAnswer.visibility = View.GONE
                        checkBtn.visibility = View.GONE
                        if (enterAnswer.text.toString().toLowerCase().trim() in answer) {
                            cardBackground.setBackgroundColor(Color.parseColor("#00C800"))
                            right ++
                        } else {
                            cardBackground.setBackgroundColor(Color.parseColor("#FF1C1C"))
                            wrong ++
                        }
                        isLast = false
                        //cardBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        elementFront.text = "Тренировка завершена"
                    } else {
                        finishBtn.visibility = View.VISIBLE

                    }
                } else {
                    if (wordList.isNotEmpty()) {
                        enterAnswer.visibility = View.VISIBLE
                        checkBtn.visibility = View.VISIBLE
                        cardBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        enterAnswer.text.clear()
                        flipCard.apply {
                            hanziBack.text = wordList[0].hanzi
                            pinYinBack.text = wordList[0].pinyin
                            translationBack.text = wordList[0].eng
                        }
                        wordList.removeAt(0)
                    } else {
                        flipCard.isAutoFlipBack = false
                        enterAnswer.visibility = View.GONE
                        checkBtn.visibility = View.GONE
                        cardBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        hanziBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F)
                        hanziBack.text = "Статистика:"
                        translationBack.visibility = View.GONE
                        //elementFront.text = "Тренировка завершена"
                        turnImage.visibility = View.VISIBLE
                        pinYinBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23F)
                        pinYinBack.text = "Всего карточек решено: ${right + wrong} \n \n " +
                                "Правильно: $right \n \n" +
                                "Ошибок: $wrong"
                    }
                    finishBtn.setOnClickListener {
                        val nextIntent = Intent(this, StartActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
            }
        }
    }
    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
    fun genCards(wordsList: MutableList<Character>, trNames:List<String>): List<List<String>> {
        lateinit var question: List<String>
        lateinit var answer: List<String>
        when (trNames[0]) {
            "иероглиф" -> question = wordsList[0].hanzi.trim().toLowerCase().split(", ", "; ")
            "транскрипция" -> question = wordsList[0].pinyin.trim().toLowerCase().split(", ", "; ")
            "перевод" -> question = wordsList[0].eng.trim().toLowerCase().split(", ", "; ")
        }
        when (trNames[1]) {
            "иероглиф" -> answer = wordsList[0].hanzi.trim().toLowerCase().split(", ", "; ")
            "транскрипция" -> answer = wordsList[0].pinyin.trim().toLowerCase().split(", ", "; ")
            "перевод" -> answer = wordsList[0].eng.trim().toLowerCase().split(", ", "; ")
        }
        return listOf(question, answer)
    }
}