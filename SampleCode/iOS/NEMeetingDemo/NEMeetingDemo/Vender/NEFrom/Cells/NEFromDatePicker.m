//
//  NEFromDatePicker.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromDatePicker.h"

@interface NEFromDatePickerBar ()

@property (nonatomic, strong) NEFromDatePicker *datePicker;
@property (nonatomic, assign) BOOL isShown;
@property (nonatomic, strong) NDFromDateDidSelected selectedBlock;
@property (nonatomic, strong) NSDate *date;
@property (nonatomic, strong) NSDate *minDate;
@property (nonatomic, strong) NSDate *maxDate;

@end

@interface NEFromDatePicker()

@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UIButton *sureBtn;
@property (nonatomic, strong) UIButton *cancelBtn;
@property (nonatomic, strong) UIView *seqLine;
@property (nonatomic, strong) UIDatePicker *picker;
@property (nonatomic, strong) dispatch_block_t cancelBlock;
@property (nonatomic, strong) NDFromDateDidSelected sureBlock;

@end
 
@implementation NEFromDatePicker

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self addSubview:self.titleLab];
        [self addSubview:self.sureBtn];
        [self addSubview:self.cancelBtn];
        [self addSubview:self.seqLine];
        [self addSubview:self.picker];
        self.backgroundColor = [UIColor whiteColor];
        self.layer.cornerRadius = 16;
        self.clipsToBounds = YES;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    _cancelBtn.frame = CGRectMake(0, 0, 60, 44);
    _sureBtn.frame = CGRectMake(self.width - 60, 0, 60, 44);
    _titleLab.centerX = self.width/2;
    _titleLab.top = 10;
    _seqLine.frame = CGRectMake(0, _cancelBtn.bottom, self.width, 1.0);
    _picker.frame = CGRectMake(0, self.height - 240 - 16, self.width, 240);
}

#pragma mark - Action
- (void)onSureAction:(UIButton *)sender {
    if (_sureBlock) {
        _sureBlock(_picker.date);
    }
}

- (void)onCancelAction:(UIButton *)sender {
    if (_cancelBlock) {
        _cancelBlock();
    }
}

#pragma mark - Getter
- (UILabel *)titleLab {
    if (!_titleLab) {
        _titleLab = [[UILabel alloc] init];
        _titleLab.font = [UIFont systemFontOfSize:17];
        _titleLab.textAlignment = NSTextAlignmentCenter;
        _titleLab.text = @"选择日期";
        [_titleLab sizeToFit];
    }
    return _titleLab;
}

- (UIButton *)sureBtn {
    if (!_sureBtn) {
        _sureBtn = [UIButton buttonWithType:UIButtonTypeSystem];
        [_sureBtn setTitle:@"完成" forState:UIControlStateNormal];
        _sureBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_sureBtn addTarget:self
                     action:@selector(onSureAction:)
           forControlEvents:UIControlEventTouchUpInside];
    }
    return _sureBtn;
}

- (UIButton *)cancelBtn {
    if (!_cancelBtn) {
        _cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_cancelBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [_cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
        _cancelBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_cancelBtn addTarget:self
                     action:@selector(onCancelAction:)
           forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelBtn;
}

- (UIView *)seqLine {
    if (!_seqLine) {
        _seqLine = [[UIView alloc] init];
        _seqLine.backgroundColor = [UIColor groupTableViewBackgroundColor];
    }
    return _seqLine;
}

- (UIDatePicker *)picker {
    if (!_picker) {
        _picker = [[UIDatePicker alloc] init];
        _picker.calendar = [NSCalendar currentCalendar];
        _picker.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"zh_GB"];
        [_picker setDate:[NSDate date] animated:YES];
        _picker.timeZone = [NSTimeZone systemTimeZone];;
        _picker.minuteInterval = 30;
        if (@available(iOS 13.4, *)) {
            _picker.preferredDatePickerStyle = UIDatePickerStyleWheels;
        }
    }
    return _picker;
}

@end

@implementation NEFromDatePickerBar

+ (void)showWithDate:(NSDate *)date
             minDate:(NSDate *)minDate
             maxDate:(NSDate *)maxDate
            selected:(NDFromDateDidSelected)selectedBlock {
    NEFromDatePickerBar *bar = [[NEFromDatePickerBar alloc] initWithFrame:[UIScreen mainScreen].bounds];
    bar.date = date;
    bar.minDate = minDate;
    bar.maxDate = maxDate;
    bar.selectedBlock = selectedBlock;
    bar.tag = 10000;
    [bar showPicker];
    
}

+ (void)dismiss {
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    NEFromDatePickerBar *bar = [keyWindow viewWithTag:10000];
    [bar dismiss];
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor colorWithWhite:0 alpha:0.3];
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTouchMask:)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void)showPicker {
    if (_isShown) {
        return;
    }
    _isShown = YES;
    
    __weak typeof(self) weakSelf = self;
    _datePicker = [[NEFromDatePicker alloc] initWithFrame:CGRectMake(0, self.height, self.width, 300)];
    _datePicker.cancelBlock = ^{
        [weakSelf dismiss];
    };
    _datePicker.picker.date = _date;
    _datePicker.picker.minimumDate = _minDate;
    _datePicker.picker.maximumDate = _maxDate;
    _datePicker.sureBlock = ^(NSDate * _Nonnull date) {
        [weakSelf dismiss];
        weakSelf.date = date;
        if (weakSelf.selectedBlock) {
            weakSelf.selectedBlock(date);
        }
    };
    
    [self addSubview:_datePicker];
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    self.frame = keyWindow.bounds;
    [keyWindow addSubview:self];
    [UIView animateWithDuration:0.3 animations:^{
        self.datePicker.top -= 284;
    }];
}

- (void)dismiss {
    if (!_isShown) {
        return;
    }
    _isShown = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.datePicker.top = self.height;
    } completion:^(BOOL finished) {
        [self.datePicker removeFromSuperview];
        self.datePicker = nil;
        [self removeFromSuperview];
    }];
}

#pragma mark - Action
- (void)onTouchMask:(id)sender {
    [self dismiss];
}
@end
