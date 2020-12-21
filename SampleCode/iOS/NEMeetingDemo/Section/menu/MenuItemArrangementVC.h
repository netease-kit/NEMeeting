//
//  MenuItemArragementVC.h
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2020/12/10.
//

#ifndef MenuItemArragementVC_h
#define MenuItemArragementVC_h

#import "BaseViewController.h"

@interface MenuItemArrangementVC : BaseViewController

@property (nonatomic, strong) NSMutableArray <NEMeetingMenuItem *> *menuItems;

@property (nonatomic, copy) void (^MenuItemSelectCallback)(NSMutableArray <NEMeetingMenuItem *> *menuItems);

@end


#endif /* MenuItemArragementVC_h */
