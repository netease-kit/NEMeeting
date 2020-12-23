//
//  SelectedMenuItemEditVC.h
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2020/12/11.
//

#ifndef SelectedMenuItemEditVC_h
#define SelectedMenuItemEditVC_h

@interface SelectedMenuItemEditVC : UIViewController

@property (nonatomic, strong) NEMeetingMenuItem *menuItem;

@property (nonatomic, copy) void (^MenuItemDeleteCallback)(void);

@end

#endif /* SelectedMenuItemEditVC_h */
