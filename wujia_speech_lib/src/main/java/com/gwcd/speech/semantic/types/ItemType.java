package com.gwcd.speech.semantic.types;

/**
 * Created by sy on 2017/3/8.<br>
 * Function: 条目类型，按识别优先级排列，识别优先级：情景>设备>标签>动作>设备类型<br>
 *     ordinal值越小，优先级越高<br>
 * Creator: sy<br>
 * Create time: 2017/3/8 14:44<br>
 * Revise Record:<br>
 * 2017/3/8: 创建并完成初始实现<br>
 */

public enum ItemType {

    /** 情景名称 */
    ITEM_SCENE,
    /** 设备昵称 */
    ITEM_DEV_NICK_NAME,
    /** 标签 */
    ITEM_TAG_NAME,
    /** 动作 */
    ITEM_ACTION,
    /** 设备类型 */
    ITEM_DEV_TYPE,
    /** 规则 */
    ITEM_RULE,
    /** 更多参数，如温度、湿度等 */
    ITEM_MORE_PARAM,
    ITEM_NONE;

    public static ItemType valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return ITEM_DEV_NICK_NAME;
            case 1:
                return ITEM_TAG_NAME;
            case 2:
                return ITEM_DEV_TYPE;
            case 3:
                return ITEM_SCENE;
            case 4:
                return ITEM_ACTION;
            case 5:
                return ITEM_MORE_PARAM;
            default:
                return null;
        }
    }

    /**
     * 是否优先级更高
     * @param comparedType
     * @return
     */
    public boolean isPrioHigher(ItemType comparedType) {
        return ordinal() <= comparedType.ordinal();
    }

    public boolean isPrioLower(ItemType comparedType) {
        return ordinal() > comparedType.ordinal();
    }
}
