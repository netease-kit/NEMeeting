/*
 * Copyright (c) 2022 NetEase, Inc. All rights reserved.
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.netease.yunxin.kit.meeting.sampleapp.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    /**
     * 递归拷贝Assets文件文件到SD卡
     * @param context 上下文
     * @param assetsPath assets 文件夹中的目录
     * @param dstPath sd卡磁盘目录
     */
    fun copyAssetsToDst(context: Context, assetsPath: String, dstPath: String) {
        try {
            val assetsFileNames = context.assets.list(assetsPath)
            val dstFile = File(dstPath)
            if (assetsFileNames != null && assetsFileNames.isNotEmpty()) {
                if (!dstFile.exists()) {
                    dstFile.mkdirs()
                }
                for (assetsFileName in assetsFileNames) {
                    if (assetsPath != "") {
                        // assets 文件夹下的目录
                        copyAssetsToDst(context, assetsPath + File.separator + assetsFileName, dstPath + File.separator + assetsFileName)
                    } else {
                        // assets 文件夹
                        copyAssetsToDst(context, assetsFileName, dstPath + File.separator + assetsFileName)
                    }
                }
            } else {
                if (!dstFile.exists()) {
                    // 当文件不存在的时候copy
                    val inputStream = context.assets.open(assetsPath)
                    val fileOutputStream = FileOutputStream(dstFile)
                    val buffer = ByteArray(1024)
                    var byteCount: Int
                    while (inputStream.read(buffer).also { byteCount = it } != -1) {
                        fileOutputStream.write(buffer, 0, byteCount)
                    }
                    fileOutputStream.flush()
                    inputStream.close()
                    fileOutputStream.close()
                } else {
                    Log.i("copyAssetsToDst", "文件已经存在:${dstFile.path}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
