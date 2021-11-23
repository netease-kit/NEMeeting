## NEMeeting Java SDK Tutorial

此项目代码是java服务端sdk源码，封装了创建会议、查询会议等所有暴露给用户的接口，通过NEMeetingClient实例来调用相关接口，一个工程中可以有一个或多个NEMeetingClient，并根据需要修改ClientConfig的默认配置项，该配置项配置了http线程池的大小、超时时间等关键参数，也可以采用默认参数。  

### 引入SDK
maven项目pom.xml中加入相应依赖即可，在&lt;dependencies&gt;中加入如下内容：

```java
<dependency>
    <groupId>com.netease.meeting</groupId>
    <artifactId>netease-sdk-meeting</artifactId>
    <version>最新版本号</version>
</dependency>
```  
最新版本号见【[变更日志](CHANGELOG.md)】

### 初始化NEMeetingClient  
NEMeetingClient支持并发使用，该实例内聚合了一个http线程池，一个项目可以有一个或多个NEMeetingClient实例。
  
- 使用默认ClientConfig配置：

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
 - 自定义ClientConfig配置：  
```java
    // 目前endpoint 为：https://meeting-api.netease.im
    String endpoint = "your endpoint";
    String appKey = "your appKey";
    String appSecret = "your appSecret";
    NEMeetingClient neMeetingClient = null;
    // 自定义线程池及连接配置
    ClientConfig config = new ClientConfig();
    // 最大连接数
    config.setMaxConnections(512);
    config.setConnectionTimeout(3000);
    try {
        // 创建NEMeetingClient实例。
        neMeetingClient = NEMeetingClientBuilder.build(endpoint, appKey, appSecret,config);
        // 创建会议
        MeetingResponse<CreateAccountResponse> response = neMeetingClient.createAccount("imAccid", "imToken" , "shortId" );
    } catch (NETMeetingException ne){
        ne.printStackTrace();
    } finally {
        // 关闭NEMeetingClient。
        neMeetingClient.shutdown();
    }
```  

### ClientConfig配置
  

|参数|描述|
|---|---|   
|connectionRequestTimeout|从连接池中获取连接的超时时间，默认不超时，单位：毫秒| 
|connectionTimeout|建立连接的超时时间,默认为5000毫秒，单位：毫秒| 
|socketTimeout|socket超时时间，默认5000毫秒，单位：毫秒| 
|maxConnections|允许打开的最大HTTP连接数。默认为1024| 
|idleConnectionTime|连接空闲超时时间，超时则关闭连接，默认为60000毫秒，单位：毫秒| 
|maxRetryTime|最大重试次数，默认3次|
