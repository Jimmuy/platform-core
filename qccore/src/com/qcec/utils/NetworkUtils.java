package com.qcec.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {


	public static final int OPERATOR_CHINA_MOBILE  = 1;
	public static final int OPERATOR_CHINA_UNICOM  = 2;
	public static final int OPERATOR_CHINA_TELECOM = 3;
	public static final int OPERATOR_CHINA_NONE    = -1;

	public static final int NETWORK_TYPE_WIFI        = -101;
	public static final int NETWORK_TYPE_UNAVAILABLE = -1;

	public static final int NETWORK_CLASS_WIFI        = -101;
	public static final int NETWORK_CLASS_UNAVAILABLE = -1;
	public static final int NETWORK_CLASS_UNKNOWN     = 0;
	/**
	 * Class of broadly defined "2G" networks.
	 */
	public static final int NETWORK_CLASS_2_G         = 1;
	/**
	 * Class of broadly defined "3G" networks.
	 */
	public static final int NETWORK_CLASS_3_G         = 2;
	/**
	 * Class of broadly defined "4G" networks.
	 */
	public static final int NETWORK_CLASS_4_G         = 3;

	/**
	 * 新发现的两个3G类型
	 */
	public static final int              TD_SCDMA         = 17;
	public static final int              TDS_HSDPA        = 18;
	private static TelephonyManager telephonyManager = null;

	public static String getOperatorMark(Context context) {

		checkTelephonyManager(context);

		return telephonyManager.getSimOperator();

	}

	/**
	 * 获取运营商，双卡模式下当前只能获得主卡信息
	 * 1为移动，2为联通，3为电信
	 *
	 * @param context
	 * @return
	 */
	public static int getOperator(Context context) {

		checkTelephonyManager(context);

		int operatorType = -1;
		String operator = telephonyManager.getSimOperator();
		if (operator == null || operator.equals("")) {
			operator = telephonyManager.getSubscriberId();
		}
		if (operator == null || operator.equals("")) {
			operatorType = OPERATOR_CHINA_NONE;
		} else if (operator != null) {
			if (operator.startsWith("46000")
					|| operator.startsWith("46002")
					|| operator.startsWith("46007")) {
				operatorType = OPERATOR_CHINA_MOBILE;
			} else if (operator.startsWith("46001") || operator.startsWith("46006")) {
				operatorType = OPERATOR_CHINA_UNICOM;
			} else if (operator.startsWith("46003") || operator.startsWith("46005")) {
				operatorType = OPERATOR_CHINA_TELECOM;
			}
		}
		return operatorType;
	}

	private static void checkTelephonyManager(Context context) {
		if (null == telephonyManager) {
			telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
	}

	/**
	 * 获取网络类型
	 *
	 * @return
	 */
	public static String getCurrentNetworkType(Context context) {
		int networkClass = getNetworkClass(context);
		String type = "未知";
		switch (networkClass) {
			case NETWORK_CLASS_UNAVAILABLE:
				type = "无";
				break;
			case NETWORK_CLASS_WIFI:
				type = "Wi-Fi";
				break;
			case NETWORK_CLASS_2_G:
				type = "2G";
				break;
			case NETWORK_CLASS_3_G:
				type = "3G";
				break;
			case NETWORK_CLASS_4_G:
				type = "4G";
				break;
			case NETWORK_CLASS_UNKNOWN:
				type = "未知";
				break;
		}
		return type;
	}

	/**
	 * 获取当前网络状态标识
	 * -1为无法识别，-101为wifi，1为2G，2为3G，3为4G
	 *
	 * @param context
	 * @return
	 */

	public static int getNetworkClass(Context context) {
		int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
		try {
			final NetworkInfo network = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					checkTelephonyManager(context);
					networkType = telephonyManager.getNetworkType();
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getNetworkClassByType(networkType);

	}

	private static int getNetworkClassByType(int networkType) {
		switch (networkType) {
			case NETWORK_TYPE_UNAVAILABLE:
				return NETWORK_CLASS_UNAVAILABLE;
			case NETWORK_TYPE_WIFI:
				return NETWORK_CLASS_WIFI;
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return NETWORK_CLASS_2_G;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
			case TD_SCDMA:
			case TDS_HSDPA:
				return NETWORK_CLASS_3_G;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return NETWORK_CLASS_4_G;
			default:
				return NETWORK_CLASS_UNKNOWN;
		}
	}


}
