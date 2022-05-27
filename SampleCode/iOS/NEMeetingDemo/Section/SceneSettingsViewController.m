//
//  SceneSettingsViewController.m
//  NEMeetingDevDemo
//
//  Created by 李成达 on 2021/7/2.
//

#import "SceneSettingsViewController.h"

@interface SceneSettingsViewController ()
@property (weak, nonatomic) IBOutlet UITextView *settingsTextView;

@end

@implementation SceneSettingsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if (_sceneJsonString) {
        _settingsTextView.text = _sceneJsonString;
    }
}

- (IBAction)confirm:(id)sender {
    if (self.delegate && [self.delegate respondsToSelector:@selector(didSceneSettingsConfirm:)]) {
        [self.delegate didSceneSettingsConfirm:self.settingsTextView.text];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}


@end
