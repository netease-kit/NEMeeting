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

### 按日查询会议统计数据
#### 接口描述
按日期查询企业下的会议统计数据
#### 接口地址
POST {host}/v1/statistic/meeting/getByTimeRange   HTTP/1.1
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
|参数|类型|说明|
| --- | --- | --- |
|startTime|Long|查询开始时间|
|endTime|Long|查询结束时间|

#### 返回值
|参数|类型|说明|
| --- | --- | --- |
|code|int|返回状态码|
|sumMeetingCount|int|总会议数|
|sumMeetingDuration|Long|总会议时长，单位秒|
|sumMeetingMemberCount|int|总参会人数|
|date|Long|日期|
|meetingCount|int|会议数|
|meetingDuration|Long|会议总时长, 单位秒|
|meetingMemberCount|int|会议总参会人次|
|peakNum|int|参会人数并发峰值|

#### 请求成功示例
```
{
    "code": 200,
    "ret": {
        "sumMeetingCount": 64,
        "sumMeetingDuration": 579000,
        "sumMeetingMemberCount": 64,
        "details": [
            {
                "date": 1608048000000,
                "meetingCount": 6,
                "meetingDuration": 279000,
                "meetingMemberCount": 7,
                "peakNum": 3
            },
            {
                "date": 1608134400000,
                "meetingCount": 58,
                "meetingDuration": 300000,
                "meetingMemberCount": 57,
                "peakNum": 3
            }
        ]
    },
    "requestId": "mc_0c5f934d2b3e466d994e15ab176cacfe",
    "costTime": "51ms"
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

### 会议列表数据
#### 接口描述
查询应用下指定时间范围内的所有会议信息
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
|参数|类型|说明|
| --- | --- | --- |
|startTime|Long|查询开始时间|
|endTime|Long|查询结束时间|
|pageIndex|int|页数|
|pageSize|int|每页数据条数|
|status|String|会议状态，/已结束-3/已回收-4/已取消-5， 多个状态以逗号分隔|

#### 返回值
|参数|类型|说明|
| --- | --- | --- |
|code|int|返回状态码|
|pageIndex|int|当前页数|
|pageSize|int|每页显示数据条数|
|totalPageCount|int|总页数|
|totalCount|int|总数据条数|
|meetingId|int|会议号|
|meetingUniqueId|Long|会议唯一id|
|subject|String|会议主题|
|creatorAccountId|String|会议创建人accountId|
|hostAccountId|String|会议主持人accountId|
|status|int|会议状态|
|meetingCreateTime|String|会议创建时间|
|reserveStartTime|String|会议预约开始时间|
|reserveEndTime|String|会议预约结束时间|
|realStartTime|String|会议实际开始时间|
|realEndTime|String|会议实际结束时间|
|duration|Long|会议时长|
|memberCount|int|参会人数|
|peakNum|int|参会人数峰值|

#### 请求成功示例
```
{
    "code": 200,
    "ret": {
        "query": {
            "DEFAULT_PAGE_SIZE": 10,
            "DEFAULT_PAGE_INDEX": 1,
            "DEFAULT_TOTAL_PAGE_COUNT": 1,
            "DEFAULT_TOTAL_COUNT": 0,
            "DEFAULT_OFFSET": 0,
            "pageSize": 10,
            "pageIndex": 1,
            "totalPageCount": 1,
            "totalCount": 6,
            "offset": 0,
            "limit": 10
        },
        "list": [
            {
                "appId": 1,
                "meetingId": 216406206,
                "meetingUniqueId": 21908,
                "id": 124159616442496,
                "subject": "test",
                "creatorAccountId": "1160033511272363",
                "hostAccountId": "1160015538215470",
                "status": 1,
                "meetingCreateTime": "2020-12-16 20:06:46",
                "reserveStartTime": "2020-12-16 20:30:00",
                "reserveEndTime": "2020-12-16 21:00:00",
                "realStartTime": "2020-12-16 20:08:13",
                "realEndTime": "2020-12-16 20:11:06",
                "duration": 173,
                "memberCount": 2,
                "peakNum": 0
            },
            {
                "appId": 1,
                "meetingId": 215806491,
                "meetingUniqueId": 21907,
                "id": 124157159698560,
                "subject": "test",
                "creatorAccountId": "1160033511272363",
                "hostAccountId": "1160033511272363",
                "status": 5,
                "meetingCreateTime": "2020-12-16 19:56:46",
                "reserveStartTime": "",
                "reserveEndTime": "",
                "realStartTime": "2020-12-16 20:00:54",
                "realEndTime": "2020-12-16 20:02:09",
                "duration": 75,
                "memberCount": 1,
                "peakNum": 0
            },
            {
                "appId": 1,
                "meetingId": 213634111,
                "meetingUniqueId": 21906,
                "id": 124148262166656,
                "subject": "Ggggg的即刻会议",
                "creatorAccountId": "126710591707418624",
                "status": 5,
                "meetingCreateTime": "2020-12-16 19:20:34",
                "reserveStartTime": "",
                "reserveEndTime": "",
                "realStartTime": "2020-12-16 19:20:34",
                "realEndTime": "2020-12-16 19:20:40",
                "duration": 6,
                "memberCount": 1,
                "peakNum": 0
            },
            {
                "appId": 1,
                "meetingId": 211761579,
                "meetingUniqueId": 21904,
                "id": 27406,
                "subject": "吴磊小号qa8的即刻会议",
                "creatorAccountId": "127112432161132544",
                "status": 5,
                "meetingCreateTime": "2020-12-16 18:49:21",
                "reserveStartTime": "",
                "reserveEndTime": "",
                "realStartTime": "2020-12-16 18:49:21",
                "realEndTime": "2020-12-16 18:49:48",
                "duration": 27,
                "memberCount": 2,
                "peakNum": 0
            },
            {
                "appId": 1,
                "meetingId": 209983928,
                "meetingUniqueId": 21903,
                "id": 27405,
                "subject": "吴磊小号qa8的即刻会议",
                "creatorAccountId": "127112432161132544",
                "status": 5,
                "meetingCreateTime": "2020-12-16 18:19:44",
                "reserveStartTime": "",
                "reserveEndTime": "",
                "realStartTime": "2020-12-16 18:19:44",
                "realEndTime": "2020-12-16 18:46:17",
                "duration": 1593,
                "memberCount": 3,
                "peakNum": 0
            },
            {
                "appId": 1,
                "meetingId": 209970541,
                "meetingUniqueId": 21902,
                "id": 27404,
                "subject": "吴磊小号qa8的即刻会议",
                "creatorAccountId": "127112432161132544",
                "status": 5,
                "meetingCreateTime": "2020-12-16 18:19:30",
                "reserveStartTime": "",
                "reserveEndTime": "",
                "realStartTime": "2020-12-16 18:19:30",
                "realEndTime": "2020-12-16 18:19:36",
                "duration": 6,
                "memberCount": 1,
                "peakNum": 0
            }
        ]
    },
    "requestId": "mc_72f0704fa3bf45d6910523a04880df49",
    "costTime": "80ms"
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

### 查询当场会议详细数据
#### 接口描述
查询单场会议的详细数据
#### 接口地址
POST {host}/v1/statistic/meeting/detail   HTTP/1.1
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
|参数|类型|说明|
| --- | --- | --- |
|meetingUniqueId|Long|会议唯一id|


#### 返回值
|参数|类型|说明|
| --- | --- | --- |
|code|int|返回状态码|
|meetingId|int|会议号|
|meetingUniqueId|Long|会议唯一id|
|subject|String|会议主题|
|creatorAccountId|String|会议创建人accountId|
|hostAccountId|String|会议主持人accountId|
|status|int|会议状态|
|meetingCreateTime|String|会议创建时间|
|reserveStartTime|String|会议预约开始时间|
|reserveEndTime|String|会议预约结束时间|
|realStartTime|String|会议实际开始时间|
|realEndTime|String|会议实际结束时间|
|duration|Long|会议时长|
|memberCount|int|参会人数|
|peakNum|int|参会人数峰值|
|accountId|int|参会人accountId|
|nickname|int|参会人昵称|
|joinTime|int|入会时间|
|exitTime|int|离开时间|
|duration|int|参会时长|
|clientType|int|参会终端类型|

#### 请求成功示例
```
{
    "code": 200,
    "ret": [
        {
            "appId": 1,
            "meetingId": 216406206,
            "meetingUniqueId": 21908,
            "id": 124159616442496,
            "subject": "test",
            "status": 1,
            "meetingCreateTime": "2020-12-16 20:06:46",
            "reserveStartTime": "2020-12-16 20:30:00",
            "reserveEndTime": "2020-12-16 21:00:00",
            "realStartTime": "2020-12-16 20:08:13",
            "realEndTime": "2020-12-16 20:11:06",
            "duration": 173,
            "memberCount": 2,
            "peakNum": 0,
            "members": [
                {
                    "id": 124006,
                    "accountId": "1160015538215470",
                    "nickname": "zhouhost",
                    "joinTime": "2020-12-16 20:06:46",
                    "exitTime": "2020-12-16 20:11:06",
                    "duration": 260,
                    "clientType": 3
                },
                {
                    "id": 124005,
                    "accountId": "1160033511272363",
                    "nickname": "zhouhost",
                    "joinTime": "2020-12-16 20:08:13",
                    "exitTime": "2020-12-16 20:09:28",
                    "duration": 75,
                    "clientType": 3
                },
                {
                    "id": 124007,
                    "accountId": "1160033511272363",
                    "nickname": "zhouhost",
                    "joinTime": "2020-12-16 20:06:46",
                    "exitTime": "2020-12-16 20:11:06",
                    "duration": 260,
                    "clientType": 3
                }
            ]
        }
    ],
    "requestId": "mc_06ddd2aadd32473a90750ea158042bb0",
    "costTime": "43ms"
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


