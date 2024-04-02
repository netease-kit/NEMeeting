// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "TimerButton.h"

@interface TimerButton () {
  dispatch_source_t _timer;
}
@property(nonatomic, assign) int64_t count;
@end

@implementation TimerButton

- (void)willMoveToSuperview:(UIView *)newSuperview {
  [super willMoveToSuperview:newSuperview];
  if (!newSuperview) {
    [self stopTimer];
    _startTime = 0;
    _count = 0;
  }
}

- (instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    self.backgroundColor = [UIColor redColor];
    self.titleLabel.font = [UIFont systemFontOfSize:11.0];
  }
  return self;
}

- (void)setStartTime:(int64_t)startTime {
  _startTime = startTime;
  _count = startTime;
  [self startTimer];
}

- (void)setHidden:(BOOL)hidden {
  [super setHidden:hidden];
  if (hidden) {
    [self stopTimer];
    _startTime = 0;
    _count = 0;
  }
}

- (void)startTimer {
  [self stopTimer];

  __weak typeof(self) weakSelf = self;
  _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, dispatch_get_main_queue());
  dispatch_source_set_timer(_timer, DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC, 0);
  dispatch_source_set_event_handler(_timer, ^{
    weakSelf.count++;
    [weakSelf updateTitle];
  });
  dispatch_resume(_timer);
}

- (void)stopTimer {
  if (_timer) {
    dispatch_cancel(_timer);
    _timer = nil;
  }
}

- (void)updateTitle {
  NSInteger hour = _count / (60 * 60);
  NSInteger minute = (_count % (60 * 60)) / 60;
  NSInteger second = (_count % 60);
  NSString *content =
      [NSString stringWithFormat:@"%d:%02d%02d", (int)hour, (int)minute, (int)second];
  if (_neTitle.length != 0) {
    content = [NSString
        stringWithFormat:@"%@  %d:%02d:%02d", _neTitle, (int)hour, (int)minute, (int)second];
  }
  [self setTitle:content forState:UIControlStateNormal];
}

@end
