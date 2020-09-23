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
    | accountId  | String | 否 | 会议账号|
    | imAccid  | String | 否 | 复用的imAccid |
    | imToken  | String | 否 | 复用的imAccid的Token |
    
#####备注
不能以 "a" 开头，不能以 "3" 开头，不能超过32位，只能包含"-"、"_"、小写字母和数字，如果不传则系统自动生成

4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | accountId | String | 会议用户账号ID |
    | accountToken | String | 会议用户账号令牌 |
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
    
2. 接口请求地址
    ```
    POST https://{host}/v2/meeting/create HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | accountId | String | 是 | 应用内唯一的用户账号id |
    | type | String | 是 | 会议类型 1 随机号即时会议; 2 个人号即时会议; 3 随机号预约会议 |
    | password | String| 否 | 会议密码，无密码为空串|
    | settings | JsonObject| 否 | 会议设置|
    | settings.attendeeAudioOff | Boolean| 否 | 加入会议后静音，默认不静音|
    | subject | String | 是 | 会议主题 30字符以内|
    | startTime | Long| type=3: 是; type=1: 否| 预约开始时间，毫秒|
    | endTime | Long| type=3: 是; type=1: 否| 预约结束时间，毫秒|

4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | meetingUniqueId | Long | 会议唯一id |
    | meetingId   | String | 随机会议码,9位数字；个人会议码，10位数字   |
    | subject | String | 预约会议主题 |
    | startTime | Long | 预约开始时间，毫秒 |
    | endTime | Long | 预约结束时间，毫秒，-1无限期 |
    | password | String | 会议密码，无密码为空串 |
    | settings | JsonObject | 会议设置 |
    | settings.attendeeAudioOff | Boolean | 加入会议后静音，默认不静音 |
    | status | int| 状态，0.无效，1.未开始，2.进行中，3.已终止，4.已取消，5.已回收 |

### 查询会议（meetingUniqueId）

1. 接口描述  
    请求查询会议。
    
2. 接口请求地址
    ```
    POST https://{host}/v2/meeting/get HTTP/1.1
    Content-Type: application/json;charset=utf-8
    ```
3. 输入参数

    | 参数 | 类型 | 必选 | 描述 |
    | :------: | :------: | :------: | :------: |
    | meetingUniqueId | String | 是 | 会议唯一id（meetingUniqueId） |

4. 输出参数

    `以下是公共响应参数的ret属性内包含的参数`
    
    | 参数 | 类型 | 描述 |
    | :------: | :------: | :------: |
    | meetingUniqueId | Long | 会议唯一id |
    | meetingId   | String | 随机会议码,9位数字；个人会议码，10位数字   |
    | subject | String | 预约会议主题 |
    | startTime | Long | 预约开始时间，毫秒 |
    | endTime | Long | 预约结束时间，毫秒，-1无限期 |
    | password | String | 会议密码，无密码为空串 |
    | settings | JsonObject | 会议设置 |
    | settings.attendeeAudioOff | Boolean | 加入会议后静音，默认不静音 |
    | status | int| 状态，0.无效，1.未开始，2.进行中，3.已终止，4.已取消，5.已回收 |
    
### 修改会议

1. 接口描述  

修改预约会议。

2. 接口请求地址

```
POST https:/${domain}/v2/meeting/edit HTTP/1.1
Content-Type: application/json;charset=utf-8
```

3. 输入参数

|请求体参数|类型|说明|必须|
|:--- | :----- | :-------| :--- |
| meetingUniqueId | Long | 会议唯一Id | 是 |
| subject | String | 会议主题 30字符以内 | 是 |
| startTime | Long | 预约开始时间，毫秒 | 否 |
| endTime | Long | 预约结束时间，毫秒，-1无限期 | 否 |
| password | String | 会议密码，密码置空传空串 | 否 |
| settings | JsonObject | 会议设置，设置有修改要传json里全部参数 | 否 |
| attendeeAudioOff | Boolean | 加入会议后静音，默认不静音 | 否 |

4. 输出参数
无
 
```    
//成功结果示例
"Content-Type": "application/json; charset=utf-8"
{
  "code": 200
}

//失败结果示例
{
    "code":501,
    "msg":"服务器内部错误"
}
```

### 取消会议

1. 接口描述  

取消预约会议。

2. 接口请求地址

```
POST https:/${domain}/v2/meeting/cancel HTTP/1.1
Content-Type: application/json;charset=utf-8
```

4. 输出参数

|请求体参数|类型|说明|必须|
|:--- | :----- | :-------| :--- |
| meetingUniqueId | Long | 会议唯一Id | 是 |

3. 输入参数
```json
{
  "meetingUniqueId": 110119120
}
```

4. 输出参数
无

```    
//成功结果示例
"Content-Type": "application/json; charset=utf-8"
{
  "code": 200
}

//失败结果示例
{
    "code":501,
    "msg":"服务器内部错误"
}
```
    


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
| 2023 | 非法会议类型|
| 2010 | 会议被锁定 |
| 2011 | 会议id已被占用 |
| 2017 | 会议主题格式错误 |
| 2021 | 会议已结束|
| 2025 | 会议信息不能被修改 |
| 2100 | 没有主持人权限 |
| 2101 | 该成员未在会议中 |
| 2102 | 会控码错误 |
| 2103 | 已被主持人关闭画面，无法自行打开 |
| 2104 | 该成员连接不稳定，请稍后再试 |
| 5000 | 该租户不存在 |
| 5001 | 租户类型错误 |
| 6000 | 应用组已存在 |
| 3003 | 功能不支持  | 
| 2002 | 该会议已在进行中 | 

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