//
//  NEFromTitleInputCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromTitleInputCell.h"
#import "UIView+Toast.h"

@interface NEFromTitleInputCell ()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *inputTextView;
@property (nonatomic, strong) UIButton *funButton;
@property (nonatomic, assign) NSInteger lengthLimit;

@end

@implementation NEFromTitleInputCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

- (void)doInit {
    [super doInit];
    [self addSubview:self.titleLabel];
    [self addSubview:self.inputTextView];
    [self addSubview:self.funButton];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.titleLabel.left = 20;
    self.titleLabel.centerY = self.height/2;
    self.titleLabel.width = MIN(80, self.titleLabel.width);
    if (_funButton.hidden) {
        _inputTextView.frame = CGRectMake(self.titleLabel.right + 16,
                                          4,
                                          self.width - (self.titleLabel.right + 16 + 16),
                                          self.height - 4*2);
    } else {
        _funButton.frame = CGRectMake(self.width - 68, 0, 68, self.height);
        _inputTextView.frame = CGRectMake(self.titleLabel.right + 16,
                                          4,
                                          self.width - (self.titleLabel.right + 16) - (_funButton.width + 16),
                                          self.height - 4*2);
    }
}

- (void)refreshCellWithRow:(NEFromRow *)row {
    [super refreshCellWithRow:row];
    self.inputTextView.text = row.value ?: @"";
    self.titleLabel.text = row.title;
    [self.titleLabel sizeToFit];
    
    BOOL disableCopy = [row.config[@"disableCopy"] boolValue];
    if (disableCopy != _funButton.hidden) {
        _funButton.hidden = disableCopy;
        [self setNeedsLayout];
    }
    
    if (row.config[@"textAlignment"]) {
        NSTextAlignment alignment = [row.config[@"textAlignment"] integerValue];
        self.inputTextView.textAlignment = alignment;
    } else {
        self.inputTextView.textAlignment = NSTextAlignmentLeft;
    }
    self.inputTextView.placeholder = row.config[@"placeholder"];
    
    if (row.config[@"clearButtonMode"]) {
        self.inputTextView.clearButtonMode = [row.config[@"clearButtonMode"] integerValue];
    } else {
        self.inputTextView.clearButtonMode = UITextFieldViewModeNever;
    }
    _lengthLimit = [row.config[@"lengthLimit"] integerValue];
}

#pragma mark - Action
- (void)onTextChanged:(UITextField *)sender {
    self.row.value = sender.text;
}

- (void)onCopyAction:(UIButton *)sender {
    if (_inputTextView.text.length != 0) {
        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
        pasteboard.string = _inputTextView.text;
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        [window makeToast:@"复制成功" duration:2 position:CSToastPositionCenter];
    }
}

#pragma mark - Getter
- (UITextField *)inputTextView {
    if (!_inputTextView) {
        _inputTextView = [[UITextField alloc] init];
        _inputTextView.font = [UIFont systemFontOfSize:16.0];
        _inputTextView.textColor = [UIColor lightGrayColor];
        [_inputTextView addTarget:self
                           action:@selector(onTextChanged:)
                 forControlEvents:UIControlEventEditingChanged];
        _inputTextView.delegate = self;
    }
    return _inputTextView;
}

- (UIButton *)funButton {
    if (!_funButton) {
        _funButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [_funButton setTitle:@"复制" forState:UIControlStateNormal];
        _funButton.titleLabel.font = [UIFont systemFontOfSize:14.0];
        [_funButton addTarget:self
                       action:@selector(onCopyAction:)
             forControlEvents:UIControlEventTouchUpInside];
    }
    return _funButton;
}

#pragma mark - <UITextFieldDelegate>
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (_lengthLimit == 0) {
        return YES;
    }
    if (string.length != 0) {
        return textField.text.length < _lengthLimit;
    } else {
        return YES;
    }
}

@end
