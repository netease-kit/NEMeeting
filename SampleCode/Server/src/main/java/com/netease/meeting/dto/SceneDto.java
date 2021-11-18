package com.netease.meeting.dto;

import java.util.List;

/**
 * @author HJ
 * @date 2021/11/16
 **/
public class SceneDto {
    private List<RoleType> roleTypes;

    public List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(List<RoleType> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public static class RoleType {
        private Integer roleType;

        private Integer maxCount;

        public Integer getRoleType() {
            return roleType;
        }

        public void setRoleType(Integer roleType) {
            this.roleType = roleType;
        }

        public Integer getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(Integer maxCount) {
            this.maxCount = maxCount;
        }
    }
}
