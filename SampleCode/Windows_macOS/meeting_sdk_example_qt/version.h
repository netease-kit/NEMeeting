// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#ifndef VERSION_H
#define VERSION_H

#include <QtGlobal>

#define APPLICATION_VERSION "."
#define VERSION_COUNTER \
    $ { VERSION_COUNTER }
#define COMMIT_HASH ""

#define LOCAL_DEFAULT_SERVER_ADDRESS ""
#define LOCAL_DEFAULT_APPKEY ""
#define LOCAL_DEFAULT_APPKEY_SSO ""
#define LOCAL_DEFAULT_UPDATE_SERVER_ADDRESS ""
#define LOCAL_DEFAULT_APPCONFIGS_SERVER_ADDRESS ""

#define LOCAL_DEFAULT_SERVER_ADDRESS_TEST ""
#define LOCAL_DEFAULT_APPKEY_TEST ""
#define LOCAL_DEFAULT_APPKEY_TEST_SSO ""
#define LOCAL_DEFAULT_UPDATE_SERVER_ADDRESS_TEST ""
#define LOCAL_DEFAULT_APPCONFIGS_SERVER_ADDRESS_TEST "h"

#ifdef Q_OS_MACX
#define MEETING_CLIENT_TYPE "5"
#endif
#ifdef Q_OS_WIN32
#define MEETING_CLIENT_TYPE "4"
#endif
#ifdef __linux__
#define MEETING_CLIENT_TYPE "3"
#endif
#endif // VERSION_H
