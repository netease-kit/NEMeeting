## NEMeeting Java SDK Tutorial

此项目代码是java服务端sdk源码，封装了创建会议、查询会议等接口，示例代码如下：

```java
// 目前endpoint 为：https://meeting-api.netease.im
    String endpoint = "your endpoint";
    String appKey = "your appKey";
    String appSecret = "your appSecret";
    NEMeetingClient neMeetingClient = null;
    try {
        // 创建NEMeetingClient实例。
        neMeetingClient = NEMeetingClientBuilder.build(endpoint, appKey, appSecret);
        // 创建会议
        MeetingResponse<CreateAccountResponse> response = neMeetingClient.createAccount("imAccid", "imToken" , "shortId" );
    } catch (NETMeetingException ne){
        ne.printStackTrace();
    } finally {
        // 关闭NEMeetingClient。
        neMeetingClient.shutdown();
    }
```