package com.example.testquestion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("name")
        titleText.text = name.toString()

        val body = intent.getStringExtra("body")
        bodyText.text = body.toString()

        val actionbar = supportActionBar
        actionbar!!.title = "Detail Post"
        actionbar.setDisplayHomeAsUpEnabled(true)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
