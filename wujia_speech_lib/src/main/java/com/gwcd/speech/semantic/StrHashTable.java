package com.gwcd.speech.semantic;

import com.gwcd.speech.semantic.types.ItemType;
import com.gwcd.speech.utils.SpeechLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sy on 2017/3/7.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/7 16:28<br>
 * Revise Record:<br>
 * 2017/3/7: 创建并完成初始实现<br>
 */

public class StrHashTable {

    private static final short INIT_HASH_NUM = 256;
    private static final short INIT_MIN_STR_LEN = 32767;
    /** 无效的下标 */
    private static final int INVALID_INDEX = -1;
    /** 无效的长度 */
    private static final int INVALID_LEN = -1;
    private short hashNum;
    private int minStrlength;
    private List<List<StringItem>> hashArray;

    public StrHashTable() {
        this.hashNum = INIT_HASH_NUM;
        /** 初始化成大数 */
        this.minStrlength = INIT_MIN_STR_LEN;
        this.hashArray = new ArrayList<>(hashNum);
        for (int i = 0; i < hashNum; i++) {
            hashArray.add(null);
        }
    }

    public void addItemToSortedArray(StringItem item, List<StringItem> destArray) {
        StringItem cur;
        int i;

        for (i = 0; i < destArray.size(); i++) {
            cur = destArray.get(i);
            // 从长到短排列，节省全匹配时间
            if (cur.keyStr.length() < item.keyStr.length() && !destArray.contains(item)) {
                addItemToArray(destArray, i, item);
                break;
            } else if (cur.keyStr.length() == item.keyStr.length() && !destArray.contains(item)) {
                // 如果等长，按优先级从高到低排列
                if (item.type.isPrioHigher(cur.type)) {
                    addItemToArray(destArray, i, item);
                } else {
                    // 比当前等长的item优先级低，向后查找插入位置
                    int desirePos = -1;
                    for (int j = i; j < destArray.size(); j++) {
                        StringItem tmpItem = destArray.get(j);
                        if (tmpItem.keyStr.length() < item.keyStr.length()) {
                            break;
                        }
                        if (item.keyStr.length() == tmpItem.keyStr.length() && tmpItem.type.isPrioLower(item.type)) {
                            desirePos = j;
                            break;
                        }
                    }
                    if (desirePos >= 0) {
                        destArray.add(desirePos, item);
                    } else {
                        addItemToArray(destArray, i, item);
                    }
                }
                break;
            }
        }
        if (i >= destArray.size() && !destArray.contains(item)) {
            destArray.add(item);
        }
    }

    private void addItemToArray(List<StringItem> destArray, int destPos, StringItem item) {
        if (destPos == 0) {
            destArray.add(destPos, item);
        } else {
            destArray.add(destPos - 1, item);
        }
    }

    public boolean addStrObjectToHashTable(StringItem item) {
        int c;
        List<StringItem> destArray;

        if (item == null || item.keyLen == 0) {
            return false;
        }

        c = item.keyStr.charAt(0);
        c = c % hashNum;
        destArray = hashArray.get(c);
        if (destArray == null) {
            destArray = new ArrayList<>(32);
            hashArray.set(c, destArray);
        }

        addItemToSortedArray(item, destArray);
        if (item.keyLen < minStrlength) {
            minStrlength = item.keyLen;
        }

        return true;
    }

    /**
     *
     * @param destString
     * @param pos
     * @param resArray
     * @return 匹配到的字符串长，度如果是INVALID_LEN，则无效
     */
    public StringMatchResult matchStringCache(String destString, int pos, List<StringMatchResult> resArray) {
        int c = destString.charAt(0);
        List<StringItem> itemArray;
        StringItem item;
        StringMatchResult res;

        c = c % hashNum;
        itemArray = hashArray.get(c);
        if (itemArray == null) {
             return null;
        }

        for (int i = 0; i < itemArray.size(); i++) {
            item = itemArray.get(i);
            if (destString.startsWith(item.keyStr)) {
                //匹配成功
                res = new StringMatchResult(item, pos);
                resArray.add(res);
                return res;
            }
        }

        return null;
    }

    private String tempPattern = "^(-|负|零下)?[0-9]{1,2}(\\.5)?(度|℃|℉|摄氏度|华氏度){1}.*$";
    public StringMatchResult matchTempParam(String destString, int pos, List<StringMatchResult> resArray) {
        if (destString.matches(tempPattern)) {
            // 查找最短能匹配的子串
            for (int k = 0; k <= destString.length(); k++) {
                String minStr = destString.substring(0, k);
                if (minStr.matches(tempPattern)) {
                    StringItem item = new StringItem(minStr, ItemType.ITEM_MORE_PARAM, 0L, 0, 0);
                    StringMatchResult res = new StringMatchResult(item, pos);
                    resArray.add(res);
                    return res;
                }
            }
        }


        return null;
    }

    public List<StringMatchResult> matchObjectByString(String speechString) {
        List<StringMatchResult> array = new ArrayList<>(32);
        String subString;
        int totalLen;

        if (speechString == null || speechString.isEmpty()) {
            return array;
        }

        totalLen = speechString.length();
        for (int i = 0; i < totalLen;) {
            if (totalLen - i < minStrlength) {
                break;
            }
            subString = speechString.substring(i);
            StringMatchResult res = matchStringCache(subString, i, array);
            if (res == null) {
                // 尝试匹配温度参数
                res = matchTempParam(subString, i, array);
            }
            // 如果匹配到了hashTable中的item，就从截取整个item之后的子串中继续匹配
            i = res != null ? i + res.item.keyLen : i + 1;
        }

        return array;
    }

    public void dumpResultArray(List<StringMatchResult> array) {
        for (StringMatchResult item : array) {
            item.dumpInfo();
        }
    }

    public void dumpHashTable() {
        System.out.println("HashTable all data");
        List<StringItem> stringItemArray;
        StringItem item;
        for (int i = 0; i < hashNum; i++) {
            stringItemArray = hashArray.get(i);
            if (stringItemArray == null) {
                SpeechLog.d("Table [" + i + "] has zero item");
            } else {
                SpeechLog.d("Table [" + i + "] has [" + stringItemArray.size() + "] item");
                for (int k = 0; k < stringItemArray.size(); k++) {
                    item = stringItemArray.get(k);
                    item.dumpInfo();
                }
            }
        }
    }

    public void clearHashTable() {
        hashArray.clear();
        for (int i = 0; i < hashNum; i++) {
            hashArray.add(null);
        }
    }
}
