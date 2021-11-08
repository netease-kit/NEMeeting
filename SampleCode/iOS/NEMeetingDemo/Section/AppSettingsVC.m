//
//  AppSettingsVC.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/8/10.
//

#import <Foundation/Foundation.h>
#import "AppSettingsVC.h"
#import "Config.h"

@implementation AppSettingsVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"应用设置";
    self.form = [self setupForm];
}

- (XLFormDescriptor *)setupForm {
    __weak typeof(self) weakSelf = self;
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
        }
    };
    [appSection addFormRow:serverType];
    
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
    
    return form;
}
    
@end
