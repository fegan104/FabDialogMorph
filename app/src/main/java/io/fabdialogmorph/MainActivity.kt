package io.fabdialogmorph

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val RC_LOGIN = 100

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, DialogActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, fab, getString(R.string.transition_dialog))
            startActivityForResult(intent, RC_LOGIN, options.toBundle())
        }
    }

}
