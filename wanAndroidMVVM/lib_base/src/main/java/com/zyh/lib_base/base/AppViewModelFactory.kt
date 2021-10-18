package com.zyh.lib_base.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zyh.lib_base.data.DataRepository
import java.lang.reflect.InvocationTargetException


/**
 * author: zhouyh
 * created on: 2021/5/11 4:55 下午
 * description: 通过反射动态实例化
 * viewModel工厂
 */
class AppViewModelFactory(
    private val mApplication: MyApplication,
    private val mRepository: DataRepository
    ): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        //反射动态实例化ViewModel
        return try {
            val className = modelClass.canonicalName
            val classViewModel = Class.forName(className!!)
            val cons = classViewModel.getConstructor(MyApplication::class.java, DataRepository::class.java)
            val viewModel = cons.newInstance(mApplication,mRepository) as ViewModel
            viewModel as T
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: InstantiationException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}