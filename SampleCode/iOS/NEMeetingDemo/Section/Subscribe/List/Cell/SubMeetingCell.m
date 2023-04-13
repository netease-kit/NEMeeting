// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "SubMeetingCell.h"

@interface SubMeetingCell ()

@property (nonatomic, strong) NEMeetingItem *item;

@property (weak, nonatomic) IBOutlet UILabel *startTimeLabel;
@property (weak, nonatomic) IBOutlet UILabel *meetingIdLabel;
@property (weak, nonatomic) IBOutlet UILabel *meetingNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

@end

@implementation SubMeetingCell

- (void)awakeFromNib {
    [super awakeFromNib];
    //self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)refreshWithItem:(NEMeetingItem *)item {
    _item = item;
    NSDateComponents *dateComponents = [self dateComponentsWithTimestamp:item.startTime/1000];
    _startTimeLabel.text = [NSString stringWithFormat:@"%d:%02d",
                            (int)dateComponents.hour, (int)dateComponents.minute];
    _meetingIdLabel.text = [NSString stringWithFormat:@"会议号：%@", item.meetingNum];
    _meetingNameLabel.text = item.subject ?: @"";
    _statusLabel.text = [self meetingStatusContent];
}

- (NSDateComponents *)dateComponentsWithTimestamp:(uint64_t)timestamp {
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSCalendarUnit components = (NSCalendarUnit)(NSCalendarUnitYear|
                                                 NSCalendarUnitMonth|
                                                 NSCalendarUnitDay|
                                                 NSCalendarUnitWeekday|
                                                 NSCalendarUnitHour|
                                                 NSCalendarUnitMinute);
    NSDateComponents *dateComponents = [[NSCalendar currentCalendar] components:components fromDate:date];
    return dateComponents;
}

- (NSString *)meetingStatusContent {
    NSString *ret = @"未知";
    switch (_item.status) {
        case NEMeetingItemStatusInit: {
            ret = @"未开始";
            break;
        }
        case NEMeetingItemStatusStarted:{
            ret = @"进行中";
            break;
        }
        case NEMeetingItemStatusEnded:{
            ret = @"已结束";
            break;
        }
        case NEMeetingItemStatusInvalid:{
            ret = @"无效";
            break;
        }
        case NEMeetingItemStatusCancel: {
            ret = @"已取消";
            break;
        }
        case NEMeetingItemStatusRecycled:
        {
            ret = @"已回收";
            break;
        }
        default:
            break;
    }
    return ret;
}

@end
