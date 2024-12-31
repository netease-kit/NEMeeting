#include "nemeeting_sdk_manager.h"

// NEMeetingKitConfig transform(const NEMDefineMeetingKitConfig& config)
// {
//     NEMeetingKitConfig ret;
//     ret.appKey = config.appKey;
//     ret.serverURL = config.serverURL;
//     ret.sdkPath = config.sdkPath;
//     return ret;
// }

NEMWrapperMeetingKit::NEMWrapperMeetingKit() {}

NEMWrapperMeetingKit::~NEMWrapperMeetingKit() {}

void NEMWrapperMeetingKit::initialize(const NEMeetingKitConfig& config, const NEEmptyCallback& cb) {
  auto pMeetingSDK = NEMeetingKit::getInstance();
  pMeetingSDK->initialize(config, cb);
}
void NEMWrapperMeetingKit::unInitialize(const NEEmptyCallback& cb) {
  auto pMeetingSDK = NEMeetingKit::getInstance();
  pMeetingSDK->unInitialize(cb);
}
void NEMWrapperMeetingKit::setLogHandler(const std::function<void(int level, const std::string& log)>& cb) {
  auto pMeetingSDK = NEMeetingKit::getInstance();
  pMeetingSDK->setLogHandler(cb);
}

// account service ------------------------------------------------------------
void NEMWrapperMeetingKit::loginByPassword(std::string userUuid, std::string password, const NEEmptyCallback& cb) {
  auto service = NEMeetingKit::getInstance()->getAuthService();
  if (service) {
    service->login(userUuid, password, cb);
  } else {
    if (cb) {
      cb(NEErrorCode::ERROR_CODE_SDK_UNINITIALIZE, "MeetingKit is not initialized");
    }
  }
}
void NEMWrapperMeetingKit::logout(const NEEmptyCallback& cb) {
  auto service = NEMeetingKit::getInstance()->getAuthService();
  if (service) {
    service->logout(false, cb);
  } else {
    if (cb) {
      cb(NEErrorCode::ERROR_CODE_SDK_UNINITIALIZE, "MeetingKit is not initialized");
    }
  }
}

// meeting service ------------------------------------------------------------
void NEMWrapperMeetingKit::startMeeting(const NEStartMeetingParams& param,
                                        const NEStartMeetingOptions& opts,
                                        const NEEmptyCallback& cb) {
  auto service = NEMeetingKit::getInstance()->getMeetingService();
  if (service) {
    service->startMeeting(param, opts, cb);
  } else {
    if (cb) {
      cb(NEErrorCode::ERROR_CODE_SDK_UNINITIALIZE, "MeetingKit is not initialized");
    }
  }
}
void NEMWrapperMeetingKit::joinMeeting(const NEJoinMeetingParams& param,
                                       const NEJoinMeetingOptions& opts,
                                       const NEEmptyCallback& cb) {
  auto service = NEMeetingKit::getInstance()->getMeetingService();
  if (service) {
    service->joinMeeting(param, opts, cb);
  } else {
    if (cb) {
      cb(NEErrorCode::ERROR_CODE_SDK_UNINITIALIZE, "MeetingKit is not initialized");
    }
  }
}
