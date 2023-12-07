package cn.watchdog.sdk.epay;

import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EPayFactory {
	@Getter
	private static Config config = null;

	public static void setOptions(Config config) {
		EPayFactory.config = config;
	}

	// Function to build the URL with parameters
	private static String buildUrl(String baseUrl, Map<String, String> params) {
		Map<String, String> sortedParam = new TreeMap<>(params);
		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("?");
		for (Map.Entry<String, String> entry : sortedParam.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			urlBuilder.append(key).append("=").append(value).append("&");
		}
		// Remove the trailing '&' character
		urlBuilder.deleteCharAt(urlBuilder.length() - 1);
		return urlBuilder.toString();
	}

	private static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : messageDigest) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Payment {
		@NotNull
		private String getSign(Map<String, String> param) {
			Map<String, String> sortedParam = new TreeMap<>(param);

			StringBuilder signatureBuilder = new StringBuilder();

			for (Map.Entry<String, String> entry : sortedParam.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();

				if (v == null) {
					continue;
				}

				if (!k.equalsIgnoreCase("sign") && !k.equalsIgnoreCase("sign_type") && !v.isEmpty()) {
					signatureBuilder.append(k).append('=').append(v).append('&');
				}
			}

			String signstr = signatureBuilder.toString();
			signstr = signstr.substring(0, signstr.length() - 1);
			System.out.println(signstr);
			signstr += config.getKey();
			System.out.println(signstr);

			return getMD5(signstr);
		}

		@NotNull
		public PaymentInformation getPaymentInformation(String no, String name, String money) {
			// Prepare the request parameters
			Map<String, String> requestParams = new HashMap<>();
			requestParams.put("pid", config.getPid());
			requestParams.put("out_trade_no", no);
			requestParams.put("notify_url", config.getNotifyUrl());
			requestParams.put("return_url", config.getReturnUrl());
			requestParams.put("name", name);
			requestParams.put("money", money);
			requestParams.put("sign_type", "MD5");
			String sign = getSign(requestParams);
			requestParams.put("sign", sign);
			String baseUrl = config.getUrl() + "submit.php";
			PaymentInformation paymentInformation = new PaymentInformation();
			paymentInformation.setUrl(buildUrl(baseUrl, requestParams));
			paymentInformation.setParams(requestParams);
			return paymentInformation;
		}

		@NotNull
		private Map<String, String> getNotifySign(EPayResultRequest ePayResultRequest, String out_trade_no, String type) {
			Map<String, String> requestParams = new HashMap<>();
			requestParams.put("pid", config.getPid());
			requestParams.put("trade_no", ePayResultRequest.getTrade_no());
			requestParams.put("out_trade_no", out_trade_no);
			requestParams.put("type", type);
			requestParams.put("name", ePayResultRequest.getName());
			requestParams.put("money", ePayResultRequest.getMoney());
			requestParams.put("trade_status", "TRADE_SUCCESS");
			requestParams.put("param", ePayResultRequest.getParam());
			requestParams.put("sign_type", "MD5");
			return requestParams;
		}

		public boolean checkNotifySign(EPayResultRequest ePayResultRequest, String out_trade_no, String type) {
			Map<String, String> requestParams = getNotifySign(ePayResultRequest, out_trade_no, type);
			String sign = getSign(requestParams);
			return sign.equals(ePayResultRequest.getSign());
		}
	}

	@Data
	public static class PaymentInformation {
		private String url;
		private Map<String, String> params;
	}
}
