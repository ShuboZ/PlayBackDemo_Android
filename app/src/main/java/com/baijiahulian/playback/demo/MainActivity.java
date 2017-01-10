package com.baijiahulian.playback.demo;

import android.content.res.Configuration;
import android.os.Bundle;
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
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.playerview.BJBottomViewPresenter;
import com.baijiahulian.player.playerview.BJCenterViewPresenter;
import com.baijiahulian.player.playerview.BJTopViewPresenter;

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

        long classId = getIntent().getLongExtra("classId", 0);
        //1. 创建房间。
        mRoom = LivePlaybackSDK.newPlayBackRoom(this, 32958737L, classId, LPConstants.LPDeployType.Test);

        //2. 进入房间
        mRoom.enterRoom(this);
        mPPTFragment = new LPPPTFragment();
        mPPTFragment.setLiveRoom(mRoom);

        mUserListFragment = new UserListFragment();
        mUserListFragment.setRoom(mRoom);

        mMessageListFragment = new MessageListFragment();
        mMessageListFragment.setRoom(mRoom);

        mRoom.bindPlayerView(playerView);

        mTabLayout.addTab(mTabLayout.newTab().setText("PPT"));
        mTabLayout.addTab(mTabLayout.newTab().setText("用户名单"));
        mTabLayout.addTab(mTabLayout.newTab().setText("聊天"));
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
        Toast.makeText(this, "进房间失败" + error.getCode() +" " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLaunchSuccess(LiveRoom liveRoom) {
        mTextView.setText("enter susccess");

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
