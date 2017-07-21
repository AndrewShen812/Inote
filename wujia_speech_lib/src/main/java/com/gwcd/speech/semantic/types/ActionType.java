package com.gwcd.speech.semantic.types;

/**
 * Created by sy on 2017/3/14.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/14 11:52<br>
 * Revise Record:<br>
 * 2017/3/14: 创建并完成初始实现<br>
 */

public enum ActionType {

    /** 无效类型 */
    ACTION_NONE,
    /** 动作类型-开 */
    ACTION_ON,
    /** 动作类型-关 */
    ACTION_OFF,
    /** 动作类型-布防 */
    ACTION_ARM,
    /** 动作类型-撤防 */
    ACTION_DISARM,
    /** 动作类型-执行 */
    ACTION_EXECUTE;

    private static final byte ACT_NONE = 0;
    private static final byte ACT_ON = 1;
    private static final byte ACT_OFF = 2;
    private static final byte ACT_ARM = 3;
    private static final byte ACT_DISARM = 4;
    private static final byte ACT_EXECUTE = 5;

    public static ActionType valueOf(int value) {
        switch (value) {
            case ACT_NONE:
                return ACTION_NONE;
            case ACT_ON:
                return ACTION_ON;
            case ACT_OFF:
                return ACTION_OFF;
            case ACT_ARM:
                return ACTION_ARM;
            case ACT_DISARM:
                return ACTION_DISARM;
            case ACT_EXECUTE:
                return ACTION_EXECUTE;
            default:
                return ACTION_NONE;
        }
    }
}
