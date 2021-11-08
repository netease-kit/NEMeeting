//
//  NEFromTitleSwitchCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromTitleSwitchCell.h"

@interface NEFromTitleSwitchCell ()

@property (nonatomic, strong) UILabel *subTitleLabel;
@property (nonatomic, strong) UISwitch *switchView;

@end

@implementation NEFromTitleSwitchCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)doInit {
    [super doInit];
    [self.contentView addSubview:self.titleLabel];
    [self.contentView addSubview:self.subTitleLabel];
    [self.contentView addSubview:self.switchView];
}

- (void)refreshCellWithRow:(NEFromRow *)row {
    [super refreshCellWithRow:row];
    self.titleLabel.text = row.title;
    self.subTitleLabel.text = row.subTitle ?: @"";
    if (row.subTitle.length != 0) {
        if (!self.subTitleLabel.isHidden) {
            self.subTitleLabel.hidden = NO;
            [self setNeedsLayout];
        }
    } else {
        if (!self.subTitleLabel.isHidden) {
            self.subTitleLabel.hidden = YES;
            [self setNeedsLayout];
        }
    }
    self.switchView.hidden = row.hideRightItem;
    self.switchView.on = [row.value boolValue];
}

- (void)layoutSubviews {
    _switchView.centerY = self.height/2;
    _switchView.left = self.width - _switchView.width - 20;
    
    if (_subTitleLabel.hidden) {
        self.titleLabel.left = 20;
        self.titleLabel.centerY = self.height/2;
        self.titleLabel.width = _switchView.left - self.titleLabel.left - 16;
    } else {
        self.titleLabel.left = 20;
        self.titleLabel.width = _switchView.left - self.titleLabel.left - 16;
        self.titleLabel.top = (self.height - self.titleLabel.height - 4.0 - _subTitleLabel.height)/2;
        self.subTitleLabel.left = self.titleLabel.left;
        self.subTitleLabel.width = self.titleLabel.width;
        self.subTitleLabel.top = self.titleLabel.bottom + 4.0;
    }
}

#pragma mark - Action
- (void)onValueChanged:(UISwitch *)sender {
    self.row.value = @(sender.on);
}

#pragma mark - Getter
- (UILabel *)subTitleLabel {
    if (!_subTitleLabel) {
        _subTitleLabel = [[UILabel alloc] init];
        _subTitleLabel.font = [UIFont systemFontOfSize:12];
        _subTitleLabel.textColor = [UIColor lightGrayColor];
        _subTitleLabel.text = @"副标题";
        [_subTitleLabel sizeToFit];
    }
    return _subTitleLabel;
}

- (UISwitch *)switchView {
    if (!_switchView) {
        _switchView = [[UISwitch alloc] init];
        [_switchView addTarget:self
                        action:@selector(onValueChanged:)
              forControlEvents:UIControlEventValueChanged];
    }
    return _switchView;
}

@end
