// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "CustomEncryptObserver.h"

@implementation CustomEncryptObserver
// 发送音频包回调
- (BOOL)onSendAudioPacket:(NERtcPacket *)packet {
  if ([[NSUserDefaults standardUserDefaults] boolForKey:@"audioEncrypt"]) {
    unsigned char *p = packet.buffer;
    unsigned char *temp = (unsigned char *)malloc(packet.size);
    uint64_t dataLength = packet.size;
    for (int i = 0; i < dataLength; i++) {
      temp[i] = ~p[i];
    }
    memcpy((unsigned char *)packet.buffer, temp, dataLength);
    free(temp);
  }
  return YES;
}
// 发送视频包回调
- (BOOL)onSendVideoPacket:(NERtcPacket *)packet {
  if ([[NSUserDefaults standardUserDefaults] boolForKey:@"videoEncrypt"]) {
    unsigned char *p = packet.buffer;
    unsigned char *temp = (unsigned char *)malloc(packet.size);
    uint64_t dataLength = packet.size;
    for (int i = 0; i < dataLength; i++) {
      temp[i] = ~p[i];
    }
    memcpy((unsigned char *)packet.buffer, temp, dataLength);
    free(temp);
  }
  return YES;
}
// 接收音频包回调
- (BOOL)onReceiveAudioPacket:(NERtcPacket *)packet {
  if ([[NSUserDefaults standardUserDefaults] boolForKey:@"audioEncrypt"]) {
    unsigned char *p = packet.buffer;
    unsigned char *temp = (unsigned char *)malloc(packet.size);
    uint64_t dataLength = packet.size;
    for (int i = 0; i < dataLength; i++) {
      temp[i] = ~p[i];
    }
    memcpy((unsigned char *)packet.buffer, temp, dataLength);
    free(temp);
  }
  return YES;
}
// 接收视频包回调
- (BOOL)onReceiveVideoPacket:(NERtcPacket *)packet {
  if ([[NSUserDefaults standardUserDefaults] boolForKey:@"videoEncrypt"]) {
    unsigned char *p = packet.buffer;
    unsigned char *temp = (unsigned char *)malloc(packet.size);
    uint64_t dataLength = packet.size;
    for (int i = 0; i < dataLength; i++) {
      temp[i] = ~p[i];
    }
    memcpy((unsigned char *)packet.buffer, temp, dataLength);
    free(temp);
  }
  return YES;
}
@end
