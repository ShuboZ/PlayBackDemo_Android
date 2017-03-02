# 云端录制回放(video-playback) Android SDK 版集成文档

## 1. 添加依赖, 在工程需要使用的 Module 中添加如下依赖
```Gradle
compile 'com.baijia.player:video-playback:0.1.0'
```

## 2. 请先正确集成好[直播 SDK](https://github.com/baijia/LivePlayerDemo_Android)，和[点播 SDK](https://github.com/baijia/maven/tree/master/com/baijia/player/videoplayer)

## 3. 创建回放房间
```java
/**
 * 创建录播回放房间 -- 在线回放
 * @param context
 * @param classId 教室 ID
 * @param token token由第三方后端返回
 * @param deployType
 * @return
 */
PBRoom mRoom = LivePlaybackSDK.newPlayBackRoom(Context context, long classId, String token, LPConstants.LPDeployType deployType);
```
注：

token 获取请参考百家云文档

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
compile 'com.baijia.live:liveplayer-sdk-core-ppt:0.0.9'
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

## 0.1.0
修复离线进教室需要联网才能播放的问题
修改了`newPlayBackRoom()`的传入参数
mRoom.getChatVM().notifyDataChange()现更名为mRoom.getChatVM().getObservableOfNotifyDataChange()

## 0.0.3-snapshot 
### 增加离线包回放功能

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
 * @param classId 教室 ID
 * @param deployType
 * @param videoFile 视频文件
 * @param signalFile 离线信令包文件
 * @return
 */
PBRoom room =  LivePlaybackSDK.newPlayBackRoom(Context context, long classId, LPConstants.LPDeployType deployType, File videoFile, File signalFile);
```

