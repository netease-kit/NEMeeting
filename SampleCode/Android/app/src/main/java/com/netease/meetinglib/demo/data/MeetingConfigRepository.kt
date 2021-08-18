package com.netease.meetinglib.demo.data

import com.netease.meetinglib.demo.utils.SPUtils

object MeetingConfigRepository {

    private const val MEETING_CONFIG_SP_NAME = "meeting-config"

    private val localStorage: SPUtils by lazy {
        SPUtils.getInstance(MEETING_CONFIG_SP_NAME)
    }

    var joinTimeout: Int
        get() = localStorage.getInt("joinTimeout", 45 * 1000)
        set(value) = localStorage.put("joinTimeout", value)

}