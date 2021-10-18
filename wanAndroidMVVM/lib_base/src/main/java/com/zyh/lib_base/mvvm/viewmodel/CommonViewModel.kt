package com.zyh.lib_base.mvvm.viewmodel

import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.data.DataRepository


/**
 * @author Alwyn
 * @Date 2020/10/19
 * @Description
 */
class CommonViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application,model) {
}