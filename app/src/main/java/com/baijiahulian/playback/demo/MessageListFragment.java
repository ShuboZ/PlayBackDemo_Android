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
import com.baijiahulian.livecore.models.imodels.IMessageModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by yanglei on 2016/12/22.
 */

public class MessageListFragment extends Fragment {

    private PBRoom mRoom;
    public void setRoom(PBRoom room) {
        mRoom = room;
        mRoom.getChatVM(); // viewModel 都是懒加载, 不初始化，整个消息模块都不会运行起来。
    }


    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.userList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(messageAdapter);

        mRoom.getChatVM().notifyDataChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                messageAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            messageInfo = (TextView) itemView.findViewById(R.id.userinfo);
        }
    }

    private RecyclerView.Adapter messageAdapter = new RecyclerView.Adapter<ViewHolder>() {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_user, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            IMessageModel messageModel = mRoom.getChatVM().getMessage(position);

            holder.messageInfo.setText(messageModel.getFrom().getName()+":" + messageModel.getContent());
        }

        @Override
        public int getItemCount() {
            return mRoom.getChatVM().getMessageCount();
        }
    };


}
