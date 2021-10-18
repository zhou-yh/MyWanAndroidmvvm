package com.zyh.lib_base.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.zyh.lib_base.data.bean.TodoBean


/**
 * author: zhouyh
 * created on: 2021/9/14 10:59 下午
 * description:
 */
data class MainEvent(val msg: String?):LiveEvent
data class TokenExpiredEvent(val msg: String?)
data class RegisterSuccessEvent(val account: String?,val pwd: String?):LiveEvent
data class SearchHistoryEvent(val code:Int):LiveEvent
data class LogoutEvent(val code:Int):LiveEvent
data class LoginSuccessEvent(val code:Int):LiveEvent
data class RefreshUserFmEvent(val code:Int):LiveEvent
data class RefreshWebListEvent(val code:Int):LiveEvent
data class RefreshCollectStateEvent(val originId:Int):LiveEvent
data class SwitchReadHistoryEvent(val checked: Boolean):LiveEvent
data class TodoListRefreshEvent(val code: Int, val todoInfo: TodoBean.Data):LiveEvent


