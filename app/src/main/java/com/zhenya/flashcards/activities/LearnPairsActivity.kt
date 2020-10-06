package com.zhenya.flashcards.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zhenya.flashcards.R
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.DataBaseHandler
import kotlinx.android.synthetic.main.activity_learn_pairs.*
import kotlinx.android.synthetic.main.layout_dialog_statistics.*
import kotlin.math.roundToInt

class LearnPairsActivity : AppCompatActivity() {
    lateinit var trNames: List<String>
    lateinit var wordList: MutableList<Character>
    lateinit var wordMap: Map<String,String>
    lateinit var textViews:MutableList<TextView>
    var pairsList: MutableList<Character> = mutableListOf()
    val group1: MutableList<String> = mutableListOf()
    val group2: MutableList<String> = mutableListOf()
    var isFirstTouched = true
    var pairGroup = ""
    var textView = 1
    val times = 3
    var correct = 0
    var timesPlayed = 0
    var right = 0
    var wrong = 0
    val wrongFirst = mutableListOf(true, true, true, true, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_pairs)
        textViews = mutableListOf(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10)
        trNames = intent.extras!!.getString("trName")!!.toLowerCase().split(" - ")
        wordList = DataBaseHandler(this).readDataCharacters(intent.extras!!.getInt("Module id"))
        if (wordList.isEmpty()) {
            Toast.makeText(
                this,
                "В этом модуле нет иероглифов. Добавьте иероглифы или выберите другой модуль",
                Toast.LENGTH_LONG
            ).show()
            this.finish()
        } else if (wordList.size < 10) {
            Toast.makeText(
                this,
                "В этом модуле меньше 10 иероглифов. Добавьте иероглифы или выберите другой модуль",
                Toast.LENGTH_LONG
            ).show()
            this.finish()
        } else {
            genPairs()
            finishPairsBtn.setOnClickListener {
                timesPlayed++
                if (finishPairsBtn.text != "Завершить") {
                    genPairs()
                    for (i in textViews) {
                        i.setBackgroundResource(R.drawable.rounded_corners_base)
                        finishPairsBtn.isClickable = false
                    }
                } else {
                    showDialogStats()
                }
                if (timesPlayed == times - 1) {
                    finishPairsBtn.text = "Завершить"
                }
            }
            finishPairsBtn.isClickable = false
        }
    }
    fun genPairs(){
        finishPairsBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorBlocked)
        correct = 0
        textViews.shuffle()
        wordList.shuffle()
        pairsList.clear()
        group1.clear()
        group2.clear()
        for(i in 0..4){
            pairsList.add(wordList[i])
        }
        when(trNames[0]){
            "иероглиф" -> {
                for(i in 0..4) {
                    group1.add(pairsList[i].hanzi)
                }
            }
            "транскрипция" -> {
                for(i in 0..4) {
                    group1.add(pairsList[i].pinyin)
                }
            }
            "перевод" -> {
                for(i in 0..4) {
                    group1.add(pairsList[i].eng)
                }
            }
        }
        when(trNames[1]) {
            "иероглиф" -> {
                for (i in 0..4) {
                    group2.add(pairsList[i].hanzi)
                }
            }
            "транскрипция" -> {
                for (i in 0..4) {
                    group2.add(pairsList[i].pinyin)
                }
            }
            "перевод" -> {
                for (i in 0..4) {
                    group2.add(pairsList[i].eng)
                }
            }
        }
        for(i in 0..4){
            textViews[i].text = group1[i]
            textViews[i].tag = (i + 1).toString()
        }
        for(i in 5..9){
            textViews[i].text = group2[i - 5]
            textViews[i].tag = (i - 4).toString()
        }
        for(i in textViews){
            i.setOnClickListener {
                i.setBackgroundResource(R.drawable.layout_border)
                val tv = findViewById<TextView>(textView)
                if(!isFirstTouched){
                    textView = i.id
                    if(i.tag == pairGroup && i != tv){
                        i.setBackgroundResource(R.drawable.rounded_corners_green)
                        tv.setBackgroundResource(R.drawable.rounded_corners_green)
                        right ++
                        correct ++
                        i.isClickable = false
                        tv.isClickable = false

                    }else if(i == tv){
                        i.setBackgroundResource(R.drawable.rounded_corners_base)
                        isFirstTouched = true
                        pairGroup = ""
                    }else{
                        i.setBackgroundResource(R.drawable.rounded_corners_red)
                        tv.setBackgroundResource(R.drawable.rounded_corners_red)
                        if(wrongFirst[(i.tag as String).toInt() - 1])
                        wrong ++
                        i.isClickable = false
                        tv.isClickable = false
                        Handler(this@LearnPairsActivity.mainLooper).postDelayed({
                            i.setBackgroundResource(R.drawable.rounded_corners_base)
                            tv.setBackgroundResource(R.drawable.rounded_corners_base)
                            i.isClickable = true
                            tv.isClickable = true
                        }, 500L)
                    }
                    isFirstTouched = true
                    pairGroup = ""
                }else{
                    textView = i.id
                    pairGroup = i.tag.toString()
                    isFirstTouched = false
                }
                if(correct == 5){
                    finishPairsBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
                    finishPairsBtn.isClickable = true

                }
            }
        }
    }
    fun showDialogStats() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog_statistics)
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.statText.text = "Всего пар \nсоеденино: ${5*times} \n \n Процент решенных\n правильно: ${(100 - (wrong.toDouble()/(5*times)*100)).roundToInt()}% \n \nОшибок: $wrong"
        Log.d("AppCustom", "$wrong, ${5*times}, ${wrong/(5*times)}")
        Toast.makeText(this,"$wrong, ${5*times}, ${wrong/(5*times)}", Toast.LENGTH_LONG).show()
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            val nextIntent = Intent(this, StartActivity::class.java)
            startActivity(nextIntent)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            val nextIntent = Intent(this, StartActivity::class.java)
            startActivity(nextIntent)
        }
        dialog.show()
    }
}