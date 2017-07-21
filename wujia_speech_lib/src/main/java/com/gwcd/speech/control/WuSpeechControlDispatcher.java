package com.gwcd.speech.control;

import android.os.Bundle;

import com.gwcd.speech.semantic.StringMatchResult;

import java.util.List;

/**
 * Created by sy on 2017/3/23.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/23 14:11<br>
 * Revise Record:<br>
 * 2017/3/23: 创建并完成初始实现<br>
 */

public class WuSpeechControlDispatcher {

    public boolean dispatchToController(List<StringMatchResult> matchItems) {
        if (matchItems == null || matchItems.isEmpty()) {
            return false;
        }
        WuSpeechControllerManager controllerManager = WuSpeechControllerManager.getInstance();
        List<AirConController> airCons = controllerManager.getAirConControllers();
        for (AirConController airCon : airCons) {
            airCon.setPower(new Bundle());
        }

        return true;
    }
}
