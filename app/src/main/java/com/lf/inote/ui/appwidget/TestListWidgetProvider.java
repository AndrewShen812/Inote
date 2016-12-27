/**
 * Project name：Inote
 * Create time：2016/12/26 18:31
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.lf.inote.R;

/**
 * Created by sy on 2016/12/26.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/12/26 18:31<br>
 * Revise Record:<br>
 * 2016/12/26: 创建并完成初始实现<br>
 */
public class TestListWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int qppWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_app_widget_test_list);
            remoteViews.setRemoteAdapter(R.id.lv_app_widget_test_list, new Intent(context, TestListService.class));

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, TestListService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, TestListService.class));
    }
}
