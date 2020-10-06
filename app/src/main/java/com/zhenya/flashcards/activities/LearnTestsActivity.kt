package com.zhenya.flashcards.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.zhenya.flashcards.R
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.DataBaseHandler
import kotlinx.android.synthetic.main.activity_learn_cards.*
import kotlinx.android.synthetic.main.activity_learn_tests.*
import kotlinx.android.synthetic.main.layout_card_back.*
import kotlinx.android.synthetic.main.layout_card_back.view.*
import kotlinx.android.synthetic.main.layout_card_front.*
import kotlinx.android.synthetic.main.layout_card_front.view.*
import kotlin.random.Random

class LearnTestsActivity : AppCompatActivity() {
    lateinit var trNames: List<String>
    lateinit var wordList: MutableList<Character>
    val testsList: MutableList<Character> = mutableListOf()
    lateinit var question: String
    val answerList = mutableListOf("","","")
    val temp: MutableList<Character> = mutableListOf()
    val testsMap = mapOf("a" to mutableListOf("",""), "b" to mutableListOf("",""), "c" to mutableListOf("",""), "d" to mutableListOf("",""))
    var right = 0
    var wrong = 0
    var isLast = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_tests)
        trNames = intent.extras!!.getString("trName")!!.toLowerCase().split(" - ")
        wordList = DataBaseHandler(this).readDataCharacters(intent.extras!!.getInt("Module id"))
        wordList.shuffle()
        testsList.clear()
        testsList.addAll(wordList)
        flipCardTests.isFlipOnTouch = false
        flipCardTests.isAutoFlipBack = true
        flipCardTests.autoFlipBackTime = 2000

        Log.d("AppCustom", "${wordList.size}")
        if (wordList.isEmpty()) {
            Toast.makeText(this, "В этом модуле нет иероглифов. Добавьте иероглифы или выберите другой модуль",Toast.LENGTH_LONG).show()
            this.finish()
        }
        else if(wordList.size < 4){
            Toast.makeText(this, "В этом модуле меньше 4 иероглифов. Добавьте иероглифы или выберите другой модуль",Toast.LENGTH_LONG).show()
            this.finish()
        }
        else {
            val textViews = listOf(a,b,c,d)
            genTests()
            flipCardTests.hanziBack.text = answerList[0]
            flipCardTests.pinYinBack.text = answerList[1]
            flipCardTests.translationBack.text = answerList[2]
            wordList.removeAt(0)
            textViews.forEach{
                val letter = it.tag.toString().split("_")[0]
                it.text = testsMap[letter]?.get(0)
                Log.d("AppCustom","String ${testsMap[letter]?.get(0).toString()}" )
                it.tag = "${letter}_${testsMap[letter]?.get(1)} ".trim()
                it.setBackgroundResource(R.drawable.rounded_corners_base)
            }
            textViews.forEach { i ->
                i.setOnClickListener {
                    Log.d("AppCustom", it.tag.toString().split("_")[1])
                    if(it.tag.toString().split("_")[1] == "correct"){
                        it.setBackgroundResource(R.drawable.rounded_corners_green)
                        right ++
                    }
                    else{
                       it.setBackgroundResource(R.drawable.rounded_corners_red)
                       wrong ++
                    }
                    flipCardTests.flipTheView()
                }
            }
            flipCardTests.onFlipListener = EasyFlipView.OnFlipAnimationListener { flipView, newCurrentSide ->
                if(newCurrentSide.name == "BACK_SIDE") {
                    if (wordList.isNotEmpty()) {
                        genTests()
                    }
                    else if(!isLast){
                        isLast = true
                    }
                    else {
                        finishTestsBtn.visibility = View.VISIBLE
                    }
                }
                else {
                    if (wordList.isNotEmpty()) {
                        flipCardTests.hanziBack.text = answerList[0]
                        flipCardTests.pinYinBack.text = answerList[1]
                        flipCardTests.translationBack.text = answerList[2]
                        wordList.removeAt(0)
                        textViews.forEach {
                            val letter = it.tag.toString().split("_")[0]
                            it.text = testsMap[letter]?.get(0)
                            Log.d("AppCustom", "String ${testsMap[letter]?.get(0).toString()}")
                            it.tag = "${letter}_${testsMap[letter]?.get(1)} ".trim()
                            it.setBackgroundResource(R.drawable.rounded_corners_base)
                        }
                    }
                    else{
                        elementFront.text = "Тренировка завершена"
                        flipCardTests.isFlipOnTouch = true
                        hanziBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F)
                        hanziBack.text = "Статистика:"
                        translationBack.visibility = View.GONE
                        turnImage.visibility = View.VISIBLE
                        pinYinBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23F)
                        pinYinBack.text = "Всего карточек решено: ${right + wrong} \n \n Правильно: $right \n \nОшибок: $wrong"
                        flipCardTests.isAutoFlipBack = false
                        textViews.forEach { it.visibility = View.GONE }
                        finishTestsBtn.setOnClickListener {
                            val nextIntent = Intent(this, StartActivity::class.java)
                            startActivity(nextIntent)
                        }
                    }
                }
            }
        }
    }
    fun genTests() {
        lateinit var answer: String
        lateinit var choices: MutableList<String>
        testsList.shuffle()
        when (trNames[0]) {
            "иероглиф" -> question = wordList[0].hanzi
            "транскрипция" -> question = wordList[0].pinyin
            "перевод" -> question = wordList[0].eng
        }
        answerList[0] = wordList[0].hanzi
        answerList[1] = wordList[0].pinyin
        answerList[2] = wordList[0].eng

        flipCardTests.elementFront.text = question
        when (trNames[1]) {
            "иероглиф" -> {
                answer = wordList[0].hanzi
                temp.clear()
                temp.addAll(testsList)
                Log.d("AppCustom","temp 1 - $temp")
                temp.remove(wordList[0])
                Log.d("AppCustom","temp 1 - $temp")
                temp.shuffle()
                choices = mutableListOf(temp[0].hanzi, temp[1].hanzi, temp[2].hanzi)
            }
            "транскрипция" -> {
                answer = wordList[0].pinyin
                temp.clear()
                temp.addAll(testsList)
                Log.d("AppCustom","temp 1 - $temp")
                temp.remove(wordList[0])
                Log.d("AppCustom","temp 1 - $temp")
                temp.shuffle()
                choices = mutableListOf(temp[0].pinyin, temp[1].pinyin, temp[2].pinyin)
            }
            "перевод" -> {
                answer = wordList[0].eng
                temp.clear()
                temp.addAll(testsList)
                Log.d("AppCustom","temp 1 - $temp")
                temp.remove(wordList[0])
                Log.d("AppCustom","temp 1 - $temp")
                temp.shuffle()
                choices = mutableListOf(temp[0].eng, temp[1].eng, temp[2].eng)
            }
        }
        val correct = listOf("a", "b", "c", "d").shuffled()[0]
        for(i in testsMap){
            Log.d("AppCustom","key - ${i.key}\ncorrect - $correct")
            if(i.key == correct) {
                i.value[0] = answer
                i.value[1] = "correct"
            }
            else{
                i.value[0] = choices[0]
                choices.removeAt(0)
                i.value[1] = "wrong"
            }
        }
    }
}