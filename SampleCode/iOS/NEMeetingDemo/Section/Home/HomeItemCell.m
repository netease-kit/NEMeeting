// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "HomeItemCell.h"
#import <Masonry/Masonry.h>

@interface HomeItemCell ()
@property(nonatomic, strong) UIImageView *logoImage;
@property(nonatomic, strong) UILabel *nameLabel;
@end

@implementation HomeItemCell
- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    [self setupSubviews];
    [self makeConstraints];
  }
  return self;
}
- (void)configWithItem:(HomeItem *)item {
  self.logoImage.image = [UIImage imageNamed:item.imageName];
  self.nameLabel.text = item.title;
}
- (void)setupSubviews {
  [self.contentView addSubview:self.logoImage];
  [self.contentView addSubview:self.nameLabel];
}
- (void)makeConstraints {
  [self.logoImage mas_makeConstraints:^(MASConstraintMaker *make) {
    make.size.mas_equalTo(CGSizeMake(60, 60));
    make.centerX.mas_equalTo(0);
    make.centerY.equalTo(self.contentView.mas_centerY).offset(-10);
  }];
  [self.nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
    make.left.mas_equalTo(5);
    make.right.mas_equalTo(-5);
    make.height.mas_equalTo(15);
    make.top.equalTo(self.logoImage.mas_bottom).offset(5);
  }];
}
#pragma mark------------------------ Getter ------------------------
- (UIImageView *)logoImage {
  if (!_logoImage) {
    _logoImage = [[UIImageView alloc] initWithFrame:CGRectZero];
  }
  return _logoImage;
}
- (UILabel *)nameLabel {
  if (!_nameLabel) {
    _nameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    _nameLabel.font = [UIFont systemFontOfSize:14];
    _nameLabel.textAlignment = NSTextAlignmentCenter;
  }
  return _nameLabel;
}
@end
