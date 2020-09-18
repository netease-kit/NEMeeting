//
//  NEFromTitleDateCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromTitleDateCell.h"
#import "NEFromDatePicker.h"

@interface NEFromTitleDateCell ()

@property (nonatomic, strong) UIButton *dateBtn;

@property (nonatomic, strong) NSDate *date;

@property (nonatomic, strong) NSDate *minDate;

@property (nonatomic, strong) NSDate *maxDate;

@end

@implementation NEFromTitleDateCell

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
    
    self.titleLabel.left = 20;
    self.titleLabel.centerY = self.height/2.0;
    if (self.accessoryType == UITableViewCellAccessoryNone) {
        self.dateBtn.frame = CGRectMake(self.width - 162, 0, 162, self.height);
    } else {
        self.dateBtn.frame = CGRectMake(self.width - 188, 0, 175, self.height);
    }
}

- (void)doInit {
    [super doInit];
    [self addSubview:self.titleLabel];
    [self addSubview:self.dateBtn];
}

- (void)refreshCellWithRow:(NEFromRow *)row {
    [super refreshCellWithRow:row];
    
    self.titleLabel.text = row.title ?: @"";
    [self.titleLabel sizeToFit];
    
    uint64_t timestamp = [row.value longLongValue];
    _date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    
    if (row.config[@"minDate"]) {
        _minDate = [NSDate dateWithTimeIntervalSince1970:[row.config[@"minDate"] longLongValue]];
    }
    if (row.config[@"maxDate"]) {
        _maxDate = [NSDate dateWithTimeIntervalSince1970:[row.config[@"maxDate"] longLongValue]];
    }
    
    NSString *timeStr = [self timeStrWithTimestamp:timestamp];
    [_dateBtn setTitle:timeStr forState:UIControlStateNormal];
}

#pragma mark - Private
- (NSString *)timeStrWithTimestamp:(uint64_t)timestamp {
    if (timestamp == 0) {
        return @"";
    }
    NSString *formatStr =@"yyyy-MM-dd HH:mm";
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:formatStr];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSString *time = [formatter stringFromDate:date];
    return time;
}

#pragma mark - Action
- (void)onDateAction:(UIButton *)sender {
    __weak typeof(self) weakSelf = self;
    [NEFromDatePickerBar showWithDate:_date
                              minDate:_minDate
                              maxDate:_maxDate
                             selected:^(NSDate * _Nonnull date) {
        uint64_t timestamp = [date timeIntervalSince1970];
        NSString *timeStr = [weakSelf timeStrWithTimestamp:timestamp];
        [weakSelf.dateBtn setTitle:timeStr forState:UIControlStateNormal];
        weakSelf.row.value = @(timestamp);
        weakSelf.date = date;
    }];
}

#pragma mark - Getter
- (UIButton *)dateBtn {
    if (!_dateBtn) {
        _dateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_dateBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        _dateBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_dateBtn addTarget:self action:@selector(onDateAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _dateBtn;
}

@end

