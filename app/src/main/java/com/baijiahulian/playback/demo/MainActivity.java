package com.baijiahulian.playback.demo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.PBRoom;
import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.BJDownloadCallback;
import com.baijiahulian.common.networkv2.BJNetRequestManager;
import com.baijiahulian.common.networkv2.BJNetworkClient;
import com.baijiahulian.common.networkv2.BJResponse;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.BJBottomViewPresenter;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;
import com.baijiahulian.player.playerview.BJTopViewPresenter;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements LPLaunchListener {

    private PBRoom mRoom;

    private TextView mTextView;
    private LPPPTFragment mPPTFragment;
    private BJPlayerView playerView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private UserListFragment mUserListFragment;
    private MessageListFragment mMessageListFragment;


    BJNetworkClient client = new BJNetworkClient.Builder()
            .setEnableLog(true)
            .build();
    final BJNetRequestManager netRequestManager = new BJNetRequestManager(client);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text1);
        playerView = (BJPlayerView) findViewById(R.id.playerView);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        playerView.setBottomPresenter(new BJBottomViewPresenter(playerView.getBottomView()));
        playerView.setTopPresenter(new BJTopViewPresenter(playerView.getTopView()));
        playerView.setCenterPresenter(new BJCenterViewPresenter(playerView.getCenterView()));

        playerView.getTopViewPresenter().setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /**
         *   设置在线播放视频格式
         *   1:mp4
         *   2:m3u8
         *   3:flv
         */
        playerView.setVideoFormat(3);

        mTabLayout.addTab(mTabLayout.newTab().setText("PPT"));
        mTabLayout.addTab(mTabLayout.newTab().setText("用户名单"));
        mTabLayout.addTab(mTabLayout.newTab().setText("聊天"));
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        long classId = getIntent().getLongExtra("classId", 0);

        File dir = new File(Environment.getExternalStorageDirectory(), classId + "/");
        dir.mkdirs();

        final File videoFile = new File(Environment.getExternalStorageDirectory(), classId + "/video.mp4");
        final File signalFile = new File(Environment.getExternalStorageDirectory(), classId + "/signal.file");

        if (!getIntent().getBooleanExtra("offline", false)) {
            mRoom = LivePlaybackSDK.newPlayBackRoom(this, classId, "test12345678", LPConstants.LPDeployType.Test);
//            mRoom = LivePlaybackSDK.newPlayBackRoom(this, 17032875178571L, "g6vKZC7Su_gBAl9P_c-K28eb5t-21VTALles2MhLUrQxuSba8SBafg", LPConstants.LPDeployType.Product);

        } else {
            mRoom = LivePlaybackSDK.newPlayBackRoom(MainActivity.this, classId, LPConstants.LPDeployType.Test, videoFile, signalFile);
        }

        mRoom.enterRoom(MainActivity.this);
        mPPTFragment = new LPPPTFragment();
        mPPTFragment.setLiveRoom(mRoom);

        mUserListFragment = new UserListFragment();
        mUserListFragment.setRoom(mRoom);

        mMessageListFragment = new MessageListFragment();
        mMessageListFragment.setRoom(mRoom);

//        playerView.initPartner(partnerID, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        mRoom.bindPlayerView(playerView);

        findViewById(R.id.downloadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRoom.getVideoUrl() == null || mRoom.getPackageSignalFile() == null) {
                    Toast.makeText(getBaseContext(), "下不了", Toast.LENGTH_SHORT).show();
                    return;
                }

                netRequestManager.newDownloadCall(mRoom.getVideoUrl(), videoFile)
                        .executeAsync(MainActivity.this, new BJDownloadCallback() {
                            @Override
                            public void onDownloadFinish(BJResponse bjResponse, File file) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "视频文件下完了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(long l, long l1) {

                            }

                            @Override
                            public void onFailure(HttpException e) {

                            }
                        });

                netRequestManager.newDownloadCall(mRoom.getPackageSignalFile(), signalFile)
                        .executeAsync(MainActivity.this, new BJDownloadCallback() {
                            @Override
                            public void onDownloadFinish(BJResponse bjResponse, File file) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "信令文件下完了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(long l, long l1) {

                            }

                            @Override
                            public void onFailure(HttpException e) {
                            }
                        });

            }
        });
        mRoom.setOnPlayerListener(new OnPlayerListener() {
            @Override
            public void onVideoInfoInitialized(BJPlayerView bjPlayerView, long l, HttpException e) {

            }

            @Override
            public void onError(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onSeekComplete(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onSpeedUp(BJPlayerView bjPlayerView, float v) {

            }

            @Override
            public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (playerView != null) {
            playerView.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (!playerView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView != null) {
            playerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerView != null) {
            playerView.onDestroy();
        }
        mRoom.quitRoom();
    }

    @Override
    public void onLaunchSteps(int step, int totalStep) {
        mTextView.setText("enter Room " + (step * 100 / totalStep) + "%");
    }

    @Override
    public void onLaunchError(LPError error) {
        Toast.makeText(this, "进房间失败" + error.getCode() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLaunchSuccess(LiveRoom liveRoom) {
        mTextView.setText("enter susccess");

//        playerView.playVideo();
        mRoom.getObservableOfUserNumberChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mTextView.setText("当前用户数: " + integer);
                    }
                });

    }

    private PagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mPPTFragment;
            } else if (position == 1) {
                return mUserListFragment;
            } else {
                return mMessageListFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "PPT";
            } else if (position == 1) {
                return "用户名单";
            } else {
                return "聊天";
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };
}
