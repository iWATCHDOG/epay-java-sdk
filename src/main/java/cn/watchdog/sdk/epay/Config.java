package cn.watchdog.sdk.epay;

import lombok.Data;

@Data
public class Config {
	/**
	 * 电子支付平台的URL
	 */
	private String url;

	/**
	 * 电子支付平台的商户ID
	 */
	private String pid;

	/**
	 * 电子支付平台的商户密钥
	 */
	private String key;

	/**
	 * 电子支付平台的通知URL
	 */
	private String notifyUrl;

	/**
	 * 电子支付平台的返回URL
	 */
	private String returnUrl;
}
