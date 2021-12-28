package com.netease.meeting.dto;

import java.util.List;

/**
 * @author HJ
 * @date 2021/11/15
 **/
public class MeetingSettingDto {
    /**
     * 非必选
     * 加入会议后静音，默认不静音
     */
    private List<Control> controls;

    /**
     * 非必选
     * 场景配置
     */
    private SceneDto scene;

    public List<Control> getControls() {
        return controls;
    }

    public void setControls(List<Control> controls) {
        this.controls = controls;
    }

    public SceneDto getScene() {
        return scene;
    }

    public void setScene(SceneDto scene) {
        this.scene = scene;
    }

    public static class Control{
        /**
         * 必选
         * 控制类型，audio音频，video视频
         */
        private String type;
        /**
         * 全局控制状态，1：全体关闭控制，0：取消全体关闭控制
         */
        private Integer state;
        /**
         * 入会后自动关闭设置，0：无，1：关闭，2：关闭且不能自行操作，默认不操作
         */
        private Integer attendeeOff;
        /**
         * 允许自行解除关闭控制，true：允许，false：不允许，默认允许
         */
        private Boolean allowSelfOn;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Integer getAttendeeOff() {
            return attendeeOff;
        }

        public void setAttendeeOff(Integer attendeeOff) {
            this.attendeeOff = attendeeOff;
        }

        public Boolean getAllowSelfOn() {
            return allowSelfOn;
        }

        public void setAllowSelfOn(Boolean allowSelfOn) {
            this.allowSelfOn = allowSelfOn;
        }
    }

}
