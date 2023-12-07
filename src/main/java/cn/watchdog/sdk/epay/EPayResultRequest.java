package cn.watchdog.sdk.epay;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EPayResultRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 商户ID
	 */
	private Integer pid;

	/**
	 * 易支付订单号
	 */
	private String trade_no;

	/**
	 * 商户订单号
	 */
	private String out_trade_no;

	/**
	 * 支付方式
	 */
	private String type;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 商品金额
	 */
	private String money;

	/**
	 * 支付状态
	 */
	private String trade_status;

	/**
	 * 业务扩展参数
	 */
	private String param;

	/**
	 * 签名字符串
	 */
	private String sign;

	/**
	 * 签名类型
	 */
	private String sign_type;
}
