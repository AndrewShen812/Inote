package com.gwcd.speech.semantic.test;

import com.gwcd.speech.semantic.StrHashTable;
import com.gwcd.speech.semantic.StringItem;
import com.gwcd.speech.semantic.StringMatchResult;
import com.gwcd.speech.semantic.types.ItemType;

import java.util.List;

public class TestClass {

//    mInnerItems = new StringItem[] {
//        // 悟空
//        new StringItem("悟空", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_AIRCON),
//                new StringItem("空调", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_AIRCON),
//                new StringItem("空调插座", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_AIRCON),
//                // 悟能
//                new StringItem("悟能", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_SOCKET),
//                new StringItem("插座", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_SOCKET),
//                new StringItem("悟能插座", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_SOCKET),
//                new StringItem("开关", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_SOCKET),
//                // 灯
//                new StringItem("魔灯", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_LIGHT),
//                new StringItem("灯", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_LIGHT),
//                new StringItem("led灯", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_LIGHT),
//                // 门锁
//                new StringItem("锁", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_LOCK),
//                new StringItem("门锁", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_LOCK),
//                // 门磁
//                new StringItem("门磁", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_MAGNET),
//                // 86控制盒
//                new StringItem("盒子", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_BOX),
//                new StringItem("控制盒", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_BOX),
//                // 人体感应
//                new StringItem("人体感应", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_INDUCTION),
//                new StringItem("红外感应", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_INDUCTION),
//                new StringItem("红外", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_INDUCTION),
//                // 暖气阀
//                new StringItem("暖气", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_HEATING),
//                new StringItem("暖气阀", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_HEATING),
//                // 窗帘
//                new StringItem("窗", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_CURTAIN),
//                new StringItem("窗帘", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_CURTAIN),
//                // 全部
//                new StringItem("所有", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_ALL),
//                new StringItem("全部", ITEM_DEV_TYPE, 0L, 0, 0, DevType.DEV_ALL),
//                // 开动作
//                new StringItem("开",   ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_ON),
//                new StringItem("开启", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_ON),
//                new StringItem("打开", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_ON),
//                new StringItem("开机", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_ON),
//                // 关动作
//                new StringItem("关",   ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_OFF),
//                new StringItem("停止", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_OFF),
//                new StringItem("关闭", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_OFF),
//                new StringItem("关掉", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_OFF),
//                // 布防动作
//                new StringItem("布防", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_ARM),
//                // 撤防动作
//                new StringItem("撤防", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_DISARM),
//                // 执行动作
//                new StringItem("执行", ITEM_ACTION, 0L, 0, 0, ActionType.ACTION_EXECUTE),
//    };

    public static void main(String[] args) {
        TestClass t = new TestClass();
        t.testHashTable();
    }

    private class StrInfo {
        public String str;
        public int type;
        public long sn;
        public int handle;
        public int sceneId;

        public StrInfo(String str, int type, long sn, int handle, int sceneId) {
            this.str = str;
            this.type = type;
            this.sn = sn;
            this.handle = handle;
            this.sceneId = sceneId;
        }
    }

    private void testHashTable() {
        StrHashTable hashTable = new StrHashTable();
        StringItem item;
        List<StringMatchResult> array;

        StrInfo[] testinfo = {
                // 一些设备
                new StrInfo("客厅空调", 0, 808000000001L, 100001, 0),
                new StrInfo("卧室门磁", 0, 808000000002L, 100002, 0),
                new StrInfo("卧室红外", 0, 808000000003L, 100003, 0),
                new StrInfo("客厅电视", 0, 80800000000L, 100004, 0),
                //一些标签
                new StrInfo("客厅", 1, 0, 0, 0),
                new StrInfo("卧室", 1, 0, 0, 0),
                new StrInfo("厨房", 1, 0, 0, 0),
                // 一些设备类型
                new StrInfo("空调", 2, 0, 0, 1),
                new StrInfo("悟空", 2, 0, 0, 1),
                new StrInfo("门磁", 2, 0, 0, 2),
                new StrInfo("红外", 2, 0, 0, 3),
                new StrInfo("安防设备", 2, 0, 0, 4),
                new StrInfo("所有", 2, 0, 0, 0xFF),
                new StrInfo("开关", 2, 0, 0, 0),
                // 一些情景模式
                new StrInfo("回家", 3, 0, 0, 0),
                new StrInfo("离家", 3, 0, 0, 0),
                new StrInfo("休息", 3, 0, 0, 0),
                new StrInfo("打开客厅空调", 3, 0, 0, 0),
                // 一些动作
                new StrInfo("执行", 4, 0, 0, 1),
                new StrInfo("开", 4, 0, 0, 1),
                new StrInfo("关", 4, 0, 0, 0),
                new StrInfo("停止", 4, 0, 0, 0),
                new StrInfo("开启", 4, 0, 0, 1),
                new StrInfo("打开", 4, 0, 0, 0),
                new StrInfo("关闭", 4, 0, 0, 0),
                new StrInfo("布防", 4, 0, 0, 0),
                new StrInfo("撤防", 4, 0, 0, 0),
                // 其他属性
                new StrInfo("不要", 5, 0, 0, 0),
                new StrInfo("制冷", 5, 0, 0, 0),
                new StrInfo("制热", 5, 0, 0, 0),
                new StrInfo("自动", 5, 0, 0, 0),
                new StrInfo("^[0-9]{1,2}度", 5, 0, 0, 0),
                new StrInfo("^[0-9]{1,2}摄氏度", 5, 0, 0, 0),
                new StrInfo("^[0-9]{1,2}℃", 5, 0, 0, 0),
        };

        for (int i = 0; i < testinfo.length; i++) {
            StrInfo si = testinfo[i];
            item = new StringItem(si.str, ItemType.valueOf(si.type), si.sn, si.handle, si.sceneId);
            hashTable.addStrObjectToHashTable(item);
        }

//        hashTable.dumpHashTable();

        String speechString;

//        speechString = "开启客厅空调";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str " + speechString  + " --------------------");
//        hashTable.dumpResultArray(array);

//        speechString = "打开客厅空调";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str " + speechString  + " --------------------");
//        hashTable.dumpResultArray(array);

//        speechString = "开启所有空调";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str " + speechString  + " --------------------");
//        hashTable.dumpResultArray(array);

//        speechString = "关闭卧室红外";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str " + speechString  + " --------------------");
//        hashTable.dumpResultArray(array);
//
//        speechString = "关闭所有红外";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str " + speechString  + " --------------------");
//        hashTable.dumpResultArray(array);

//        speechString = "执行回家";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);

//        speechString = "开开关";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);
//
//        speechString = "关开关";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);
//
//        speechString = "开关开";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);
//
//        speechString = "开关关";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);

        speechString = "空调26度";
        array = hashTable.matchObjectByString(speechString);
        System.out.println("match str  " + speechString  + "--------------------");
        hashTable.dumpResultArray(array);

//        speechString = "客厅空调制冷";
//        array = hashTable.matchObjectByString(speechString);
//        System.out.println("match str  " + speechString  + "--------------------");
//        hashTable.dumpResultArray(array);

//        String reg = "^[0-9]{1,2}度";
//        System.out.println(speechString.matches(reg));
    }
}
