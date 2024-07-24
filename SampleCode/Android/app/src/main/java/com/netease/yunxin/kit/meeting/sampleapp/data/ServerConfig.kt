/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.kit.meeting.sampleapp.data
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils

data class ServerConfig(
    val name: String,
    val appKey: String,
    val serverUrl: String? = null,
    val isDebugMode: Boolean = false,
    val useAssetServerConfig: Boolean = false
)

object ServerConfigs {

    fun determineServerConfig(appKey: String): ServerConfig {
        val serverConfig = ServerConfig("online", appKey = appKey)

        return serverConfig.copy(
            appKey = SPUtils.getInstance().getString("custom_app_key").ifEmpty { serverConfig.appKey },
            serverUrl = SPUtils.getInstance().getString("custom_sdk_server_url").ifEmpty { serverConfig.serverUrl }
        )
    }
}
