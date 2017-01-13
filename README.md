# 云端录制回放(video-playback) Android SDK 版集成文档

## 1. 添加依赖, 在工程需要使用的 Module 中添加如下依赖
```Gradle
compile 'com.baijia.player:video-playback:0.0.2-snapshot'
```

## 2. 请先正确集成好直播 SDK，和点播 SDK

## 3. 创建回放房间
```java
/**
 * 创建录播回放房间 -- 在线回放
 * @param context
 * @param partnerId 合作方 ID
 * @param classId 教室 ID
 * @param deployType
 * @return
 */
PBRoom mRoom = LivePlaybackSDK.newPlayBackRoom(Context context, long partnerId, long classId, LPConstants.LPDeployType deployType);
```
注：

sign 参数暂时没有实际意义， 可以传任意非空字符串

deploy 为运行环境，请参考直播 SDK 文档。

## 4. 进入房间
```java
mRoom = enterRoom(LPLaunchListener listener);
```
LPLauncherListener 为进房间步骤回调。 

## 5. 绑定视频播放器
```java
mRoom.bindPlayerView(playerView);
```

## 6. 如果需要使用到 PPT、白板的回放, 请添加直播 ppt 组件：
```Gradle
compile 'com.baijia.live:liveplayer-sdk-core-ppt:0.0.4'
```

### 6.1 集成文档回放功能
```java
  LPPPTFragment mPPTFragment = new LPPPTFragment();
  mPPTFragment.setLiveRoom(mRoom);
```
将 Fragment 添加到自定义的布局中即可。


## 7. 在线用户组件的回放
```java
OnlineUserVM userVM =  mRoom.getOnlineUserVM();
```
OnlineUserVM 的使用请参考直播 SDK 文档或者 PlayBackDemo_Android 工程中的 UserListFragment.java 文件


## 8. 消息组件的回放
```java
ChatVM chatVm = mRoom.getChatVM();
```
ChatVM 的使用请参考直播 SDK 文档或者 PlayBackDemo_Android 工程中的 MessageListFragment.java 文件

## 9. 退出时关闭房间
```java
@Override
protected void onDestroy() {
  super.onDestroy();
  if (playerView != null) {
    playerView.onDestroy();
  }
  mRoom.quitRoom();
}
```
**在退出 Activity 时需要显示的将 BJPlayerView 和 PBRoom 关闭， 否则会造成内存泄漏**


## CHANGELOG

### version 0.0.3-snapshot 
增加离线包回放功能

apis:
PBRoom 增加三个扩展 API
```java
/**
 * @return  离线音频地址（仅供第三方下载使用，回放 SDK 不处理音频回放）
 */
String getAudioUrl();
```

```java
/**
 * @return 当前播放的视频文件地址
 */
String getVideoUrl();
```

```java
/**
 * @return 离线信令文件包地址
 */
String getPackageSignalFile();
```

在在线播放之后, 可以通过这三个 api 获取资源地址。 并自行下载离线包文件。

### 创建离线回放房间

```java

/**
 * 创建回放房间 -- 离线回放
 * @param context
 * @param partnerId  合作方ID
 * @param classId 教室 ID
 * @param deployType
 * @param videoFile 视频文件
 * @param signalFile 离线信令包文件
 * @return
 */
PBRoom room =  LivePlaybackSDK.newPlayBackRoom(Context context, long partnerId, long classId, LPConstants.LPDeployType deployType, File videoFile, File signalFile); 
```

