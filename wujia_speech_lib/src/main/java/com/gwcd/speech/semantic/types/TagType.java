package com.gwcd.speech.semantic.types;

/**
 * Created by sy on 2017/3/15.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/15 10:35<br>
 * Revise Record:<br>
 * 2017/3/15: 创建并完成初始实现<br>
 */

public enum TagType {

    /** 无效类型 */
    TAG_NONE,
    /** 默认标签-温控类 */
    TAG_DEF_TEMPER,
    /** 默认标签-安防类 */
    TAG_DEF_SECURITY,
    /** 默认标签-照明类 */
    TAG_DEF_LIGHTING,
    /** 默认标签-其他 */
    TAG_DEF_OTHER,
    /** 自定义标签 */
    TAG_CUSTOM
}
