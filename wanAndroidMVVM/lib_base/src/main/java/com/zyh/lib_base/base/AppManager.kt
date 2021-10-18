package com.zyh.lib_base.base

import android.app.Activity
import androidx.fragment.app.Fragment
import java.util.*


/**
 * author: zhouyh
 * created on: 2021/5/11 3:52 下午
 * description:应用栈管理类
 */
class AppManager private constructor(){

    /**
     * 伴生对象，只加载一次，使用的时候才初始化
     */
    companion object{
        var activityTasks : Stack<Activity> ?= null
            private set  // the setter is private and has the default implementation
        var fragmentTasks : Stack<Fragment> ?= null
            private set

        /**
         * 单例
         */
        val instance : AppManager by lazy {AppManager()}
    }

    /**
     * 添加activity
     */
    fun addActivity(activity: Activity){
        if (activityTasks==null){
            activityTasks = Stack();
        }
        activityTasks!!.add(activity)
    }

    /**
     * 移除activity
     */
    fun removeActivity(activity: Activity){
        if (activity != null){
            activityTasks!!.remove(activity)
        }
    }

    /**
     * 是否有activity
     */
    val isActivity : Boolean
        get() = if (activityTasks!=null){
            !activityTasks!!.isEmpty()
        }else false

    /**
     * 获取当前currentActivity(最后一个压入栈的）
     */
    fun currentActivity() : Activity{
        return activityTasks!!.lastElement()
    }

    fun finishActivity(){
        val activity = activityTasks!!.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity){
        if (activity!=null){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
    }

    /**
     * 结束指定类名的activity
     */
    fun finishActivity(cls : Class<*>){
        for (activty in activityTasks!!){
            if (activty.javaClass == cls){
                finishActivity(activty)
                break
            }
        }
    }

    /**
     * 结束所有的activity
     */
    fun finishAllActivity(){
        for (activity in activityTasks!!){
            finishActivity(activity)
        }
    }


    /**
     * 获得指定的activity
     */
    fun getActivity(cls: Class<*>) : Activity? {
        if (activityTasks!=null) for (activity in activityTasks!!){
            if (activity.javaClass == cls){
                return activity
            }
        }
        return null
    }


    fun addFragment(fragment: Fragment){
        if (fragmentTasks == null){
            fragmentTasks = Stack()
        }
        fragmentTasks!!.add(fragment)
    }

    fun removeFragment(fragment: Fragment){
        if (fragment!=null){
            fragmentTasks!!.remove(fragment)
        }
    }

    /**
     * 是否有fragment
     */
    val isFragment : Boolean
        get() = if (fragmentTasks!=null){
            fragmentTasks!!.isEmpty()
        }else false

    fun currentFragment() : Fragment?{
        return if (fragmentTasks!=null){
            fragmentTasks!!.lastElement()
        }else null
    }

    /**
     * 退出程序
     */
    fun appExit(){
        try {
            finishAllActivity()
            //杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid())
        }catch (e : Exception){
            activityTasks!!.clear()
            e.printStackTrace()
        }



    }



}