package com.zyh.lib_base.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV


/**
 * author: zhouyh
 * created on: 2021/9/14 9:00 上午
 * description:缓存封装类
 */
object SpHelper {

    private var mv: MMKV = MMKV.defaultMMKV()

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param value
     */
    fun encode(key: String?, value: Any) {
        when (value) {
            is String -> {
                mv.encode(key, value)
            }
            is Int -> {
                mv.encode(key, value)
            }
            is Boolean -> {
                mv.encode(key, value)
            }
            is Float -> {
                mv.encode(key, value)
            }
            is Long -> {
                mv.encode(key, value)
            }
            is Double -> {
                mv.encode(key, value)
            }
            is ByteArray -> {
                mv.encode(key, value)
            }
            else -> {
                mv.encode(key, value.toString())
            }
        }
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    fun decodeInt(key: String?): Int {
        return mv.decodeInt(key, 0)
    }

    fun decodeString(key: String?): String {
        return mv.decodeString(key, "")
    }

    fun decodeDouble(key: String?): Double{
        return mv.decodeDouble(key, 0.0)
    }

    fun decodeBoolean(key: String?): Boolean {
        return mv.decodeBool(key, false)
    }

    fun decodeBoolean(key: String?, def: Boolean): Boolean {
        return mv.decodeBool(key, def)
    }


    fun decodeFloat(key: String?): Float {
        return mv.decodeFloat(key, 0f)
    }

    fun decodeBytes(key: String?): ByteArray {
        return mv.decodeBytes(key)
    }

    fun decodeStringSet(key: String?): Set<String> {
        return mv.decodeStringSet(key, linkedSetOf())
    }

    fun <T : Parcelable> decodeParcelable(key: String?, tClass: Class<T>): Parcelable? {
        return mv.decodeParcelable(key, tClass)
    }

    fun clearAll() {
        mv.clearAll()
    }


}