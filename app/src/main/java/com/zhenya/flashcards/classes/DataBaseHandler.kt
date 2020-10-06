package com.zhenya.flashcards.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.zhenya.flashcards.others.*
import kotlin.collections.ArrayList




class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("MyApp", "DB onCreate()")
        val createTable1 = "CREATE TABLE " + TABLE_CHARACTERS + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HANZI + " VARCHAR(256), " +
                COL_PINYIN + " VARCHAR(256), " +
                COL_ENG + " VARCHAR(256), " +
                COL_MODULE + " VARCHAR(256), " +
                COL_CATEGORY + " VARCHAR(256));"
        val createTable2 = "CREATE TABLE " + TABLE_MODULES + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " VARCHAR(256), " +
                COL_DESCRIPTION + " VARCHAR(256), " +
                COL_CATEGORY + " VARCHAR(256));"
        val createTable3 = "CREATE TABLE " + TABLE_CATEGORIES + " ( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " VARCHAR(256) UNIQUE);"
        val addCategory = "INSERT INTO $TABLE_CATEGORIES ($COL_NAME) VALUES ('test');"
        val addModule = "INSERT INTO $TABLE_MODULES ($COL_NAME,$COL_DESCRIPTION,$COL_CATEGORY) VALUES ('testModule', 'тест', 'test')"
        val addWords = "INSERT INTO $TABLE_CHARACTERS ($COL_HANZI, $COL_PINYIN,$COL_ENG, $COL_MODULE, $COL_CATEGORY) VALUES ('你好', 'nǐhǎo', 'привет', 'testModule', 'test'), ('大家好', 'qiánghuà', 'всем здравствуйте', 'testModule', 'test')"
        db?.execSQL(createTable1)
        db?.execSQL(createTable2)
        db?.execSQL(createTable3)
        /*db?.execSQL(addCategory)
        db?.execSQL(addModule)
        db?.execSQL(addWords)*/
        Log.i("MyApp", "DB onCreate() End")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVesion: Int) {}

    fun insertDataCategories(category: String): Boolean {

        val db = this.writableDatabase
        val query =
            "SELECT * FROM $TABLE_CATEGORIES WHERE $COL_NAME = '$category'"
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            do {
                count++
                Log.i("MyApp", "count is now $count")

            } while (cursor.moveToNext())
        }
        cursor.close()

        if (count != 0) {
            Toast.makeText(context, "В этом модуле уже есть такой иероглиф", Toast.LENGTH_LONG)
                .show()
            return false
        }
        val cv = ContentValues()
        cv.put(COL_NAME, category)
        val result = db.insert(TABLE_CATEGORIES, null, cv)
        Log.i("MyApp", "DB insert character result $result")
        if (result == -1L) {
            Toast.makeText(
                context,
                "Произошла какя-то ошибка. Попробуйте еще раз",
                Toast.LENGTH_LONG
            ).show()
            return false
        } else {
            Toast.makeText(context, "DB Character - Success", Toast.LENGTH_LONG).show()
            return true
        }
    }

    fun readDataCategories(): MutableList<String> {
        val list: MutableList<String> = ArrayList()

        val db = this.readableDatabase
        val query = "Select $COL_NAME from $TABLE_CATEGORIES"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                list.add(result.getString(0))
            } while (result.moveToNext())
        }

        result.close()
        db.close()

        return list
    }

    fun editDataCategories(category: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_CATEGORY, category)
        val cv2 = ContentValues()
        cv2.put(COL_NAME, category)
        db.update(TABLE_CHARACTERS, cv, "$COL_CATEGORY ='$category'", null)
        db.update(TABLE_MODULES, cv, "$COL_CATEGORY ='$category'", null)
        db.update(TABLE_CATEGORIES, cv2,"$COL_NAME = '$category'", null )
        db.close()
    }


    fun deleteDataCategories(category: String) {
        val db = this.writableDatabase

        db.delete(TABLE_CATEGORIES, "$COL_NAME = '$category'", null)
        db.delete(TABLE_MODULES, "$COL_CATEGORY = '$category'", null)
        db.delete(TABLE_CHARACTERS, "$COL_CATEGORY = '$category'", null)

        db.close()
    }

    fun insertDataModule(modules: Modules): Boolean {
        //readDataModules()
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, modules.name)
        cv.put(COL_DESCRIPTION, modules.description)
        cv.put(COL_CATEGORY, modules.category)

        try {
            val query =
                "SELECT * FROM $TABLE_MODULES WHERE $COL_NAME = '${modules.name}' AND $COL_CATEGORY = '${modules.category}' "
            val cursor = db.rawQuery(query, null)
            var count = 0
            if (cursor.moveToFirst()) {
                do {
                    count++
                    Log.i("MyApp", "count is now $count")

                } while (cursor.moveToNext())
            }
            cursor.close()

            if (count != 0) {
                Toast.makeText(context, "Такой модуль уже существует", Toast.LENGTH_LONG)
                    .show()
                return false

            } else {val result = db.insertOrThrow(TABLE_MODULES, null, cv)
                //Log.i("MyApp","DB insert module result $result")
                if (result == -1L) {
                    Toast.makeText(context, "DB Modules - Failed", Toast.LENGTH_LONG).show()
                    return false

                }else{
                    Toast.makeText(context, "DB Modules - Success", Toast.LENGTH_LONG).show()
                    return true

                }
            }

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "$e Произошла какая-то ошибка. Попробуйте еще раз",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }


    fun readDataModules(category: String): MutableList<Modules> {
        val list: MutableList<Modules> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_MODULES WHERE $COL_CATEGORY = '$category'"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val module = Modules(
                    id = result.getInt(0),
                    name = result.getString(1),
                    description = result.getString(2),
                    category = result.getString(3)
                )
                list.add(module)
                Log.i("MyApp", module.toString())
            } while (result.moveToNext())
        }

        result.close()
        db.close()

        return list
    }

    fun editDataModules(module: Modules) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, module.name)
        cv.put(COL_DESCRIPTION, module.description)
        val cv2 = ContentValues()
        cv2.put(COL_MODULE, module.name)
        val module1 = this.moduleByID(module.id)
        db.update(TABLE_MODULES, cv, "$COL_ID ='${module.id}'", null)
        db.update(TABLE_CHARACTERS, cv2, "$COL_MODULE = '${module1.name}'", null)
        db.close()
    }

    fun deleteDataModules(module: Modules) {
        val db = this.writableDatabase

        db.delete(TABLE_MODULES, "$COL_NAME = '${module.name}'", null)
        db.delete(TABLE_CHARACTERS, "$COL_MODULE = '${module.name}'", null)
        db.close()
    }


    fun insertDataCharacter(character: Character): Boolean {
        val db = this.writableDatabase
        val query =
            "SELECT * FROM $TABLE_CHARACTERS WHERE $COL_HANZI = '${character.hanzi}' AND $COL_MODULE = '${character.module}' "
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            do {
                count++
                Log.i("MyApp", "count is now $count")

            } while (cursor.moveToNext())
        }
        cursor.close()

        if (count != 0) {
            Toast.makeText(context, "В этом модуле уже есть такой иероглиф", Toast.LENGTH_LONG)
                .show()
            return false
        }
        val cv = ContentValues()
        cv.put(COL_HANZI, character.hanzi)
        cv.put(COL_PINYIN, character.pinyin)
        cv.put(COL_ENG, character.eng)
        cv.put(COL_MODULE, character.module)
        cv.put(COL_CATEGORY, character.category)
        val result = db.insert(TABLE_CHARACTERS, null, cv)
        Log.i("MyApp", "DB insert character result $result")
        if (result == -1L) {
            Toast.makeText(
                context,
                "Произошла какя-то ошибка. Попробуйте еще раз",
                Toast.LENGTH_LONG
            ).show()
            return false
        } else {
            Toast.makeText(context, "DB Character - Success", Toast.LENGTH_LONG).show()
            return true
        }

    }



    fun readDataCharacters(id: Int): MutableList<Character> {
        val list: MutableList<Character> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_CHARACTERS where $COL_MODULE = (SELECT $COL_NAME FROM $TABLE_MODULES where $COL_ID = '$id') AND $COL_CATEGORY = (SELECT $COL_CATEGORY FROM $TABLE_MODULES where $COL_ID = '$id')"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val character = Character(
                    id = result.getInt(0),
                    hanzi = result.getString(1),
                    pinyin = result.getString(2),
                    eng = result.getString(3),
                    module = result.getString(4),
                    category = result.getString(5)
                )
                list.add(character)
                Log.i("MyApp", character.toString())
            } while (result.moveToNext())
        }

        result.close()
        db.close()
        Log.i("MyApp", "list, given by read data fun : $list, moduleID: $id")
        return list

    }





    fun deleteDataCharacters(character: Character) {
        val db = this.writableDatabase

        db.delete(TABLE_CHARACTERS, "$COL_ID = '${character.id}'", null)
        db.close()
    }



    fun editDataCharacters(character: Character) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_HANZI, character.hanzi)
        cv.put(COL_PINYIN, character.pinyin)
        cv.put(COL_ENG, character.eng)

        val res = db.update(TABLE_CHARACTERS, cv, "$COL_ID ='${character.id}'", null)
        println(res)
        db.close()
    }

    fun editDataCharacters(col_name: String, col_content: String, id: Int) {
        lateinit var name: String
        val db = this.writableDatabase
        when (col_name) {
            "Иероглиф" -> {
                name = COL_HANZI
            }
            "Транскрипция" -> {
                name = COL_PINYIN
            }
            "Перевод" -> {
                name = COL_ENG
            }
        }
        val cv = ContentValues()
        cv.put(name, col_content)
        db.update(TABLE_CHARACTERS, cv, "$COL_ID ='$id'", null)
        db.close()
    }
    fun moduleByID(id:Int): Modules {
        val db = this.readableDatabase
        val query =
            "SELECT * FROM $TABLE_MODULES WHERE $COL_ID = '$id'"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val module  = Modules(
            name = cursor.getString(1),
            description = cursor.getString(2),
            category = ""
        )
        cursor.close()
        return module
    }
    fun runQuery(query:String){
        val db = this.writableDatabase
        db.execSQL(query)
    }
    fun findModule(module:String, category: String):Boolean{
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_MODULES WHERE $COL_NAME = '$module' AND $COL_CATEGORY = '$category'", null)
        cursor.moveToFirst()
        val bool = cursor.getInt(0)!=0
        Log.d("AppCustom","${cursor.getInt(0)}")
        cursor.close()
        return bool
    }
    fun findCharacter(hanzi:String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_CHARACTERS WHERE $COL_HANZI = '$hanzi'", null)
        cursor.moveToFirst()
        val bool = cursor.getInt(0)!=0
        cursor.close()
        return bool
    }
}








