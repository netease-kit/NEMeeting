//
//  SceneSettingsViewController.h
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/7/2.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol SceneSettingsDelegate <NSObject>

- (void)didSceneSettingsConfirm:(NSString *)settings;

@end

@interface SceneSettingsViewController : UIViewController

@property (nonatomic, weak) id<SceneSettingsDelegate> delegate;

@property (nonatomic, copy) NSString *sceneJsonString;

@end

NS_ASSUME_NONNULL_END
