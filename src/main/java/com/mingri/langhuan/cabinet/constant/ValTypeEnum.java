package com.mingri.langhuan.cabinet.constant;

/**
 * 值类型
 * @author  ljl
 */
public enum ValTypeEnum {
	/**
	 * 长度为0的字符串对象
	 *  <br>
	 */
	BLANK(""),
	/**
	 * null对象
	 *  <br>
	 */
	NULL("null"),
	/**
	 * 有意义的对象，非BLANK，NULL
	 *  <br>
	 */
	OBJ("obj"),
	/**
	 * 所有对象，包含BLANK，NULL，OBJ
	 *  <br>
	 */
	ALL("all");

	public final String code;

	ValTypeEnum(String code) {
		this.code = code;
	}

}
