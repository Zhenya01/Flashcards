package com.zhenya.flashcards.adaptersAndViewHolders

import com.zhenya.flashcards.activities.CharacterUpdateActivity
import com.zhenya.flashcards.activities.CharactersListActivity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.zhenya.flashcards.classes.Character
import com.zhenya.flashcards.classes.ChineseHandler
import com.zhenya.flashcards.classes.DataBaseHandler
import com.zhenya.flashcards.R
import com.zhenya.flashcards.others.position
import kotlinx.android.synthetic.main.activity_character_list.*
import kotlinx.android.synthetic.main.layout_edit_dialog.*
import kotlinx.android.synthetic.main.layout_words.view.*



class CharactersAdapter(val context: Context, var charactersList: MutableList<Character>?): RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {


    private var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }


    override fun getItemCount(): Int {
        return charactersList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.layout_words, parent, false)
        return CharactersViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val id = charactersList!![position].id
        val hanzi = charactersList!![position].hanzi
        val pinyin = charactersList!![position].pinyin
        val eng = charactersList!![position].eng
        val module = charactersList!![position].module
        val category = charactersList!![position].category

        viewBinderHelper.bind(holder.swipeLayout, id.toString())
        holder.bind(context, id, hanzi, pinyin, eng, module, category, position)
    }

    fun updateList(newList: MutableList<Character>) {
        charactersList = newList
        notifyDataSetChanged()
        Log.i("MyApp", "update list worked, list: $newList")

    }
    fun update(position: Int){
        notifyItemChanged(position)
    }

    inner class CharactersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var swipeLayout: SwipeRevealLayout = view.swipelayoutWords
        private var frontview = view.frontViewWords
        private var del = view.delBtnWords
        private var edit = view.editBtnWords
        private var hanzitext = view.hanziTV
        private var pinyintext = view.pinyinTV
        private var engtext = view.EngTV

        fun showAlert(
            position: Int,
            view: View,
            id: Int,
            hanzi: String,
            pinyin: String,
            eng: String,
            module: String,
            category: String
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Удаление")
            builder.setMessage(
                "Данное действие приведет к безвозвратному удалению слова из модуля. Точно хотите удалить?"
            )

            builder.setPositiveButton("Да", DialogInterface.OnClickListener { dialog, _ ->
                deleteCharacter(position, id, hanzi, pinyin, eng, module, category)
            })
            builder.setNegativeButton("Нет", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            builder.setNeutralButton("Отмена", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })

            val alertDialog: AlertDialog = builder.create()
            alertDialog.window?.attributes?.width = WRAP_CONTENT
            alertDialog.show()


        }

        fun deleteCharacter(position: Int, id: Int, hanzi: String, pinyin: String, eng: String, module: String, category: String) {

            DataBaseHandler(context).deleteDataCharacters(Character(id, hanzi, pinyin, eng, module, category))
            //modulesList!!.removeAt(position)
            charactersList = DataBaseHandler(context).readDataCharacters(id)
            val noWordsText = (context as CharactersListActivity).noWordsText
            if (charactersList!!.size == 0) {
                noWordsText.visibility = View.VISIBLE
            } else {
                noWordsText.visibility = View.INVISIBLE
            }

            notifyItemRemoved(adapterPosition)
            //notifyDataSetChanged()
            val displayText = "$hanzi deleted"
            Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
            Log.d("RecyclerAdapter", displayText)
        }

        fun bind(context: Context, id: Int, hanzi: String, pinyin: String, eng: String, module: String, category:String,
                                                                 position: Int) {
            del.setOnClickListener(View.OnClickListener {
                showAlert(position, it, id, hanzi, pinyin, eng, module, category)
            })

            edit.setOnClickListener(View.OnClickListener {
                val displayText = "$hanzi edited"
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
                val nextIntent = Intent(context, CharacterUpdateActivity::class.java)
                var b = Bundle()
                b.putInt("id", id)
                b.putString("hanzi", hanzitext.text.toString())
                b.putString("pinyin", pinyintext.text.toString())
                b.putString("eng", engtext.text.toString())
                b.putString("module", charactersList?.get(0)?.module)
                b.putString("category", category)
                nextIntent.putExtras(b)
                context.startActivity(nextIntent)
            })
            hanzitext.text = hanzi
            pinyintext.text = pinyin
            engtext.text = eng


            hanzitext.setOnClickListener(View.OnClickListener {
                val displayText = "$hanzi hanzi clicked"
                showDialog("Иероглиф", hanzi, id, position)
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
            })
            pinyintext.setOnClickListener(View.OnClickListener {
                val displayText = "$hanzi pinyin clicked"
                showDialog("Транскрипция", pinyin, id, position)
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
            })
            engtext.setOnClickListener(View.OnClickListener {
                val displayText = "$hanzi eng clicked"
                showDialog("Перевод", eng, id, position)
                Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                Log.d("RecyclerAdapter", displayText)
            })
        }

    }

    private fun showDialog(title: String, content: String, id: Int, position: Int) {
        val dialog = Dialog(this.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_edit_dialog)
        dialog.window?.attributes?.width = MATCH_PARENT
        val columnName = dialog.findViewById<TextView>(R.id.ColumnName)
        columnName.setText(title)
        val columnContent = dialog.findViewById<EditText>(R.id.ColumnContent)
        columnContent.setText(content)
        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
        val noBtn = dialog.findViewById(R.id.noBtn) as Button
        val toneLayout = dialog.findViewById<LinearLayout>(R.id.toneLayout)

        columnContent.addTextChangedListener(object: TextWatcher {
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
                        columnContent.setText(string)
                        columnContent.setSelection(s.length)
                    }
                } catch (e:NoSuchElementException){}
            }
        })

        yesBtn.setOnClickListener {
            val db = DataBaseHandler(context)
            if (columnContent.text.toString().isNotEmpty()) {
                db.editDataCharacters(columnName.text.toString(), columnContent.text.toString(), id)
                dialog.dismiss()
                when (title) {
                    "Иероглиф" -> {
                        charactersList?.get(position)?.hanzi = columnContent.text.toString()
                    }
                    "Транскрипция" -> {
                        charactersList?.get(position)?.pinyin = columnContent.text.toString()
                    }
                    "Перевод" -> {
                        charactersList?.get(position)?.eng   = columnContent.text.toString()
                    }
                }
                notifyItemChanged(position)
            } else {
                Toast.makeText(context, "Заполните поле", Toast.LENGTH_LONG).show()
            }
        }
        if(title == "Транскрипция"){
            toneLayout.visibility = View.VISIBLE
        }
        dialog.toneBtn1.setOnClickListener{toneBtnClicked(dialog.toneBtn1, columnContent)}
        dialog.toneBtn2.setOnClickListener{toneBtnClicked(dialog.toneBtn2, columnContent)}
        dialog.toneBtn3.setOnClickListener{toneBtnClicked(dialog.toneBtn3, columnContent)}
        dialog.toneBtn4.setOnClickListener{toneBtnClicked(dialog.toneBtn4, columnContent)}
        dialog.toneBtn5.setOnClickListener{toneBtnClicked(dialog.toneBtn5, columnContent)}
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun toneBtnClicked(view: View, columnContent:EditText) {

        var string = columnContent.text.toString()
        if (string.isNotEmpty()) {
            string = string.slice(position until string.length)
            when (view.id) {
                R.id.toneBtn1 -> {
                    string += "1"
                }
                R.id.toneBtn2 -> {
                    string += "2"
                }
                R.id.toneBtn3 -> {
                    string += "3"
                }
                R.id.toneBtn4 -> {
                    string += "4"
                }
                R.id.toneBtn5 -> {
                    string += "0"
                }
            }
        }
        string = columnContent.text.slice(0 until position).toString() + ChineseHandler().toPinyin(
            string
        )
        columnContent.setText(string)
        columnContent.setSelection(string.length)

    }
}

