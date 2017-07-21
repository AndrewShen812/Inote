package com.gwcd.speech.semantic.types;

/**
 * Created by sy on 2017/3/15.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/15 10:24<br>
 * Revise Record:<br>
 * 2017/3/15: 创建并完成初始实现<br>
 */

public enum DevType {

    /** 无效类型 */
    DEV_NONE,
    /** 设备类型-空调（增强版和普通悟空、中央、格力、mini悟空、林肯、科希曼） */
    DEV_AIRCON,
    /** 设备类型-插座 单火线一类（wifi插座、MacBee插座、单火线、零火线、凯特、嘉德） */
    DEV_SOCKET,
    /** 设备类型-灯（wifi灯、魔灯、音速灯、电威灯、灯带、MacBee灯） */
    DEV_LIGHT,
    /** 设备类型-门锁（门闩、汇泰龙锁） */
    DEV_LOCK,
    /** 设备类型-门磁（v1、v2门磁） */
    DEV_MAGNET,
    /** 设备类型-控制盒 */
    DEV_BOX,
    /** 设备类型-人体感应（v1、v2，夜狼，悟安S6） */
    DEV_INDUCTION,
    /** 设备类型-暖气阀 */
    DEV_HEATING,
    /** 设备类型-窗帘（wifi窗帘、MacBee窗帘） */
    DEV_CURTAIN,
    /** 设备类型-甲醛(甲醛，甲醛感应器、传感器) */
    DEV_CH2O,
    /** 设备类型-摄像头 */
    DEV_CAMERA,
    /** 设备类型-声光报警器 */
    DEV_SOUND_LIGHT,
    /** 设备类型-网关 */
    DEV_GATEWAY,
    /** 设备类型-所有/全部 */
    DEV_ALL;

    private static final byte DT_NONE = 0;
    private static final byte DT_AIRCON = 1;
    private static final byte DT_SOCKET = 2;
    private static final byte DT_LIGHT = 3;
    private static final byte DT_LOCK = 4;
    private static final byte DT_MAGNET = 5;
    private static final byte DT_BOX = 6;
    private static final byte DT_INDUCTION = 7;
    private static final byte DT_HEATING = 8;
    private static final byte DT_CURTAIN = 9;
    private static final byte DT_CH2O = 10;
    private static final byte DT_CAMERA = 11;
    private static final byte DT_SOUND_LIGHT = 12;
    private static final byte DT_GATEWAY = 13;
    private static final byte DT_ALL = 14;

    public static DevType valueOf(int value) {
        switch (value) {
            case DT_NONE:
                return DEV_NONE;
            case DT_AIRCON:
                return DEV_AIRCON;
            case DT_SOCKET:
                return DEV_SOCKET;
            case DT_LIGHT:
                return DEV_LIGHT;
            case DT_LOCK:
                return DEV_LOCK;
            case DT_MAGNET:
                return DEV_MAGNET;
            case DT_BOX:
                return DEV_BOX;
            case DT_INDUCTION:
                return DEV_INDUCTION;
            case DT_HEATING:
                return DEV_HEATING;
            case DT_CURTAIN:
                return DEV_CURTAIN;
            case DT_CH2O:
                return DEV_CH2O;
            case DT_CAMERA:
                return DEV_CAMERA;
            case DT_SOUND_LIGHT:
                return DEV_SOUND_LIGHT;
            case DT_GATEWAY:
                return DEV_GATEWAY;
            case DT_ALL:
                return DEV_ALL;
            default:
                return DEV_NONE;
        }
    }
}
