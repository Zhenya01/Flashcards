package com.zhenya.flashcards.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.adaptersAndViewHolders.CharactersAdapter
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_character_list.*
import kotlin.properties.Delegates

class CharactersListActivity : AppCompatActivity() {
    lateinit var module: String
    var id by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("MyApp","OnCreate triggered")
        module = intent.extras?.getString("module").toString()
        id = intent.extras?.getInt("moduleID")!!.toInt()
        setContentView(R.layout.activity_character_list)
        this.title = module
        findViewById<FloatingActionButton>(R.id.addWordFAB).setOnClickListener{
            val nextIntent = Intent(this, CharacterAddActivity::class.java)
            nextIntent.putExtra("module", module)
            nextIntent.putExtra("category", intent?.extras?.getString("category"))
            startActivity(nextIntent)
            Log.i("MyApp","goAddWords function worked") }
        charactersView.layoutManager = LinearLayoutManager(this)
        charactersView.adapter = CharactersAdapter(this, getCharacterData(id))
    }

    fun getCharacterData(module:Int): MutableList<Character> {
        val db = DataBaseHandler(this)
        val charactersList = db.readDataCharacters(module)
        charactersList.sortBy { it.hanzi }
        return charactersList
    }

    override fun onResume() {
        super.onResume()
        Log.i("MyApp","OnResume triggered")

        val charactersData = getCharacterData(id)

        if(charactersData.size == 0) {
            noWordsText.visibility = View.VISIBLE
        }else{
            noWordsText.visibility = View.INVISIBLE
        }
        (charactersView.adapter!! as CharactersAdapter).updateList(charactersData)
    }
    override fun onBackPressed() {
        val nextIntent = Intent(this, ModulesViewActivity::class.java)
        startActivity(nextIntent)
    }
}