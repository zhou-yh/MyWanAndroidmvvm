package com.zyh.lib_base.extension

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.lxj.xpopup.interfaces.XPopupImageLoader
import com.zyh.lib_base.R
import java.io.File


/**
 * author: zhouyh
 * created on: 2021/9/21 11:46 上午
 * description: 图片弹框扩展类
 */
class ImagePopLoader:XPopupImageLoader {

    override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
        //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
        Glide.with(imageView).load(uri).apply {
            RequestOptions.placeholderOf(R.drawable.default_background)
                .override(Target.SIZE_ORIGINAL)
        }.into(imageView)
    }

    /**
     * 获取图片文件
     */
    override fun getImageFile(context: Context, uri: Any): File {
        return Glide.with(context).downloadOnly().load(uri).submit().get()
    }
}