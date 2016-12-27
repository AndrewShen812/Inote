/**
 * Project name：Inote
 * Create time：2016/12/26 15:49
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lf.inote.NoteApp;
import com.lf.inote.R;

/**
 * Created by sy on 2016/12/26.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/12/26 15:49<br>
 * Revise Record:<br>
 * 2016/12/26: 创建并完成初始实现<br>
 */
public class SpeechWidgetService extends RemoteViewsService {

    private static final String TAG = "SpeechWidgetService";

    public static final String ACTION_MSG_UPDATE = "com.lf.inote.ACTION_MSG_UPDATE";

    private TalkMsg mTalkMsg = new TalkMsg();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SpeechWidgetService onReceive: ");
            if (ACTION_MSG_UPDATE.equals(intent.getAction())) {
                String robotMsg = intent.getStringExtra("robotMsg");
                if (!TextUtils.isEmpty(robotMsg)) {
                    mTalkMsg.mRobotMsg = robotMsg;
                }
                String userMsg = intent.getStringExtra("userMsg");
                if (!TextUtils.isEmpty(userMsg)) {
                    mTalkMsg.mUserMsg = intent.getStringExtra("userMsg");
                }
                Log.d(TAG, "SpeechWidgetService robotMsg: " + robotMsg);
                Log.d(TAG, "SpeechWidgetService userMsg: " + userMsg);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SpeechWidgetService onCreate");
        mTalkMsg.mRobotMsg = NoteApp.getAppContext().getString(R.string.text_widget_robot_tip);
        mTalkMsg.mUserMsg = NoteApp.getAppContext().getString(R.string.text_widget_user_tip);
        registerReceiver(mReceiver, new IntentFilter(ACTION_MSG_UPDATE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SpeechWidgetService onDestroy");
        unregisterReceiver(mReceiver);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SpeechListFactory(this, intent);
    }

    private class SpeechListFactory implements RemoteViewsFactory {

        private static final String TAG = "SpeechListFactory";

        private Context mContext;
        private int mAppWidgetId;

        public SpeechListFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG, "SpeechListFactory mAppWidgetId:" + mAppWidgetId);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            // 暂时固定只显示一组对话
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_app_widget_list_item);
            if (!TextUtils.isEmpty(mTalkMsg.mRobotMsg)) {
                rv.setTextViewText(R.id.tv_app_widget_item_robot, mTalkMsg.mRobotMsg);
            }
            if (!TextUtils.isEmpty(mTalkMsg.mUserMsg)) {
                rv.setTextViewText(R.id.tv_app_widget_item_user, mTalkMsg.mUserMsg);
            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    private class TalkMsg {
        String mRobotMsg;
        String mUserMsg;
    }
}
