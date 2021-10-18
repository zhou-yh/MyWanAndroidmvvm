package com.zyh.wanandroidmvvm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.czl.lib_base.config.AppConstants
import com.zyh.wanandroidmvvm.databinding.ActivityMainBinding

@Route(path = AppConstants.Router.App.F_MAIN)
class MainActivity : AppCompatActivity() {

    private var btn : Button ?= null
    private var activityMainBinding : ActivityMainBinding?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(activityMainBinding!!.root)
        btn = findViewById<View>(R.id.btn) as Button
        btn!!.setOnClickListener {
            startActivity(Intent(this,SplashActivity::class.java))
        }



    }
}