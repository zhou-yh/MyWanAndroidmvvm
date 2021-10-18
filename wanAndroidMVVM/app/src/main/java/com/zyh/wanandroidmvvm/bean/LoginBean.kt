package com.zyh.wanandroidmvvm.bean


/**
 * author: zhouyh
 * created on: 2021/9/10 10:00 下午
 * description:
 */
data class LoginBean(var data: DataBean? = null,
                     var errorCode: Int = 0,
                     var errorMsg: String? = null) {


    /**
     * data : {"chapterTops":[],"collectIds":[7360,7357,7356],"email":"","icon":"","id":11730,"password":"111111","token":"","type":0,"username":"ALEXALEX"}
     * errorCode : 0
     * errorMsg :
     */

    data class DataBean(var email: String? = null,
                        var icon: String? = null,
                        var id: Int = 0,
                        var password: String? = null,
                        var token: String? = null,
                        var type: Int = 0,
                        var username: String? = null,
                        var chapterTops: List<*>? = null,
                        var collectIds: List<Int>? = null)



}
