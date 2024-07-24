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
    val serverUrl: String,
    val isDebugMode: Boolean = false,
    val useAssetServerConfig: Boolean = false
)

object ServerConfigs {

    private val SERVER_CONFIGS: Map<String, ServerConfig> = listOf(
        ServerConfig(
            name = "online",
            appKey = "91d597b20132e6fa131615aa2d229388",
            serverUrl = ""
        ),
        ServerConfig(
            name = "test",
            appKey = "4649991c6ab7cc5a4309ccf25d8793e5",
            serverUrl = "https://roomkit-dev.netease.im/",
            isDebugMode = true
        ),
        ServerConfig(
            name = "private",
            appKey = "ff965d13edd293d22bcf78af1a621bcb",
            serverUrl = "",
            useAssetServerConfig = true
        )
    ).associateBy { it.name }

    fun determineServerConfig(appKey: String): ServerConfig {
        // val serverConfig = ServerConfig(appKey = appKey, appServerUrl = "", sdkServerUrl = "")
        val serverConfig = SERVER_CONFIGS[SPUtils.getInstance().getString("server_type", "test")]!!

        return serverConfig.copy(
            appKey = SPUtils.getInstance().getString("custom_app_key").ifEmpty { serverConfig.appKey },
            serverUrl = SPUtils.getInstance().getString("custom_sdk_server_url").ifEmpty { serverConfig.serverUrl }
        )
    }
}
