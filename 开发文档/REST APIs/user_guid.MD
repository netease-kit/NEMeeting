## 概述

网易会议 Rest API 是提供给开发者接入网易会议开放平台的入口。
开发者可以通过网易会议 API 进行二次开发，例如创建一个会议，获取会议信息，加入会议，结束会议，会议控制等。

## 变更记录

| 日期 | 版本 | 变更内容 |
| :------: | :------: | :------: | 
| 2020-07-14  | 0.5.0 | 初稿 |

## 准备工作

接入网易会议 Saas 服务前需要联系客服获取 AppKey

## API 网关

网关地址：https://meeting-api.netease.im


## 签名验证

Rest API对每个访问请求进行身份验证，验证失败的请求无法调用API接口。

签名用的请求头参数

| 参数 | 类型 | 必选 | 描述 |
| :------: | :------: | :------: | :------: |
| AppKey  | String | 是 | 平台分配的应用appkey |
| Nonce | String | 是 | 随机8位Integer正整数（最大长度128个字符）,例如12345678 |
| CurTime | String| 是 | 当前 UNIX 时间戳，可记录发起 API 请求的时间。例如1594639036，单位为秒。注意：如果与服务器时间相差超过1分钟，会引起签名过期错误。 |
| CheckSum | String | 是 | SHA1(AppSecret + Nonce + CurTime)，三个参数拼接的字符串，进行SHA1哈希计算，转化成16进制字符(String，小写)，注意AppSecret为AppKey对应的秘钥 [签名代码](#sign_code) |

## 公共响应参数
| 参数 | 类型 | 描述 |
| :------: | :------: | :------: |
| code  | Integer | 状态码，200：表示请求处理成功 |
| msg | String | 错误信息 |
| requestId | String | 请求流水号 |
| costTime | String | 请求响应时间，单位：毫秒 |
| ret | Object | 响应数据，业务参数都是放在这个对象内的 |

## Rest APIs

### 会议账号创建

1. 接口描述  
    注册创建一个会议账号
    
2. 接口请求地址
    ```
    POST https://{host}/v1/account/create HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | accountId  | String | 否 | 会议账号，不能以 "a" 开头，不能以 "3" 开头，不能超过32位，只能包含"-"、"_"、小写字母和数字，如果不传则系统自动生成 |

4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | accountId | String | 会议用户账号ID |
    | accountToken | String | 会议用户账号令牌 |
    | imAccid | String | IM通信ID |
    | imToken | String | IM通信ID密码 |
    | personalMeetingId | Long | 个人会议的会议码 |
    
### 会议账号更新令牌

1. 接口描述  
    会议账号更新令牌
    
2. 接口请求地址
    ```
    POST https://{host}/v1/account/updateToken HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | accountId  | String | 是 | 会议用户账号 |
    | accountToken  | String | 是 | 会议用户账号令牌 |

4. 输出参数
    公共响应

### 创建会议

1. 接口描述  
    请求创建会议。
    * 创建会议者为主持人
    * 此处返回成功后，会有一条参会记录，但是状态为【1：未参会】，等接收到加入抄送则将状态变更为【2：参会中】
    * 创建会议时会检查appKey下所有会议参会数量的总和的并发数量
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/create HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 否 | 个人会议码，10位数字；若为空，则随机分配会议id，9位数字 |
    | accountId | String | 是 | 应用内唯一的用户账号id |
    | deviceId | String | 是 | 客户端设备编号 |
    | clientType | Integer | 是 | 客户端类型。1：TV，2：IOS，3：AOS，4：PC，5：MAC，6：WEB |
    | nickName | String| 是 | 用户加入会议时的昵称，10位以内的汉字、字母、数字 |
    | video | Integer | 是 | 画面状态，1：打开，2：关闭 |
    | audio | Integer | 是 | 声音状态，1：打开，2：关闭 |

4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | meetingId | String | 个人会议码，10位数字；若为空，则随机分配会议id，9位数字 |
    | meetingKey | String | 会议唯一key |
    | avRoomCName | String | 公有云音视频房间名称 |
    | avRoomCid | String | 公有云音视频房间id |
    | avRoomUid | String | 公有云音视频房间成员uid |
    | avRoomCheckSum | String | 音视频服务器请求token |
    | createTime | Long | 会议创建时间，UNIX时间戳（单位：秒） |

### 获取会议加入信息

1. 接口描述  
    获取会议加入信息。
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/joinInfo HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | accountId | String | 是 | 应用内唯一的用户账号id |
    | deviceId | String | 是 | 客户端设备编号 |
    | clientType | Integer | 是 | 客户端类型。1：TV，2：IOS，3：AOS，4：PC，5：MAC，6：WEB |
    | nickName | String | 是 | 用户加入会议时的昵称，10位以内的汉字、字母、数字 |
    | video | Integer | 是 | 画面状态，1：打开，2：关闭 |
    | audio | Integer | 是 | 声音状态，1：打开，2：关闭 |
    
4. 输出参数
    
    `以下是公共响应参数的ret属性内包含的参数`

    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | meetingKey | String | 会议唯一key |
    | avRoomCName | String | 公有云音视频房间名称 |
    | avRoomCid | String | 公有云音视频房间id |
    | avRoomUid | String | 公有云音视频房间成员uid |
    | avRoomCheckSum | String | 音视频服务器请求token |
    | createTime | Long | 会议创建时间，UNIX时间戳（单位：秒） |
    | duration | Long | 会议持续时长（单位：秒） |
    | audioAllMute | Integer | 是否全体静音，0：否，1：是 |

### 结束会议

1. 接口描述  
    结束会议。
    * 只有主持人有权操作。
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/delete HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | hostDeviceId | String | 是 | 主持人的客户端设备编号 |
    
4. 输出参数
    公共响应

### 主持人发起会议控制操作

1. 接口描述  
    主持人发起会议控制操作。
    * 只有主持人有权操作
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/control/host HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | hostDeviceId | String | 是 | 主持人客户端设备编号 |
    | members | List<String> | 否 | 操作成员设备编号列表 |
    | action | Integer | 是 |  0：剔除，10：主持人禁止成员画面，11：主持人禁止成员声音，12：主持人全体禁音，13：主持人锁定会议，15：主持人解禁成员画面，16：主持人解禁成员声音，17：主持人全体解禁，18：主持人解锁会议，22：移交主持人，30：主持人指定焦点视频，31：主持人取消焦点视频 |
    
4. 输出参数
    公共响应

### 成员发起会议控制操作

1. 接口描述  
    成员自己变更音视频状态，向会议成员广播消息。
    * 只有主持人有权操作
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/control/host HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | deviceId | String | 是 | 客户端设备编号 |
    | action | Integer | 是 |  50：成员关闭自身画面，51：成员关闭自身声音，52：成员结束屏幕共享，55：成员打开自身画面，56：成员打开自身声音，57：成员开始屏幕共享 |
    
4. 输出参数
    公共响应

### 获取会议状态

1. 接口描述  
    获取会议中当前成员信息。
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/info HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | deviceId | String | 是 | 客户端设备编号 |
    | avRoomUid | Long | 否 | 音视频房间成员uid，如果不传则返回会议所有成员的状态信息，否则返回对应uid的成员状态信息 |
    
4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | meeting | Object | 会议信息 |
    | members | List<Object> | 成员信息，按照进入会议的先后顺序排序 |
    
    `meeting 对象内部参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | audioAllMute | Integer | 是否全体静音，0：否，1：是 |
    | hostAccountId | String | 主持人，应用内唯一的用户账号id |
    | focusAccountId | String | 会议焦点成员，应用内唯一的用户账号id |
    | joinControlType | Integer | 会议加入控制类型，1：允许任何人直接加入，2：不允许 |
    | screenSharesAccountId | List<String> | 会议中屏幕共享者的账号id列表 |
    
    `members 数组内对象的内部参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | accountId | String | 会议成员，应用内唯一的用户账号id |
    | nickName | String | 会议成员昵称 |
    | avRoomUid | Long | 会议成员的音视频房间uid |
    | audio | Integer |  声音状态，1：有，2：无（自己关闭），3：无（主持人禁），4：无（主持人打开，等待成员确认） |
    | video | Integer |  声音状态，1：有，2：无（自己关闭），3：无（主持人禁），4：无（主持人打开，等待成员确认） |
    | screenSharing | Integer | 屏幕共享状态，0：非共享中，1：共享中 |
    | status | Integer | 1：占位中，2：参会中 |
    
### 会议中昵称修改

1. 接口描述  
    会议中昵称修改。
    * 只修改会议中的显示昵称，不修改与账号绑定的昵称
    
2. 接口请求地址
    ```
    POST https://{host}/v1/meeting/member/modify HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingId  | String | 是 | 会议id |
    | deviceId | String | 是 | 客户端设备编号 |
    | nickName | String | 是 | 修改后的昵称 |
    
4. 输出参数
    公共响应
    
## 错误码
| 错误码 | 说明 |
| :------: | :------: |
| 300 | 非法参数 |
| 301 | 参数为空 |
| 302 | 非法请求头 |
| 401 | 请求未通过验签 |
| 1019 | 会议账号已被创建 |
| 2000 | 该会议不存在 |
| 2001 | 会议已达人数上限 |
| 2002 | 该会议已在进行中 |
| 2003 | 昵称格式不正确 |
| 2004 | 会议成员视频状态值非法 |
| 2005 | 会议成员音频状态值非法 |
| 2007 | 该用户未拥有会议码 |
| 2009 | 已达屏幕共享人数上限 |
| 2010 | 会议被锁定 |
| 2011 | 会议id已被占用 |
| 2100 | 没有主持人权限 |
| 2101 | 该成员未在会议中 |
| 2102 | 会控码错误 |
| 2103 | 已被主持人关闭画面，无法自行打开 |
| 2104 | 该成员连接不稳定，请稍后再试 |
| 5000 | 该租户不存在 |
| 5001 | 租户类型错误 |
| 6000 | 应用组已存在 |

## 签名代码Demo

<span id="sign_code" />

```java

package com.netease.vcloud.meeting.utils;

import org.apache.commons.lang3.StringUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSumBuilder {
    
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getCheckSum(String nonce, String curTime, String appSecret) {
        String plaintext = appSecret + nonce + curTime;
        return encode(plaintext, "SHA1");
    }

    private static String encode(String plaintext, String method) {
        if (StringUtils.isBlank(plaintext)) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(method);
            messageDigest.update(plaintext.getBytes(StandardCharsets.UTF_8));
            return getFormattedText(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
        }
        return "";
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }
}

```