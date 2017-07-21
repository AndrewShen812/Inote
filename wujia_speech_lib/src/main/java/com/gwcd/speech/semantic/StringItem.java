package com.gwcd.speech.semantic;

import com.gwcd.speech.semantic.types.ActionType;
import com.gwcd.speech.semantic.types.DevType;
import com.gwcd.speech.semantic.types.ItemType;
import com.gwcd.speech.semantic.types.TagType;
import com.gwcd.speech.utils.SpeechLog;

/**
 * Created by sy on 2017/3/8.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/8 14:45<br>
 * Revise Record:<br>
 * 2017/3/8: 创建并完成初始实现<br>
 */

public class StringItem {

    public String keyStr;
    public ItemType type;
    public int keyLen;
    // 以下用于缓存对应数据使用
    public long devSn;
    public int devHandle;
    public int sceneId;

    /** 具体动作属性，当type == ItemType.ITEM_ACTION时有效 */
    public ActionType actionType = ActionType.ACTION_NONE;
    /** 具体设备类型属性，当type == ItemType.ITEM_DEV_TYPE时有效 */
    public DevType devType = DevType.DEV_NONE;
    /** 具体标签属性，当type == ItemType.ITEM_TAG_NAME时有效 */
    public TagType tagType = TagType.TAG_NONE;

    public StringItem(String keyStr, ItemType type, long sn, int handle, int sceneId) {
        this.keyStr = keyStr;
        this.keyLen = keyStr.length();
        this.type = type;
        this.devSn = sn;
        this.devHandle = handle;
        this.sceneId = sceneId;
    }

    public StringItem(String keyStr, ItemType type, long sn, int handle, int sceneId, ActionType actionType) {
        this(keyStr, type, sn, handle, sceneId);
        this.actionType = actionType;
    }

    public StringItem(String keyStr, ItemType type, long sn, int handle, int sceneId, DevType devType) {
        this(keyStr, type, sn, handle, sceneId);
        this.devType = devType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        StringItem objItem = (StringItem) obj;
        if (objItem == null) {
            return false;
        }

        return keyStr.equals(objItem.keyStr)
                && type == objItem.type
                && devSn == objItem.devSn
                && devHandle == objItem.devHandle
                && sceneId == objItem.sceneId;
    }

    public boolean isDataValid() {
        boolean doNotCheck = true;
        if (doNotCheck) {
            return true;
        }
        if (keyStr == null || keyStr.isEmpty()) {
            return false;
        }

        boolean valid = false;
        switch (type) {
            case ITEM_DEV_NICK_NAME:
                valid = devSn > 0 && devHandle > 0;
                break;
            case ITEM_TAG_NAME:
            case ITEM_DEV_TYPE:
            case ITEM_ACTION:
            case ITEM_MORE_PARAM:
                valid = true;
                break;
            case ITEM_SCENE:
                valid = sceneId > 0;
                break;
        }

        return valid;
    }

    public boolean isOtherParam() {
        return type == ItemType.ITEM_MORE_PARAM;
    }

    public String getTypeDesc() {
        String desc;
        switch (type) {
            case ITEM_DEV_NICK_NAME:
                desc = "类型:设备昵称";
                break;
            case ITEM_TAG_NAME:
                desc = "类型:标签名称";
                break;
            case ITEM_DEV_TYPE:
                desc = "类型:设备类型";
                break;
            case ITEM_SCENE:
                desc = "类型:情景模式";
                break;
            case ITEM_ACTION:
                desc = "类型:执行动作";
                break;
            case ITEM_MORE_PARAM:
                desc = "类型:其他参数";
                break;
            default:
                desc = "未知类型";
                break;
        }

        return desc;
    }

    public String dumpInfo() {
        String str = String.format("StringItem keyStr=%s, len=%d", keyStr, keyLen);
        String ids = String.format("序列号=%d, handle=%d, sceneid=%d", devSn, devHandle, sceneId);

        SpeechLog.d(str);
        SpeechLog.d(getTypeDesc());
        SpeechLog.d(ids);

        StringBuffer sb = new StringBuffer(str);
        sb.append("\n" + getTypeDesc());
        sb.append("\n" + ids);
        return sb.toString();
    }
}
