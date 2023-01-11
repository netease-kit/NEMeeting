// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>
#import "AppSettingsVC.h"
#import "ServerConfig.h"

@implementation AppSettingsVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"应用设置";
    self.form = [self setupForm];
}

- (XLFormDescriptor *)setupForm {
    XLFormDescriptor *form = [XLFormDescriptor formDescriptorWithTitle:@"应用设置"];
    
    XLFormSectionDescriptor *appSection = [XLFormSectionDescriptor formSection];
    appSection.title = @"服务器配置";
    [form addFormSection:appSection];
    
    
    XLFormRowDescriptor *serverType = [XLFormRowDescriptor
        formRowDescriptorWithTag:@"serverType"
        rowType:XLFormRowDescriptorTypeSelectorActionSheet
        title:@"服务器环境"];
    serverType.height = 60.0;
    NSMutableArray * selectorOptions = [[NSMutableArray alloc] init];
    for(id key in ServerConfig.servers) {
        [selectorOptions addObject: [XLFormOptionsObject formOptionsObjectWithValue:key displayText:key]];
    }
    serverType.selectorOptions = selectorOptions;
    serverType.value = [XLFormOptionsObject formOptionsOptionForValue:ServerConfig.serverType fromOptions:selectorOptions];
    serverType.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        if (newValue && (NSNull *)newValue != [NSNull null]) {
            ServerConfig.serverType = [newValue formValue];
            [NSNotificationCenter.defaultCenter postNotificationName:kNEMeetingResetWindow object:nil];
        }
    };
    [appSection addFormRow:serverType];
    
    XLFormRowDescriptor *languageType =
        [XLFormRowDescriptor formRowDescriptorWithTag:@"languageType"
                                              rowType:XLFormRowDescriptorTypeSelectorActionSheet
                                                title:@"语言环境"];
    serverType.height = 60.0;
    NSDictionary *langSelectorMap =
        @{@"Auto" : @(0), @"中文" : @(1), @"English" : @(2), @"日本语" : @(3)};
    NSMutableArray *langSelectorOptions = @[].mutableCopy;
    for (id key in langSelectorMap) {
      [langSelectorOptions addObject:[XLFormOptionsObject formOptionsObjectWithValue:key
                                                                         displayText:key]];
    }
    NSString *type = [[NSUserDefaults standardUserDefaults] valueForKey:@"languageType"];
    languageType.selectorOptions = langSelectorOptions;
    languageType.value = [XLFormOptionsObject formOptionsOptionForValue:type
                                                            fromOptions:langSelectorOptions];
    languageType.onChangeBlock =
        ^(id _Nullable oldValue, id _Nullable newValue, XLFormRowDescriptor *_Nonnull rowDescriptor) {
          if (newValue && (NSNull *)newValue != [NSNull null]) {
            [[NEMeetingKit getInstance]
                switchLanguage:[[langSelectorMap valueForKey:[newValue formValue]] integerValue]
                      callback:^(NSInteger resultCode, NSString *resultMsg, id result) {
                        if (resultCode != ERROR_CODE_SUCCESS) {
                          NSLog(@"语言切换失败");
                        } else {
                          NSLog(@"语言切换成功");
                          [[NSUserDefaults standardUserDefaults] setObject:[newValue formValue]
                                                                    forKey:@"languageType"];
                        }
                      }];
          }
        };
    [appSection addFormRow:languageType];
    
    XLFormRowDescriptor *customAppKey = [XLFormRowDescriptor
        formRowDescriptorWithTag:@"customAppKey"
        rowType:XLFormRowDescriptorTypeText
        title:@"自定义AppKey"];
    customAppKey.height = 60.0;
    customAppKey.value = ServerConfig.customAppKey;
    customAppKey.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        ServerConfig.customAppKey = newValue;
    };
    [appSection addFormRow:customAppKey];
    
    XLFormRowDescriptor *customAppServerUrl = [XLFormRowDescriptor formRowDescriptorWithTag:@"customAppServerUrl"
        rowType:XLFormRowDescriptorTypeText
        title:@"自定义应用服务器地址"];
    customAppServerUrl.height = 60.0;
    customAppServerUrl.value = ServerConfig.customAppServerUrl;
    customAppServerUrl.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        ServerConfig.customAppServerUrl = newValue;
    };
    [appSection addFormRow:customAppServerUrl];
    
    XLFormRowDescriptor *customSDKServerUrl = [XLFormRowDescriptor formRowDescriptorWithTag:@"customSDKServerUrl"
        rowType:XLFormRowDescriptorTypeText
        title:@"自定义SDK服务器地址"];
    customSDKServerUrl.height = 60.0;
    customSDKServerUrl.value = ServerConfig.customSDKServerUrl;
    customSDKServerUrl.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        ServerConfig.customSDKServerUrl = newValue;
    };
    [appSection addFormRow:customSDKServerUrl];
    
    XLFormRowDescriptor *developerMode = [XLFormRowDescriptor formRowDescriptorWithTag:@"developerMode"
        rowType:XLFormRowDescriptorTypeBooleanSwitch
        title:@"开发者模式"
    ];
    developerMode.height = 60.0;
    developerMode.value =  @([[NSUserDefaults standardUserDefaults] boolForKey:@"developerMode"]);
    developerMode.onChangeBlock = ^(id  _Nullable oldValue, id  _Nullable newValue, XLFormRowDescriptor * _Nonnull rowDescriptor) {
        [[NSUserDefaults standardUserDefaults] setBool:[newValue boolValue] forKey:@"developerMode"];
    };
    [appSection addFormRow:developerMode];
    
    return form;
}
    
@end
