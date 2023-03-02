/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.kit.meeting.sampleapp.data

import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioProfileType
import com.netease.yunxin.kit.meeting.sdk.media.NEAudioScenarioType

object MeetingConfigRepository {

    private const val MEETING_CONFIG_SP_NAME = "meeting-config"

    private val localStorage: SPUtils by lazy {
        SPUtils.getInstance(MEETING_CONFIG_SP_NAME)
    }

    var joinTimeout: Int
        get() = localStorage.getInt("joinTimeout", 45 * 1000)
        set(value) = localStorage.put("joinTimeout", value)

    var enableAudioOptions: Boolean
        get() = localStorage.getBoolean("enable_audio_options", false)
        set(value) { localStorage.put("enable_audio_options", value) }

    var audioProfile: String
        get() = localStorage.getString("audioProfile", NEAudioProfileType.DEFAULT.toString())
        set(value) { localStorage.put("audioProfile", value) }

    var audioScenario: String
        get() = localStorage.getString("audioScenario", NEAudioScenarioType.DEFAULT.toString())
        set(value) { localStorage.put("audioScenario", value) }
}
