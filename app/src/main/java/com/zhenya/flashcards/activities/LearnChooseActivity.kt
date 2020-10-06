package com.zhenya.flashcards.activities


import com.zhenya.flashcards.adaptersAndViewHolders.HeaderItemVH
import com.zhenya.flashcards.adaptersAndViewHolders.TrainingItem
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.*
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_learn_choose.*


class LearnChooseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_choose)
        val adapter = GroupAdapter<GroupieViewHolder>().apply {
            spanCount = 3
        }
        val displayMetrics = applicationContext.resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels/(displayMetrics.densityDpi/160)
        val objwidth = 125
        val spacing = (displayWidth - (objwidth * 3))/4
        Log.d("AppCustom","$spacing,$displayWidth,$objwidth ")
        // trainingsView.addItemDecoration(GridSpacingItemDecoration(3, spacing, true, 0))
        trainingsView.adapter = adapter
        trainingsView.apply {
            layoutManager =
                GridLayoutManager(this@LearnChooseActivity, 3).apply {
                    spanSizeLookup = adapter.spanSizeLookup
                }
        }
        val section1 = Section()
        val sec1 = "#7B1FA2"
        section1.apply {
            setHeader(HeaderItemVH(sec1, "Обучение"))
            add(
                TrainingItem(
                    "Иероглиф",
                    "Программа для изучения иероглифов",
                    sec1,
                    "Обучение",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Перевод",
                    "Программа для изучения перевода",
                    sec1,
                    "Обучение",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Транскрипция",
                    "Программа для изучения транскрипции",
                    sec1,
                    "Обучение",
                    this@LearnChooseActivity
                )
            )
        }
        adapter.add(section1)

        val section2 = Section()
        val sec2 = "#512DA8"
        section2.apply {
            setHeader(HeaderItemVH(sec2, "Карточки с проверкой"))
            add(
                TrainingItem(
                    "Иероглиф - перевод",
                    "Программа показывает иероглиф и проверяет правильность его перевода",
                    sec2,
                    "Карточки с проверкой",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Перевод - иероглиф",
                    "Программа показывает перевод и проверяет правильность иероглифа",
                    sec2,
                    "Карточки с проверкой",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Транскрипция - перевод",
                    "Программа показывает транскрипцию и проверяет правильность перевода",
                    sec2,
                    "Карточки с проверкой",
                    this@LearnChooseActivity
                )
            )
        }
        adapter.add(section2)

        val section3 = Section()
        val sec3 = "#1976D2"
        section3.apply {
            setHeader(HeaderItemVH(sec3,"Карточки"))

            add(
                TrainingItem(
                    "Иероглиф",
                    "Программа для тренировки по иероглифу",
                    sec3,
                    "Карточки",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Перевод",
                    "Программа для тренировки по переводу",
                    sec3,
                    "Карточки",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Транскрипция",
                    "Программа для тренировки по транскрипции",
                    sec3,
                    "Карточки",
                    this@LearnChooseActivity
                )
            )

        }
        adapter.add(section3)



        val section4 = Section()
        val sec4 = "#0097A7"
        section4.apply {
            setHeader(HeaderItemVH(sec4,"Тест"))
            add(
                TrainingItem(
                    "Иероглиф - перевод",
                    "Программа показывает иероглиф и несколько вариантов перевода",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Перевод - иероглиф",
                    "Программа показывает перевод и несколько вариантов иероглифов",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Иероглиф - транскрипция",
                    "Программа показывает иероглиф и несколько вариантов транскрипции",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Транскрипция - иероглиф",
                    "Программа показывает транскрипцию иероглифа и несколько вариантов иероглифов",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Транскрипция - перевод",
                    "Программа показывает транскрипцию иероглифа и несколько вариантов его перевода",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
            add(
                TrainingItem(
                    "Перевод - транскрипция",
                    "Программа показывает перевод иероглифа и несколько вариантов его транскрипции",
                    sec4,
                    "Тест",
                    this@LearnChooseActivity
                )
            )
        }
        adapter.add(section4)
        val section5 = Section()
        val sec5 = "#689F38"
        section5.apply {
            setHeader(HeaderItemVH(sec5,"Пары"))
            add(
                TrainingItem(
                    "Иероглиф - перевод",
                    "Программа показывает несколько пар иероглиф - перевод в разброс",
                    sec5,
                    "Пары",
                    this@LearnChooseActivity
                )
            )

            add(
                TrainingItem(
                    "Транскрипция - иероглиф",
                    "Программа показывает несколько пар транскрипция - иероглиф в разброс",
                    sec5,
                    "Пары",
                    this@LearnChooseActivity
                )
            )

            add(
                TrainingItem(
                    "Перевод - транскрипция",
                    "Программа показывает несколько пар транскрипция - перевод в разброс",
                    sec5,
                    "Пары",
                    this@LearnChooseActivity
                )
            )


        }
        adapter.add(section5)

    }

}







