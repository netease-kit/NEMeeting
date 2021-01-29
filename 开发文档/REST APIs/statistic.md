## 会议统计接口
### 会议实时统计
#### 接口描述
查询应用当天的实时数据
#### 接口地址
POST {host}/v1/statistic/meeting/list   HTTP/1.1
Content-Type: application/json;charset=utf-8
#### 请求头
|参数|类型|说明|
| --- | --- | --- |
|AppKey|String|appKey|
|CheckSum|String|签名，校验和|
|CurTime|String|时间戳|
|Nonce|String|随机数|
|Content-Type|String|参数类型|
|accountId|String|账户id|
|AppSecret|String|appSecret|

#### 请求参数

#### 返回值
|参数|类型|说明|
| --- | --- | --- |
|code|int|返回状态码|
|inMeetingCount|String|实时会议数|
|inMeetingMemberCount|String|实时参会人数|
|requestId|String|请求id|
|costTime|String|请求用时|

#### 请求成功示例
```
{
    "code": 200,
    "ret": {
        "inMeetingCount": 0,
        "inMeetingMemberCount": 0
    },
    "requestId": "mc_11eb89665d804fb8a4aa473f5cc9d346",
    "costTime": "32ms"
}
```
#### 请求失败示例
```
{
    "code": 401,
    "msg": "请求未通过验证",
    "requestId": "mc_e4e56f6182de42ef8edc03b023f91cdb",
    "costTime": "25ms"
}
```

