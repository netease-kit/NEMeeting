package com.netease.yunxin.kit.meeting.sampleapp.data

import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils

object MeetingConfigRepository {

    private const val MEETING_CONFIG_SP_NAME = "meeting-config"

    private val localStorage: SPUtils by lazy {
        SPUtils.getInstance(MEETING_CONFIG_SP_NAME)
    }

    var joinTimeout: Int
        get() = localStorage.getInt("joinTimeout", 45 * 1000)
        set(value) = localStorage.put("joinTimeout", value)

    var audioProfile: String
        get() = localStorage.getString("audioProfile", "default")
        set(value) { localStorage.put("audioProfile", value) }

    val isSpeechAudioMode: Boolean
        get() = audioProfile == "speech"

    val isMusicAudioMode: Boolean
        get() = audioProfile == "music"
}