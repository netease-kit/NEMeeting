package com.netease.yunxin.kit.meeting.sampleapp.data
import com.netease.yunxin.kit.meeting.sampleapp.utils.SPUtils

data class ServerConfig(
    val appKey: String,
    val appServerUrl: String,
    val sdkServerUrl: String,
)

object ServerConfigs {

    fun determineServerConfig(appKey: String): ServerConfig {

        val serverConfig = ServerConfig(appKey = appKey, appServerUrl = "", sdkServerUrl = "")

        return ServerConfig(
            appKey = SPUtils.getInstance().getString("custom_app_key").ifEmpty { serverConfig.appKey },
            appServerUrl = SPUtils.getInstance().getString("custom_app_server_url").ifEmpty { serverConfig.appServerUrl },
            sdkServerUrl = SPUtils.getInstance().getString("custom_sdk_server_url").ifEmpty { serverConfig.sdkServerUrl },
        )
    }

}
