package com.htc.luminaos.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.htc.luminaos.databinding.ActivitySupportBinding
import com.htc.luminaos.utils.LogUtils
import com.htc.luminaos.utils.Utils
import java.io.File

class SupportActivity : AppCompatActivity() {
    lateinit var supportBinding: ActivitySupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportBinding = ActivitySupportBinding.inflate(LayoutInflater.from(this))
        setContentView(supportBinding.root)
    }

    override fun onResume() {
        super.onResume()
        val file = File(Utils.support_image_path)
        if (file.exists()) {
            Glide.with(this)
                .load(file)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        supportBinding.rlMain.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // 可选：清除背景或设置占位图
                    }
                })
        } else {
            LogUtils.e("SupportActivity", "File not found: " + Utils.support_image_path)
        }
    }
}