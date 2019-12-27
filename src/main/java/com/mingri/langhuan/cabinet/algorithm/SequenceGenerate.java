package com.mingri.langhuan.cabinet.algorithm;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.mingri.langhuan.cabinet.tool.DateTool;

/**
 * 序列号生成器：
 * 
 * 有序的、原子性，超性能， 并发量随便配置
 * 
 * 
 * @author ljl
 */
public class SequenceGenerate {

	/**
	 * 设置最高并发量/每秒
	 * 
	 * @param cps 最高并发量/每秒
	 */
	public void setCps(Integer cps) {
		if (cps != null) {
			MAX_NO = cps + 1;
		}
	}

	/**
	 * 每秒最高并发量
	 */
	private static int MAX_NO = 100001;

	/**
	 * 应用编号
	 */
	private static String APP_NO = "0";

	/**
	 * 开始序号
	 */
	private static byte START_NUM = 0;

	private static final Map<String, AtomicInteger> SEQUENCE_MAP = new HashMap<String, AtomicInteger>();

	/**
	 * 设置续接起始序号： 使用场景：序号默认是从1开始，累计到cps（默认10w）后，或者服务重启后，又从1开始累计，
	 * 若是累计到5w断了，重启服务需要从50001开始累计，则用此函数设置
	 * 
	 * @param sequenceKey 序列key
	 * @param startNum    开始序号
	 */
	public static void setStartNum(String sequenceKey, int startNum) {
		synchronized (SEQUENCE_MAP) {
			AtomicInteger sequence = SEQUENCE_MAP.get(sequenceKey);
			sequence.set(startNum - 1);
		}
	}

	/**
	 * 初始化配置
	 * 
	 * @param cps   默认值100000，默认每秒最高并发量，也是最大序号，自增到该序号后从1开始重新自增
	 * @param appNo 默认值0，应用编号
	 */
	public synchronized static void initConfiguraion(int cps, String appNo) {
		MAX_NO = cps + 1;
		APP_NO = appNo;
	}

	private static String getSecond() {
		return DateTool.yyMdHms_FMTS.format(LocalDateTime.now());
	}

	/**
	 * 不够序号的长度补0
	 * 
	 * @param srcNum 要补0的数字
	 * @param max    小于该数字要补0
	 */
	private static String fill0(int srcNum, int len) {
		StringBuilder strSrc = new StringBuilder();
		strSrc.append(srcNum);
		while (strSrc.length() < len - 1) {
			strSrc.insert(0, "0");
		}
		return strSrc.toString();
	}

	/**
	 * 
	 * @param sequenceKey 序列key，自定义一个key，每次获取都会在对应的key首次获取序列值的基础上递增
	 * @return 返回下一个序列号
	 */
	public static String nexId(String sequenceKey) {
		return nexId(sequenceKey, SequenceGenerate.MAX_NO);
	}

	/**
	 * 将序列号格式化成LocalDateTime对象
	 * 
	 * @param id 要格式化的序列号
	 * @return 返回格式化的LocalDateTime
	 */
	public static LocalDateTime formatIdToDateTiem(String id) {
		int year = Integer.parseInt("20" + id.substring(0, 2));
		int month = Integer.parseInt(id.substring(2, 4));
		int day = Integer.parseInt(id.substring(4, 6));
		int hour = Integer.parseInt(id.substring(6, 8));
		int minute = Integer.parseInt(id.substring(8, 10));
		int second = Integer.parseInt(id.substring(10, 12));

		LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, second);
		return ldt;
	}

	/**
	 * 
	 * @param sequenceKey 序列key，自定义一个key，每次获取都会在对应的key首次获取序列值的基础上递增
	 * @param cps         每秒最高并发量
	 * @return 返回下一个序列号，序列号小于并发量
	 */
	public static String nexId(String sequenceKey, int cps) {
		AtomicInteger sequence = SEQUENCE_MAP.get(sequenceKey);
		if (sequence == null) {
			synchronized (SEQUENCE_MAP) {
				sequence = SEQUENCE_MAP.get(sequenceKey);
				if (sequence == null) {
					sequence = new AtomicInteger(START_NUM);
					SEQUENCE_MAP.put(sequenceKey, sequence);
				}
			}
		}
		int no = sequence.incrementAndGet();
		String strNo = null;
		int len = (cps + "").length();
		if (no < cps) {
			strNo = SequenceGenerate.fill0(no, len);
		} else {
			synchronized (SEQUENCE_MAP) {
				no = sequence.incrementAndGet();
				if (no < cps) {
					strNo = SequenceGenerate.fill0(no, len);
				} else {
					sequence.set(START_NUM);
					no = sequence.incrementAndGet();
					strNo = SequenceGenerate.fill0(no, len);
				}
			}
		}
		return SequenceGenerate.getSecond().concat(strNo).concat(APP_NO);
	}
}
