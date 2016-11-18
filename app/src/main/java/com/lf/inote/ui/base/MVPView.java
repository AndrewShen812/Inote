/**
 * Project name：Inote
 * Create time：2016/11/17 18:37
 * Copyright: 2016 GALAXYWIND Network Systems Co.,Ltd.All rights reserved.
 */
package com.lf.inote.ui.base;

/**
 * Created by sy on 2016/11/17.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2016/11/17 18:37<br>
 * Revise Record:<br>
 * 2016/11/17: 创建并完成初始实现<br>
 */
public interface MVPView<T> {

	void setPresenter(T presenter);
}
