package com.demo.pizzanbeer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.pizzanbeer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        startListFragment()

    }
    private fun startListFragment() {
        supportFragmentManager.beginTransaction().apply {
            val listFragment = ListFragment()
            add(activityMainBinding.fragmentContainer.id, listFragment)
            addToBackStack(null)
            commit()
        }
    }
}