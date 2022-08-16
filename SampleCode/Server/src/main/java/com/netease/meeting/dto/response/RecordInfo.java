package com.netease.meeting.dto.response;

/**
 * @author HJ
 * @date 2021/11/15
 **/
public class RecordInfo {
    /**
     * 用户id
     */
    private String accountId;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 文件的md5值
     */
    private String md5;
    /**
     * 文件大小，单位为字符，可转为Long值
     */
    private Long size;
    /**
     * 文件的类型（扩展名），包括：实时音频录制文件(aac)、白板录制文件(gz)、实时视频录制文件(mp4)、互动直播视频录制文件(flv)
     */
    private String type;
    /**
     * 文件的下载地址
     */
    private String url;
    /**
     * 是否为混合录制文件，1：混合录制文件；2：单人录制文件
     */
    private Integer mix;
    /**
     * 录制文件的切片索引，如果单通通话录制时长超过切片时长，则录制文件会被且被切割成多个文件
     */
    private Integer pieceIndex;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMix() {
        return mix;
    }

    public void setMix(Integer mix) {
        this.mix = mix;
    }

    public Integer getPieceIndex() {
        return pieceIndex;
    }

    public void setPieceIndex(Integer pieceIndex) {
        this.pieceIndex = pieceIndex;
    }
}
