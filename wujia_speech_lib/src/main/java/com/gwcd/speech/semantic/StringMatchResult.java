package com.gwcd.speech.semantic;

import com.gwcd.speech.utils.SpeechLog;

/**
 * Created by sy on 2017/3/8.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/8 14:45<br>
 * Revise Record:<br>
 * 2017/3/8: 创建并完成初始实现<br>
 */

public class StringMatchResult {
    /** 匹配到的字符串 */
    public StringItem item;
    /** 匹配到的位置，用于获取温度数值等参数 */
    public int matchPos;

    public StringMatchResult(StringItem item, int matchPos) {
        this.item = item;
        this.matchPos = matchPos;
    }

    public String dumpInfo() {
        SpeechLog.d("StringMatchResult:");
        SpeechLog.d("Match at pos:" + matchPos);

        String itemDump = item.dumpInfo();
        StringBuffer sb = new StringBuffer("StringMatchResult:");
        sb.append("\nMatch at pos:" + matchPos);
        sb.append("\n" + itemDump);
        return sb.toString();
    }
}
