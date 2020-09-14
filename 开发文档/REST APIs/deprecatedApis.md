
### ~~创建会议~~

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

### ~~获取会议加入信息~~

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

### ~~结束会议~~

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

### ~~主持人发起会议控制操作~~

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

### ~~成员发起会议控制操作~~

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

### ~~获取会议状态~~

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
    
### ~~会议中昵称修改~~

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
    