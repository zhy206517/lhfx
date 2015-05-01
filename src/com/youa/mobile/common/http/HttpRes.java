package com.youa.mobile.common.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.youa.mobile.R;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.params.CommonParam;
import com.youa.mobile.common.util.ZlibUtil;
import com.youa.mobile.parser.JsonObject;
import com.youa.mobile.parser.JsonParser;

public class HttpRes {
	private static final long serialVersionUID = 8198071473792537310L;

	// public static final String OK = "mcphp.ok";
	// // public static final int FAILED = 1;
	// public static final String NOT_LOGIN = "mcphp.u_login";
	// public static final String NOT_EXIST = "mcphp.u_notfound";
	// public static final String PARAM_ERROR = "mcphp.u_input";
	//
	// // public static final int JSON_ERROR = 1000;
	// public static final String NETWORK_ERROR = "NETWORK_ERROR";

	// public static final int AUTH_FAILED = 10005;

	// public static final int PERMISSION_DENIED = 10006;
	// public static final int USED = 10007;
	// public static final int VERIFYCODE_ERROR = 10010;

	// public static final int ALREADY_ADDED_CONTACT = 10011;
	//
	// public static final int NO_VERIFICATION_EXIST = 10015;
	// public static final int WAIT_VERIFICATION = 10016;
	// public static final int OLD_VERSION = 10012;

	private String code;
	private JsonObject msg = null;

	public HttpRes() {
	}

	private HttpRes(String code, JsonObject msg) {
		this.code = code;
		this.msg = msg;
	}

	public static HttpRes create(String jsonString) throws MessageException {
		if (jsonString.startsWith("{")) {// json
//			jsonString = treateGzip(jsonString);
			JsonObject jsonObject;
			System.out.println("currentTime begin:" + System.currentTimeMillis());
//			jsonObject = (JsonObject) new JsonParser(jsonString).parse();
			jsonObject = (JsonObject) JsonParser.parse(jsonString);
			System.out.println("currentTime end:" + System.currentTimeMillis());
			String errorCode = jsonObject.getString(CommonParam.RESULT_KEY_ERR);
			if (!errorCode.equals(CommonParam.VALUE_ERROR_OK)
					&& !errorCode.equals(CommonParam.VALUE_ERROR_NOT_LOGIN)
					&& !errorCode.equals(CommonParam.VALUE_ERROR_SESSION_ERROR)) {
				if (errorCode.equals(CommonParam.VALUE_ERROR_NETWORK_ERROR)) {
					throw new MessageException(errorCode,
							R.string.common_network_error);
				} else {
					throw new MessageException(errorCode);
				}
			} else {
				// JsonObject dataObject =
				// jsonObject.getJsonObject(CommonParam.RESULT_KEY_DATA);
				return new HttpRes(errorCode, jsonObject);
			}
		} else {// exception
			throw new MessageException("", R.string.common_error_unknow);
		}
	}

	private static String treateGzip(String jsonString) {
		int index = jsonString.indexOf("rpcret");
		String str = jsonString.substring(index + 8, index + 12);
		if (index < 0 || ("null".equals(str))) {
			return jsonString;
		}
		index += 8;
		str = jsonString.substring(index);
		byte[] data = null;
		if (str != null) {
			try {
				data = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				data = str.getBytes();
			}
		}
		data = ZlibUtil.decompress(data);
		if (data != null) {
			try {
				str = new String(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
				str = new String(data);
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append(jsonString.substring(0, index));
		sb.append(str);
		return sb.toString();
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public String toJSONString() {
		return String.format("{code: %1$d, msg: %2$s}", code, msg.toString());
	}

	public JsonObject getJSONObject() {
		return msg;
	}

	public String getCode() {
		return code;
	}
}
