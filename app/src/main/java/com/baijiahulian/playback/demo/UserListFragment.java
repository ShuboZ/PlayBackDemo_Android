package com.baijiahulian.playback.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baijia.player.playback.PBRoom;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.utils.LPBackPressureBufferedSubscriber;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yanglei on 2016/12/22.
 */

public class UserListFragment extends Fragment {

    private PBRoom mRoom;
    public void setRoom(PBRoom room) {
        mRoom = room;
    }


    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.userList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(userAdapter);

        mRoom.getOnlineUserVM().getObservableOfOnlineUser()
                .onBackpressureBuffer(1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPBackPressureBufferedSubscriber<List<IUserModel>>() {
                    @Override
                    public void call(List<IUserModel> iUserModels) {
                        userAdapter.notifyDataSetChanged();
                    }
                });

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            userInfo = (TextView) itemView.findViewById(R.id.userinfo);
        }
    }

    private RecyclerView.Adapter userAdapter = new RecyclerView.Adapter<ViewHolder>() {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_user, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            IUserModel userModel = mRoom.getOnlineUserVM().getUser(position);
            holder.userInfo.setText(String.valueOf(userModel.getName()));
        }

        @Override
        public int getItemCount() {
            return mRoom.getOnlineUserVM().getUserCount();
        }
    };


}
