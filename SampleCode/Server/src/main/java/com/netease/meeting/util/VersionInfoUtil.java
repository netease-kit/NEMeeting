package com.netease.meeting.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author HJ
 * @date 2021/11/17
 **/
public class VersionInfoUtil {
    private static final Log log = LogFactory.getLog(VersionInfoUtil.class);
    private static final String VERSION_INFO_FILE = "version.properties";
    private static final String USER_AGENT_PREFIX = "NEMeeting-sdk-java";

    private static String version = null;

    private static String userAgent = null;

    public static String getVersion() {
        if (version == null) {
            InputStream inputStream = VersionInfoUtil.class.getClassLoader().getResourceAsStream(VERSION_INFO_FILE);
            if (inputStream == null) {
                throw new IllegalArgumentException(VERSION_INFO_FILE + " not found on classpath");
            }
            Properties vp = new Properties();
            try {
                vp.load(inputStream);
                version = vp.getProperty("version");
            } catch (Exception e) {
                log.error("load version information error.", e);
                version = "unknown";
            }
        }
        return version;
    }

    public static String getUserAgent() {
        if (userAgent == null) {
            userAgent = USER_AGENT_PREFIX + "/" + getVersion() + "(" + System.getProperty("os.name") + "/"
                    + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";"
                    + System.getProperty("java.version") + ")";
        }
        return userAgent;
    }
}
