package com.zhenya.flashcards.activities

import com.zhenya.flashcards.adaptersAndViewHolders.CategoriesHeaderVH
import com.zhenya.flashcards.adaptersAndViewHolders.ModuleItemVH
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.classes.Modules
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.*
import com.zhenya.flashcards.R
import kotlinx.android.synthetic.main.activity_choose_words.*
class ChooseWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trainingType = intent.extras?.getString("Training type")
        val trainingName = intent.extras?.getString("Training name")
        setContentView(R.layout.activity_choose_words)
        val adapter = GroupAdapter<GroupieViewHolder>()
        wordChooseList.adapter = adapter
        wordChooseList.layoutManager = LinearLayoutManager(this)
        val db = DataBaseHandler(this)
        val categories = db.readDataCategories()
        var modulesMap: MutableMap<Int, List<Modules>> = mutableMapOf()
        for(i in 0 until categories.size){
            var set = db.readDataModules(categories[i])
            if (set.size > 0) {
                modulesMap[i] = emptyList()
                for (j in set) {
                    modulesMap[i] = modulesMap[i]!!.plus(j)
                    Log.d("AppCustom","${modulesMap.size}, $modulesMap")
                }
            }
        }
        for(i in 0 until (categories.size)) {
            ExpandableGroup(CategoriesHeaderVH(categories[i], false, this), false).apply {
                add(Section(genItemList(modulesMap[i], trainingType.toString(),trainingName.toString())))
                adapter.add(this)
            }
        }
        //Log.d("AppCustom","${modules.size}, $modules")
    }

    fun genItemList(listModules:List<Modules>?, trType: String, trName: String):List<ModuleItemVH> {
        var list:List<ModuleItemVH> = emptyList<ModuleItemVH>()
        if (listModules != null) {
            if (listModules.isNotEmpty()) {
                for (i in listModules) {
                    list = list.plus(ModuleItemVH(i.name, i.description, i.id,this, trType, trName))
                }
            }
        }
        Log.d("AppCustom","$list")
        return list
    }

}




