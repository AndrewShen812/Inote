/**
 * Project name：Inote
 * Create time：2016/12/20 17:49
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.lf.inote.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by sy on 2016/12/20.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/12/20 17:49<br>
 * Revise Record:<br>
 * 2016/12/20: 创建并完成初始实现<br>
 */
public class WheelDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 是否显示标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View root = inflater.inflate(R.layout.layout_wheel_dialog, container, false);
        WheelView wheelView = (WheelView) root.findViewById(R.id.wheel_dialog_wheel);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(getActivity())); // 文本数据源
        wheelView.setSkin(WheelView.Skin.Holo); // common皮肤
        wheelView.setExtraText("市", Color.BLUE, 64, 120);
//        wheelView.setStyle();
        ArrayList<String> data = new ArrayList<>();
        data.add("北京");
        data.add("上海");
        data.add("广州");
        data.add("深圳");
        wheelView.setWheelData(data);  // 数据集合
        return root;
    }
}
