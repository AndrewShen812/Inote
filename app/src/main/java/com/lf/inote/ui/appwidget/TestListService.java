/**
 * Project name：Inote
 * Create time：2016/12/26 18:36
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lf.inote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sy on 2016/12/26.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/12/26 18:36<br>
 * Revise Record:<br>
 * 2016/12/26: 创建并完成初始实现<br>
 */
public class TestListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TestListFactory(getApplicationContext(), intent);
    }

    public static final int COUNT = 10;
    private List<String> mItemData = new ArrayList<>();

    private class TestListFactory implements RemoteViewsFactory {

        private static final String TAG = "TestListFactory";

        private Context mContext;
        private int mAppWidgetId;

        public TestListFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG, "SpeechListFactory mAppWidgetId:" + mAppWidgetId);
        }

        @Override
        public void onCreate() {
            for (int i = 0; i < COUNT; i++) {
                mItemData.add("这是第 " + i + " 个子项");
            }
        }

        @Override
        public void onDataSetChanged() {}

        @Override
        public void onDestroy() {
            mItemData.clear();
        }

        @Override
        public int getCount() {
            return COUNT;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_app_widget_test_item);
            rv.setTextViewText(R.id.tv_widget_test_item, mItemData.get(position));

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
}
