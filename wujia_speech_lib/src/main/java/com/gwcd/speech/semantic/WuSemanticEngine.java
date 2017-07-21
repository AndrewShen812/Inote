package com.gwcd.speech.semantic;

import android.content.Context;

import com.gwcd.speech.R;
import com.gwcd.speech.WuSpeechComponent;
import com.gwcd.speech.control.WuSpeechControllerManager;
import com.gwcd.speech.semantic.types.ActionType;
import com.gwcd.speech.semantic.types.DevType;
import com.gwcd.speech.semantic.types.ItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sy on 2017/3/15.<br>
 * Function: 语义解析引擎<br>
 * Creator: sy<br>
 * Create time: 2017/3/15 13:56<br>
 * Revise Record:<br>
 * 2017/3/15: 创建并完成初始实现<br>
 */

public class WuSemanticEngine extends WuSpeechComponent {

    /** 识别引擎核心--hash表 */
    private StrHashTable mStrHashTable;
    private Context mAppContext;
    private StringItem[] mInnerItems;
    private StringItem[] mImportItems;
    private WuSpeechControllerManager mControllerManager;

    public WuSemanticEngine(Context context) {
        mAppContext = context.getApplicationContext();
        mStrHashTable = new StrHashTable();
        addInnerStringItems();
    }

    public WuSemanticEngine(Context context, StringItem[] importItems) {
        this(context);
        addImportStringItems(importItems);
        mStrHashTable.dumpHashTable();
    }

    /**
     * 获取语义解析结果<br>
     * 识别优先级：情景>设备>标签>动作>设备类型
     * @param speechString
     * @return
     */
    public List<StringMatchResult> getResult(String speechString) {
        return mStrHashTable.matchObjectByString(speechString);
    }

    /**
     * 添加内置的信息项
     */
    private void addInnerStringItems() {
        String[] innerItems = mAppContext.getResources().getStringArray(R.array.inner_dev_type_items);
        List<StringItem> list = new ArrayList<>();
        addInnerItems(innerItems, list, ItemType.ITEM_DEV_TYPE);
        innerItems = mAppContext.getResources().getStringArray(R.array.inner_action_items);
        addInnerItems(innerItems, list, ItemType.ITEM_ACTION);
        innerItems = mAppContext.getResources().getStringArray(R.array.inner_param_items);
        addInnerItems(innerItems, list, ItemType.ITEM_MORE_PARAM);
        mInnerItems = new StringItem[list.size()];
        list.toArray(mInnerItems);
    }

    private void addInnerItems(String[] innerItems, List<StringItem> list, ItemType itemType) {
        for (String line : innerItems) {
            try {
                String[] info = line.split(",");
                if (info == null || info.length != 2) {
                    continue;
                }
                StringItem item = null;
                if (itemType == ItemType.ITEM_DEV_TYPE) {
                    item = new StringItem(info[0].trim(), itemType, 0L, 0, 0,
                            DevType.valueOf(Integer.parseInt(info[1].trim())));
                } else if (itemType == ItemType.ITEM_ACTION) {
                    item = new StringItem(info[0].trim(), itemType, 0L, 0, 0,
                            ActionType.valueOf(Integer.parseInt(info[1].trim())));
                } else if (itemType == ItemType.ITEM_MORE_PARAM) {
                    item = new StringItem(info[0].trim(), itemType, 0L, 0, 0);
                }

                if (item != null && item.isDataValid()) {
                    list.add(item);
                    mStrHashTable.addStrObjectToHashTable(item);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加外部导入的信息项
     * @param communityInfo
     */
    public void addImportStringItems(StringItem[] communityInfo) {
        mImportItems = communityInfo;
        for (StringItem item : communityInfo) {
            if (item.isDataValid()) {
                mStrHashTable.addStrObjectToHashTable(item);
            }
        }
    }

    public void clearImportItems() {
        // TODO: 2017/3/15 实现的效率怎么样？
        mStrHashTable.clearHashTable();
        for (StringItem item : mInnerItems) {
            mStrHashTable.addStrObjectToHashTable(item);
        }
    }
}
