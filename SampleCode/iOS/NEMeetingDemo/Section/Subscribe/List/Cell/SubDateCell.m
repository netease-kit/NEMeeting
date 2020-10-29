//
//  SubDateCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "SubDateCell.h"

@interface SubDateCell ()

@property (nonatomic, strong) NEDateItem *item;
@property (weak, nonatomic) IBOutlet UILabel *dayLabel;
@property (weak, nonatomic) IBOutlet UILabel *monthLabel;

@end

@implementation SubDateCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.userInteractionEnabled = NO;
    }
    return self;
}

- (void)refreshWithDateItem:(NEDateItem *)item {
    _item = item;
    _dayLabel.text = @(item.day).stringValue;
    _monthLabel.text = @(item.month).stringValue;
}

@end
