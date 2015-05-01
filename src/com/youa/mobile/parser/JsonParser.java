package com.youa.mobile.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.util.Log;

import com.youa.mobile.utils.NetTools;
public class JsonParser {
	public static final String TAG = "JsonParser";
	
	private int curReadIndex = -1;
	private int len;//需要初期化
	private String alltext;
//	private String curName;
	private int curCh;
	
	public JsonParser(String s) {
		
		alltext = s;
		curReadIndex = -1;
		len = s.length();

	}
	
	/**
	 * Parse a String to a JsonValue.
	 * 
	 * @param s
	 *            a String
	 * @return a JsonValue
	 */
	public JsonValue parse() {
		nextChar();
		return parseValueByCh("null", curCh);
//		return JsonValue.parseValue(alltext);
	}
	
	public static JsonValue parse(String s) {
		return JsonValue.parseValue(s);
	}

	public static void serialize(JsonValue obj, DataOutputStream dos)
			throws IOException {
		JsonValue.writeObject(obj, dos);
	}

	/**
	 * 序列化对�?
	 * 
	 * @param obj
	 *            被序列化的对�?
	 * @param gzip
	 *            进行gzip压缩
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(JsonValue obj, boolean gzip)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = null;
		if (gzip) {
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			dos = new DataOutputStream(gos);
		} else {
			dos = new DataOutputStream(baos);
		}
		serialize(obj, dos);
		dos.close();
		return baos.toByteArray();
	}

	public static JsonValue deserialize(DataInputStream dis) throws IOException {
		JsonValue obj = JsonValue.readObject(dis);
		try {
			while (dis.read() != -1)
				;
		} catch (IOException e) {
		}
		return obj;
	}

	/**
	 * 反序列化对象
	 * 
	 * @param bytes
	 *            数据
	 * @param gzip
	 *            bytes是否被gzip压缩�?
	 * @return 对象
	 * @throws IOException
	 */
	public static JsonValue deserialize(byte[] bytes, boolean gzip)
			throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = null;
		if (gzip) {
			GZIPInputStream gis = new GZIPInputStream(bais);
			dis = new DataInputStream(gis);
		} else {
			dis = new DataInputStream(bais);
		}
		JsonValue obj = deserialize(dis);
		dis.close();
		return obj;
	}

	/**
	 * 从数据流中读取对�?
	 * 
	 * @param is
	 *            输入�?
	 * @param gzip
	 *            是否使用gzip压缩
	 * @param contentLength
	 *            已知的数据长度，未知则传�?=0的数
	 * @return 对象
	 * @throws IOException
	 */
	public static JsonValue deserialize(InputStream is, boolean gzip,
			int contentLength) throws IOException {
		byte[] buf = null;
		try {
			buf = NetTools.toByteArray(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if (contentLength <= 0) {
		// contentLength = 1024;
		// // 读取数据
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// buf = new byte[contentLength];
		// while (true) {
		// int len = is.read(buf);
		// if (len == -1) {
		// break;
		// }
		// baos.write(buf, 0, len);
		// }
		// buf = baos.toByteArray();
		// } else {
		// buf = new byte[contentLength];
		// is.read(buf);
		// }
		// is.close();
		JsonValue obj = deserialize(buf, gzip);
		return obj;
	}
	
	private boolean isReadEnd() {
		return curReadIndex >= len;
	}
	
	private int nextChar() { 
		if(++curReadIndex < len) {
			curCh = alltext.charAt(curReadIndex);
		   return curCh;
		} else {
			return -1;
		}
	}
	private JsonValue parseValueByCh(String name, int ch) {
		
//		System.out.println("begin-"+(char)ch+":" + System.currentTimeMillis());
		
		
//		int firstChar;
//		boolean isHasMore = false;
//		while ((firstChar = nextChar()) != -1) {
//			char ch = str.charAt(curReadIndex);
//			if(firstChar == -1) {
//				firstChar = ch;
//			}
//		    System.out.println(curName+"-ch=" + (char)ch);
		    JsonValue object;
			if (ch == '{') {
//				long start = System.currentTimeMillis();
				object = parseObject(name);
//				System.out.println(name +"-parseObject:" + (System.currentTimeMillis() - start));
			} else if (ch == '[') {
				object = parseArray(name); 
			} else if (ch == '"') {

				object = parseString(name);
				
			} else if (isNum(ch)) {// 解析数字0到9
				curReadIndex--;
				object = parseNumber();
			} else if (ch == 't' || ch == 'T' || ch == 'f' || ch == 'F') {//解析boolean�?
				curReadIndex--;
				object = parseBool();
			} else if(isTextEnd(ch)) {				
				object = new JsonNull();
			} else if(ch == 'n' || ch == 'N') {//null Null
				readSampleText();
				object = new JsonNull();
			} else {
				throw new IllegalArgumentException("charch:" + ch);
			}
//			if(object instanceof JsonString
//					|| object instanceof JsonNum) {
//				System.out.println("parseValueByCh:" + name + "=" + object);
//			} else {
//				System.out.println("parseValueByCh:" + name + "=" + object.getClass().getName());
//			}
			
			return object;
//		}
//		return true;
	}
	protected JsonBool parseBool() {
		String s = readSampleText();
		s = s.toLowerCase();
		if (s.equals("true")) {
			return new JsonBool(true);
		} else if (s.equals("false")) {
			return new JsonBool(false);
		} else {
			System.err.println("Boolean cast error!\n" + s);
		}
		return null;
	}
	
	private boolean isNum(int ch) {
		return (ch > 47 && ch < 58) || ch == '-';
	}
	
	private boolean isTextEnd(int ch) {
		return ch == ',' || ch == ']' || ch == '}';
	}
	private boolean isReadContinue(int ch) {
		return ch == ',' || ch == ']' || ch == '}';
	}
	
	private String readSampleText() {
		int ch;
//		StringBuffer strBuf = new StringBuffer();
		int start = curReadIndex;
		int end = start;
		while (true) {
			ch = nextChar();
//			System.out.println("ch:" + ch);
			if(!isTextEnd(ch)) {
				end = curReadIndex;
			} else {
				break;
			}
		}
		return alltext.substring(start, end);
	}
	private String readKey() {
//		long start = System.currentTimeMillis();
		if(isReadEnd()) {
			return null;
		}
		int ch;
		boolean isHasFirst = false;
		boolean isHasLast = false;
//		long time2 = System.currentTimeMillis();
//		System.out.println("-readKey1:" + (time2 - start));
//		StringBuffer strBuf = new StringBuffer(20);
		int start = -1;
		int end = -1;
		while ((ch = nextChar()) != -1) {
//			System.out.println((char)ch);
			if(ch == ',') {
				continue;
			} else if(ch == '"') {
				if(!isHasFirst) {
					isHasFirst = true;
					start = curReadIndex + 1;
				} else {
					end = curReadIndex;
					isHasLast = true;
					break;
				}
			} else if(isTextEnd(ch)) {
				return null;
			} else {
//				strBuf.append((char)ch);
			}
		}
		String result = alltext.substring(start, end);
//		long time3 = System.currentTimeMillis();
//		System.out.println("-readKey2:" + (time3- start));
//		String result = strBuf.toString();
//		System.out.println(result + "-readKey3:" + (System.currentTimeMillis() - time3));
		return result;
	}
	
	private JsonNum parseNumber() {
		String value = readSampleText();
		return JsonNum.parseNum(value);
	}
	

	/**
	 * Parse a String to a JsonString.
	 * 
	 * @param s
	 *            a String
	 * @return a JsonString
	 */
	protected JsonString parseString(String name) {
		String s = alltext;
		StringBuffer sb = null;
		if("content".equals(name)){//如果是內容的話
			sb = new StringBuffer(140*5);//初始化140個字的大小
		} 
		
//		int idx = 0;
//		final int len = s.length();
		int start = curReadIndex + 1;
		int end = start;
		int ch = 0;
		while (true) {
			ch = nextChar();
			if (ch == '\\' && (curReadIndex + 1) < len) {
				if(sb == null) {//速度优化
				    end = curReadIndex;
				    sb = new StringBuffer(40);
				    if(start != end) {
				    	String text = alltext.substring(start, end);
				    	sb.append(text);
				    }
				}
				ch = nextChar();
				switch (ch) {
					case '"':
						sb.append('\"');
						break;
					case '\\':
						sb.append('\\');
						break;
					case '/':
						sb.append('/');
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'u':
						if (curReadIndex + 5 <= len) {
							int c = Integer.parseInt(s.substring(curReadIndex + 1, curReadIndex + 5), 16);
							sb.append((char) c);
							curReadIndex += 4;
						}
						break;
					case 'x':
						if (curReadIndex + 3 <= len) {
							int c = Integer.parseInt(s.substring(curReadIndex + 1, curReadIndex + 3), 16);
							sb.append((char) c);
							curReadIndex += 2;
						}
						break;
					default:
						sb.append((char)ch);
				}
			} else if(ch == '"'){
				end = curReadIndex;
				break;
			} else {
				if(sb != null) {
				   sb.append((char)ch);
				}
			}
		}
		
		String text;
		if(sb == null) {//速度优化
			text = alltext.substring(start, end);
		} else {
			text = sb.toString();
		}
//		System.out.println("text-name:" + text);
		JsonString ret = new JsonString(text);
//		System.out.println()
//		ret.value = sb.toString();
		return ret;
//		return null;
	}

	
	protected JsonArray parseArray(String name) {
//		if (s == null) {
//			return null;
//		}
		JsonArray ret = new JsonArray();
//		if (s.equals("")) {
//			return ret;
//		}
//		final int len = s.length();
//		int sIdx = start-1, eIdx;
//		int ch;
//		int len;
		nextChar();
	    while (true){
            if(curCh == ','
            	|| curCh == '}') {
            	nextChar();
            	continue;
            } else if(curCh == ']') {
            	nextChar();
            	break;
            }
			JsonValue jsonValue = parseValueByCh(name, curCh);
			ret.add(jsonValue);
		}
		return ret;
	}
	
//	/**
//	 * Parse a String to a JsonValue.
//	 * @param s a String
//	 * @return a JsonValue
//	 */
//	protected static JsonValue parseValue(String s, char firstChar, int start) {
//		if (s == null || s.equals("null")) {
//			return new JsonNull();
//		}
////		final int len = end - start;
//		char curChar = firstChar;
////		int sIdx = start, eIdx;
////		while (++sIdx < len && (curChar = s.charAt(sIdx)) < 33);
////		sIdx++;
//		if (curChar == '"') {//解析字符
////			eIdx = s.lastIndexOf('"', end);
////			System.out.println("JsonValue-s:" + s);
////			System.out.println("sIdx:" + sIdx);
////			System.out.println("eIdx:" + eIdx);
////			if (eIdx > -1) {
//				String str = s.substring(start+1, end - 1);
//				return JsonString.parseString(str);
////			} else {
////				System.err.println("'\"' is expected to close a string!");
////			}
//		} else if ((curChar > 47 && curChar < 58) || curChar == '-') {// 解析数字0到9
//			String str = s.substring(start, end);
//			return JsonNum.parseNum(str);
//		} else if (curChar == '{') {//解析对象
////			eIdx = s.lastIndexOf('}');
////			if (eIdx > -1) {
////				String str = s.substring(sIdx, eIdx);
//				return JsonObject.parseObject(s, start + 1);
////			} else {
////				System.err.println("'}' is expected to close a JsonObject!");
////			}
//		} else if (curChar == '[') {//解析数组
////			eIdx = s.lastIndexOf(']');
////			if (eIdx > -1) {
//				String str = s.substring(start + 1, end -1);
////				System.out.println("array str:" + str);
//				return JsonArray.parseArray(str);
////			} else {
////				System.err.println("']' is expected to close a JsonArray!");
////			}
//		} else if (curChar == 't' || curChar == 'T' || curChar == 'f' || curChar == 'F') {//解析boolean�?
//			return JsonBool.parseBool(s);
//		}
//		return null;
//	}
	
	/**
	 * Parse a String to a JsonObject.
	 * 
	 * @param s
	 *            a String
	 * @return a JsonObject
	 */
	protected JsonObject parseObject(String name) {
//		long start = System.currentTimeMillis();
		JsonObject ret = new JsonObject();
//		final int strEnd = s.length();
//		char curChar = 0;
//		int sIdx = start-1, eIdx;
//		System.out.println("s:" + s);
//		System.out.println(">>sIdx:" + sIdx);
//		System.out.println("strEnd:" + strEnd);
//		nextChar();
//		Log.w(TAG, ">>>>>>>>>>>>parseObject start:" + name);
//		long time3 = start;
		while (true) {
//			long start1 = System.currentTimeMillis();
			if(curCh == ']'){
				nextChar();
				continue;
			} else if(curCh == ',') {
				;
			} else if(curCh == '}') {
				nextChar();
//				Log.w(TAG, ">>>>>>>>>>>>parseObject end:" + name);
				break;
			}
//			long time1 = System.currentTimeMillis();
//			System.out.println(name +"-time1:" + (time1 - start1));
			String subname = readKey();
//			Log.w(TAG, "subname:" + subname);
			if(subname == null) {
				nextChar();
//				Log.w(TAG, ">>>>>>>>>>>>parseObject end:" + name);
				break;
			}
//			long time2 = System.currentTimeMillis();
//			System.out.println(name +"-time2:" + (time2 - time1));
//			curName = name;			
			nextChar();
//			Log.w(TAG, "curName:" + subname + "       curCh:" + (char)curCh);
			JsonValue jsonValue;
			if(curCh == ':') {
				int tempch = nextChar();
				jsonValue = parseValueByCh(subname, tempch);
			} else {
				Log.w(TAG, "illegal jsonObject-name:" + subname + "       curCh:" + (char)curCh);
				jsonValue = new JsonNull();
			}
//			time3 = System.currentTimeMillis();
//			System.out.println(name +"-time3:" + (time3 - time2));
			ret.put(subname, jsonValue);
//			Log.w(TAG, "<<<<<<<<<<<<<<<<<<,,");
		}
//		System.out.println("end-"+name+":" + (System.currentTimeMillis() - time3));
		return ret;
	}

}
