// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#ifndef NEMEETINGSDKMANAGER_H
#define NEMEETINGSDKMANAGER_H

#include "stable.h"

USING_NS_NNEM_SDK_INTERFACE

// define------------------------------------------------------------

struct NEMDefineMeetingKitConfig {
  std::string appKey;
  std::string appName;
  bool useAssetServerConfig;
  std::string broadcastAppGroup;
  std::map<std::string, std::string> extras;
  std::string language;
  std::string serverUrl;
  std::string corpCode;
  std::string corpEmail;
  std::string imIM;
  std::string serverConfig;
  int height;
  int width;
};

class NEMWrapperMeetingKit {
 public:
  explicit NEMWrapperMeetingKit();
  ~NEMWrapperMeetingKit();

 public:
  void initialize(const NEMeetingKitConfig& config, const NEEmptyCallback& cb);
  void unInitialize(const NEMeetingKit::NEUnInitializeCallback& cb);
  void setLogHandler(const std::function<void(int level, const std::string& log)>& cb);

  // account service
  void loginByPassword(std::string userUuid, std::string password, const NEEmptyCallback& cb);
  void logout(const NEEmptyCallback& cb);

  // meeting service
  void startMeeting(const NEStartMeetingParams& param, const NEStartMeetingOptions& opts, const NEEmptyCallback& cb);
  void joinMeeting(const NEJoinMeetingParams& param, const NEJoinMeetingOptions& opts, const NEEmptyCallback& cb);
};

#endif