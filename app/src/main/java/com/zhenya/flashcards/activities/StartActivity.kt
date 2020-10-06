package com.zhenya.flashcards.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhenya.flashcards.R

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, intent?.data?.toString(),Toast.LENGTH_LONG).show()

/*
        Log.i("MyApp","On create")
        Toast.makeText(this,"onCreate",Toast.LENGTH_SHORT).show()
        val path = this.getDatabasePath("CharDB")
        val a = path.absolutePath
        Log.i("MyApp","DB is here : ${path.absolutePath}")
        Toast.makeText(this,"DB is here : ${path.absolutePath}",Toast.LENGTH_SHORT).show()

        val f = File(a)
        Log.i("MyApp","Can Read : ${f.canRead()}")
        Log.i("MyApp","Can Write : ${f.canWrite()}")

        f.copyTo(File("/storage/emulated/0/CharDB.sqlite"))
        f.delete()

        val path2 = this.getDatabasePath("CharDB")
        val a2 = path2.absolutePath
        Log.i("MyApp","DB is here : ${path2.absolutePath}")
        Toast.makeText(this,"DB is here : ${path.absolutePath}",Toast.LENGTH_SHORT).show()
*/

        setContentView(R.layout.activity_start)
    }

    fun goModulesView(view: View) {
        val nextIntent = Intent(this, ModulesViewActivity::class.java)
        startActivity(nextIntent)
    }
    fun goLearnChoose(view: View) {
        val nextIntent = Intent(this, LearnChooseActivity::class.java)
        startActivity(nextIntent)
    }
}
    //fun goRecyclerView(view: View) {
    //    val nextIntent = Intent(this, RecyclerViewActivity::class.java)
    //    startActivity(nextIntent)
    //}
