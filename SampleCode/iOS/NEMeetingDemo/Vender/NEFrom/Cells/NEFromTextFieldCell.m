//
//  NEFromTextFieldCell.m
//  NEMeetingDemo
//
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//

#import "NEFromTextFieldCell.h"

@interface NEFromTextFieldCell ()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *inputTextView;

@property (nonatomic, assign) NSInteger lengthLimit;
@end

@implementation NEFromTextFieldCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    _inputTextView.frame = CGRectMake(20, 8, self.width - 20*2, self.height-8*2);
}

- (void)doInit {
    [super doInit];
    [self.contentView addSubview:self.inputTextView];
}

- (void)refreshCellWithRow:(NEFromRow *)row {
    [super refreshCellWithRow:row];
    self.inputTextView.text = row.value ?: @"";
    self.inputTextView.placeholder = row.config[@"placeholder"];
    if (row.config[@"keyboardType"]) {
        self.inputTextView.keyboardType = [row.config[@"keyboardType"] integerValue];
    }
    _lengthLimit = [row.config[@"lengthLimit"] integerValue];
}

#pragma mark - Action
- (void)onTextValueChanged:(UITextField *)input {
    self.row.value = input.text;
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

#pragma mark - Getter
- (UITextField *)inputTextView {
    if (!_inputTextView) {
        _inputTextView = [[UITextField alloc] init];
        _inputTextView.font = [UIFont systemFontOfSize:16];
        _inputTextView.clearButtonMode = UITextFieldViewModeWhileEditing;
        _inputTextView.delegate = self;
        [_inputTextView addTarget:self
                           action:@selector(onTextValueChanged:)
                 forControlEvents:UIControlEventEditingChanged];
    }
    return _inputTextView;
}

@end
