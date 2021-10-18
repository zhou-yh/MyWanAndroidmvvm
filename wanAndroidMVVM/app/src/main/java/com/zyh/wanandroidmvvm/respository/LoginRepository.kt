package com.zyh.wanandroidmvvm.respository

import androidx.lifecycle.MutableLiveData
import com.zyh.wanandroidmvvm.bean.LoginBean


/**
 * author: zhouyh
 * created on: 2021/9/10 9:54 下午
 * description: 登录仓库
 */
class LoginRepository {

    var login = MutableLiveData<LoginBean>()
    var register = MutableLiveData<LoginBean>()

    fun login(account:String,pwd:String){

    }

}