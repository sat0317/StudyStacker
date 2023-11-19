package com.khumakers.studystacker

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.khumakers.studystacker.databinding.ActivityMainBinding
import com.khumakers.studystacker.ui.home.HomeFragment
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var database: SharedPreferences;
    private lateinit var databaseEditor: SharedPreferences.Editor;

    lateinit var adapter: ListviewAdapter

    companion object{
        var mainActivity : MainActivity? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = getSharedPreferences("database", MODE_PRIVATE);
        databaseEditor = database.edit();
        if (database.getString("THEME", "default_color").equals("color1")) {
            setTheme(R.style.Theme_StudyStacker_Colorset1_NoActionBar);
        } else if (database.getString("THEME", "default_color").equals("color2")) {
            setTheme(R.style.Theme_StudyStacker_Colorset2_NoActionBar);
        } else if (database.getString("THEME", "default_color").equals("color3")) {
            setTheme(R.style.Theme_StudyStacker_Colorset3_NoActionBar);
        } else {
            setTheme(R.style.Theme_StudyStacker_Colorset1_NoActionBar);
            databaseEditor.putString("default_color", "color1");
            databaseEditor.apply();
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            customDialog();
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        adapter = ListviewAdapter()
        adapter.clearList();


        var timeClock = intent.getIntExtra("timeClock", -1) as Int
        var innerCounter = intent.getIntExtra("innerCounter", -1) as Int
        if(innerCounter != -1){
            databaseEditor.putString("SUBJECT_TIME_$innerCounter", timeClock.toString());
            databaseEditor.apply();
//            Toast.makeText(this, (database.getString("SUBJECT_TIME_0", "0")), Toast.LENGTH_SHORT).show()
        }

        var subjectCount = database.getString("SUBJECT_COUNT", "0")?.toInt() ?: 0

        if(subjectCount==0) {
            databaseEditor.putString("SUBJECT_COUNT", "0");
            databaseEditor.apply();
        }
        if (database.getString("RECENT_DATE", "0") != (LocalDate.now().toString())){
            for (i: Int in 0..<subjectCount) {
                databaseEditor.putString("SUBJECT_TIME_$i", "0");
            }
            databaseEditor.putString("RECENT_DATE", LocalDate.now().toString());
            databaseEditor.apply();
        }

        var sumTime: Int = 0;
        for(i: Int in 0..<subjectCount) {
            adapter.addItem(
                database.getString("SUBJECT_NAME_$i", "NULL").toString(),
                database.getString("SUBJECT_TIME_$i", "0")?.toInt() ?: 0
            );
        }
        adapter.setAimTime(database.getString("AIM_TIME", "0")!!.toInt());
        val aimTime: Int = adapter.getAimTime();

        databaseEditor.commit()

        mainActivity = this

        findViewById<TextView>(R.id.time_all).text = adapter.getSumTime().toTimestamp();
        findViewById<TextView>(R.id.aim_time_head).text = "일일 목표: "+(adapter.getAimTime()/60).toString()+"시간 "+(adapter.getAimTime()%60).toString()+"분";


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    private fun customDialog(){
        val dialogView = layoutInflater.inflate(R.layout.addsubject_main, null)
        val name = dialogView.findViewById<EditText>(R.id.subjectName)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        val addSubject = dialogView.findViewById<Button>(R.id.addSubject)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)


        addSubject.setOnClickListener {
            if(name.text.toString() == ""){
                Toast.makeText(
                    this,
                    "과목 이름이 입력되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                builder.dismiss()
            }
            else{
                adapter = ListviewAdapter();

                database = getSharedPreferences("database", MODE_PRIVATE);
                databaseEditor = database.edit();

                adapter.addItem(name.text.toString(), 0);

                var subjectCount = adapter.getListSize();

                databaseEditor.putString("SUBJECT_COUNT", subjectCount.toString());

                for(i: Int in 0..<subjectCount){
                    databaseEditor.putString("SUBJECT_NAME_$i", adapter.getListItem(i).subjectName)
                    databaseEditor.putString("SUBJECT_TIME_$i", adapter.getListItem(i).timeClock.toString())
                }

                databaseEditor.apply();
                databaseEditor.commit();
                adapter.notifyDataSetChanged();
                findViewById<RecyclerView>(R.id.subjectTable).requestLayout()

                Toast.makeText(
                    this,
                    "${name.text} 과목이 추가되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                builder.dismiss()
            }
        }

        cancelButton.setOnClickListener { builder.dismiss() }


        builder.show()
    }

    fun galleryStart(){
        val houredit: EditText = findViewById<EditText>(R.id.hour_setting)
        val minuteedit: EditText = findViewById<EditText>(R.id.minute_setting)

        findViewById<Button>(R.id.save_time).setOnClickListener {
            database = getSharedPreferences("database", MODE_PRIVATE);
            databaseEditor = database.edit();

            if(houredit.text.toString() == "" || minuteedit.text.toString() == ""){ }
            else if(houredit.text.toString().toInt()*60+minuteedit.text.toString().toInt()>=0){
                adapter.setAimTime(houredit.text.toString().toInt()*60 + minuteedit.text.toString().toInt())
                databaseEditor.putString("AIM_TIME", (houredit.text.toString().toInt()*60 + minuteedit.text.toString().toInt()).toString())
                Toast.makeText(this, "목표 시간이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                databaseEditor.apply();
                databaseEditor.commit();
            }
        }
        findViewById<Button>(R.id.theme1).setOnClickListener {
            database = getSharedPreferences("database", MODE_PRIVATE);
            databaseEditor = database.edit();

            databaseEditor.putString("THEME", "color1")
            databaseEditor.apply();
            databaseEditor.commit();

            Toast.makeText(this, "테마가 1로 변경되었습니다. 앱을 재시작하면 적용됩니다!", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.theme2).setOnClickListener {
            database = getSharedPreferences("database", MODE_PRIVATE);
            databaseEditor = database.edit();

            databaseEditor.putString("THEME", "color2")
            databaseEditor.apply();
            databaseEditor.commit();
            Toast.makeText(this, "테마가 2로 변경되었습니다. 앱을 재시작하면 적용됩니다!", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.theme3).setOnClickListener {
            database = getSharedPreferences("database", MODE_PRIVATE);
            databaseEditor = database.edit();

            databaseEditor.putString("THEME", "color3")
            databaseEditor.apply();
            databaseEditor.commit();
            Toast.makeText(this, "테마가 3으로 변경되었습니다. 앱을 재시작하면 적용됩니다!", Toast.LENGTH_SHORT).show()
        }
    }

}