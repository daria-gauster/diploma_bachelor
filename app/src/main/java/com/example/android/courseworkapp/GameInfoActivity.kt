package com.example.android.courseworkapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.android.courseworkapp.databinding.ActivityGameInfoBinding


private const val TAG = "GameInfoActivity"

class GameInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_info)
        binding = ActivityGameInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbarGameInfo))
        //home navigation
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Manage Games"
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.home -> {
            Log.d(TAG, "Closed dashboard activity")
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gameinfo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.menuAdd -> {
//            Toast.makeText(this, "Add action", Toast.LENGTH_LONG).show()
//            true
//        }
//
//        else -> {
//            // If we got here, the user's action was not recognized.
//            // Invoke the superclass to handle it.
//            super.onOptionsItemSelected(item)
//        }
//    }

}
