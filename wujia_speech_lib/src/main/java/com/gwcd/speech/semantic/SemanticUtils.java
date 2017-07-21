package com.gwcd.speech.semantic;

import android.text.TextUtils;

import com.gwcd.speech.semantic.types.ItemType;

import java.util.List;

/**
 * Created by sy on 2017/3/14.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/14 11:32<br>
 * Revise Record:<br>
 * 2017/3/14: 创建并完成初始实现<br>
 */

public class SemanticUtils {

    /**
     *
     * @param array
     * @return
     */
    public static boolean isMatchResultValid(List<StringMatchResult> array) {
        if (array == null || array.isEmpty()) {
            return false;
        }

        // 无效情况：动作多于两个/只有动作
        int actionCnt = 0;
        int otherCnt = 0;
        for (StringMatchResult result : array) {
            if (result.item.type == ItemType.ITEM_ACTION) {
                actionCnt++;
            } else {
                otherCnt++;
            }
        }

        return actionCnt > 0 && otherCnt > 0;
    }

    public static String orderedDumpInfo(List<StringMatchResult> array) {
        StringBuffer sb = new StringBuffer("结果: ");
        String action = "";
        for (StringMatchResult r : array) {
            if (r.item.type == ItemType.ITEM_ACTION) {
                if (!TextUtils.isEmpty(action)) {
                    action += "  ";
                }
                action += r.item.keyStr;
            }
        }
        String actionDesc = TextUtils.isEmpty(action) ? "[无动作]" : "动作:" + action;
        sb.append(actionDesc + "\t");
        int cnt = 0;
        for (StringMatchResult r : array) {
            switch (r.item.type) {
                case ITEM_DEV_NICK_NAME:
                    cnt++;
                    sb.append("设备类型:" + r.item.keyStr  + "\t");
                    break;
                case ITEM_TAG_NAME:
                    cnt++;
                    sb.append("标签:" + r.item.keyStr  + "\t");
                    break;
                case ITEM_DEV_TYPE:
                    cnt++;
                    sb.append("设备类型:" + r.item.keyStr  + "\t");
                    break;
                case ITEM_SCENE:
                    cnt++;
                    sb.append("情景模式:" + r.item.keyStr  + "\t");
                    break;
                case ITEM_MORE_PARAM:
                    cnt++;
                    sb.append("其他参数:" + r.item.keyStr  + "\t");
                    break;
            }
        }
        if (cnt  == 0) {
            sb.append("[无动作对象]");
        }
        return sb.toString();
    }
}
