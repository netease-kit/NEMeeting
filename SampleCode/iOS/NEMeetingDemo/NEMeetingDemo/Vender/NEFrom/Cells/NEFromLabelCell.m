//
//  NEFromLabelCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromLabelCell.h"

@implementation NEFromLabelCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.titleLabel.frame = CGRectMake(20, 0, self.width - 20*2, self.height);
}

- (void)doInit {
    [super doInit];
    [self addSubview:self.titleLabel];
}

- (void)refreshCellWithRow:(NEFromRow *)row {
    [super refreshCellWithRow:row];
    self.titleLabel.text = row.title;
}

@end
