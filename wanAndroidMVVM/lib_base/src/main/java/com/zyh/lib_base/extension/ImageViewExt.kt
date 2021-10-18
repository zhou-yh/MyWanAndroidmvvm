package com.zyh.lib_base.extension

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.blankj.utilcode.util.ResourceUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zyh.lib_base.R


/**
 * author: zhouyh
 * created on: 2021/9/19 12:33 下午
 * description:ImageView扩展函数
 */
fun ImageView.loadImage(url: String) {
    Glide.with(this).load(url).apply(
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .dontAnimate()
            .placeholder(R.drawable.ic_placeholder)
    ).thumbnail(0.6f)  //进行压缩
        .into(this)
}

fun ImageView.loadUrl(url: String?, placeHolderRes: Drawable, errorHolder: Drawable?= ResourceUtils.getDrawable(R.drawable.ic_error_placeholder)){
    Glide.with(this)
        .load(url)
        .apply(
            RequestOptions().placeholder(placeHolderRes).error(errorHolder)
        )
        .into(this)
}


