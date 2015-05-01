package com.youa.mobile.parser;

import java.net.URLEncoder;

//减号和下划线
public class ParserContent {
	final private boolean isDebug = false;
	final private static int Start = 1;

	final private static int End = 2;

	final private static int Text = 3;

	final private static int No = 4;

	final private static int Over = 5;
	private int iType = No;
	private static ParserContent parser;
	private static boolean isComplete;
	private char[] buf;

	private int bufPos, bufCount;

	private ContentData[] dataArray;
	private int index;
	final private int LENGTH = 10;
	private boolean isBr;

	private void addArray(String str, String href, int type) {
		if (dataArray == null) {
			dataArray = new ContentData[LENGTH];
		} else if (index >= dataArray.length) {
			ContentData[] temp = dataArray;
			dataArray = new ContentData[index + LENGTH];
			System.arraycopy(temp, 0, dataArray, 0, temp.length);
			temp = null;
		}
		ContentData data = new ContentData();
		data.type = type;
		StringBuffer sb = null;
		if (isBr && index > 0) {
			sb = new StringBuffer();
			sb.append('\n');
			sb.append(str);
			str = sb.toString();
			isBr = false;
			sb = null;
		}
		data.str = str;
		data.href = href;
		dataArray[index++] = data;
	}

	private void trimArray() {
		if (isDebug) {
			System.out.println("trim 开始 dataArray length:" + dataArray.length
					+ "//" + Thread.currentThread());
		}
		if (dataArray == null || index >= dataArray.length) {
			return;
		}
		ContentData[] temp = new ContentData[index];
		System.arraycopy(dataArray, 0, temp, 0, index);
		dataArray = temp;
		temp = null;
		if (isDebug) {
			System.out.println("trim 结束:" + dataArray.length + "//"
					+ Thread.currentThread());
		}
	}

	public static ParserContent getParser() {
		if (parser == null) {
			parser = new ParserContent();
		}
		return parser;
	}

	Object synObj = new Object();
	
	public ContentData[] parser(char[] data) {
		while (isComplete) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
//		if(isComplete) {
//			synchronized(synObj) {
//				try {
//					synObj.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		isComplete = true;
		dataArray = null;
		if (isDebug) {
			System.out.println("parser 开始 :" + dataArray + "//"
					+ Thread.currentThread());
		}
		iType = No;
		index = 0;
		buf = data;
		bufCount = buf.length;
		bufPos = 0;
		skipWhitespace();
		while (iType != Over) {
			checkType(true);
			if (iType == Text) {
				addArray(readTo('<', false), null, ContentData.TYPE_TEXT);
			} else if (iType == Start) {
				parserElement(true);
			}
		}
		if (isDebug) {
			System.out.println("parser 结束 :" + dataArray + "//"
					+ Thread.currentThread());
		}
		trimArray();
		isComplete = false;
//		synchronized(synObj) {
//			synObj.notify();
//		}
		return dataArray;
	}

	private void parserElement(boolean isHead) {
		switch (peekChar(true)) {
		case 'a':// a
			++bufPos;
			if (isWhitespace(peekChar(false)) || peekChar(false) == '>') {
				parserA();
				return;
			}
			break;
		case 'b':
			if (peekChar(1, true) == 'r') {
				parserBr();
				return;
			}
			break;
		case 'i':// img
			++bufPos;
			if (peekChar(true) == 'm' && peekChar(1, true) == 'g') {
				skipNumsChar(1);
				parserImg(null);
				return;
			}
			break;
		case 's':// span
			++bufPos;
			if (peekChar(true) == 'p' && peekChar(1, true) == 'a'
					&& peekChar(2, true) == 'n') {
				skipNumsChar(2);
				parserSpan(true);
			}
			break;
		default:
			break;
		}
		skipTo('>');
		++bufPos;// >后
	}

	private void parserSpan(boolean isSpan) {//
		int status = parserTagHeader();
		if (status < 0) {
			return;
		}
		while (peekChar(false) != -1) {
			if (status <= 0) {
				break;
			}
			switch (peekChar(true)) {
			case '>':
				++bufPos;
				status = 0;
				break;
			case '/':
				++bufPos;// /
				skipWhitespace();
				++bufPos;// >
				return;
			default:
				skipAttriValue();
			}
		}
		addArray(readTo('<', false), null, ContentData.TYPE_TEXT);
	}

	private void parserBr() {
		skipTo('>');
		bufPos++;
		isBr = true;
	}

	private void parserA() {
		int status = parserTagHeader();
		if (status < 0) {
			return;
		}
		String href = null;
		while (peekChar(false) != -1) {
			if (status <= 0) {
				break;
			}
			switch (peekChar(true)) {
			case '>':
				++bufPos;
				status = 0;
				break;
			case '/':
				++bufPos;// /
				skipWhitespace();
				++bufPos;// >
				return;
			case 'h':
				skipNumsChar(4);
				href = readAttriValue();
				if (href == null || "".equals(href.trim())) {
					break;
				}
				break;
			default:
				skipAttriValue();
			}
		}
		// ----------------------------------------------
		while (iType != Over) {
			checkType(true);
			if (iType == Text) {
				checkTextType(readTo('<', false), href);
			} else if (iType == Start) {
				if (peekChar(true) == 'i') {
					skipNumsChar(3);
					parserImg(href);
					break;
				} else {
					backTo('<');
					break;
				}
			} else if (iType == End) {
				break;
			}
		}
	}

	private void checkTextType(String str, String href) {
		int type = ContentData.TYPE_TEXT;
		if (str.startsWith("@")) {
			type = ContentData.TYPE_AT;
			int end = href.lastIndexOf('/', href.length() - 1);
			href = href.substring(end + 1, href.length());
		} else if (str.startsWith("#") && str.endsWith("#")) {
			type = ContentData.TYPE_TOPIC;
		} else {
			type = ContentData.TYPE_LINK;
			// href = URLEncoder.encode(href);
		}
		if (href != null && "75fb2a357d38397d5e1e75fb".equals(href)) {
			type = ContentData.TYPE_TEXT;
		}
		addArray(str, href, type);
	}

	private void parserImg(String url) {
		int status = parserTagHeader();
		if (status <= 0) {
			return;
		}
		String src = null;
		String alt = null;
		while (peekChar(false) != -1) {
			if (status <= 0) {
				break;
			}
			switch (peekChar(true)) {
			case '>':
				++bufPos;
				status = 0;
				break;
			case '/':
				++bufPos;// /
				skipWhitespace();
				++bufPos;// >
				status = 0;
				break;
			case 's':
				src = URLEncoder.encode(readAttriValue());
				break;
			case 'a':
				alt = readAttriValue();
				break;
			default:
				skipAttriValue();
			}
		}
		// ----------------------------------------------
		addArray(alt, null, ContentData.TYPE_EMOTION);
	}

	private String readAttriValue() {
		String strtemp = null;
		skipTo('=');
		++bufPos;//
		skipWhitespace();
		if (peekChar(false) == '\"') {
			++bufPos;// 跳过前"
			strtemp = readTo('\"', true);
		} else if (peekChar(false) == '\'') {
			++bufPos;
			strtemp = readTo('\'', true);
		} else {
			int i = 0;
			int Char = -1;
			while (bufPos + i < bufCount) {
				if (peekChar(i, false) == '>') {
					Char = '>';
					break;
				} else if (peekChar(i, false) == '/'
						&& peekChar(i + 1, false) == '>') {
					Char = '>';
					break;
				} else if (peekChar(i, false) == '<') {
					Char = '<';
					break;
				} else if (isWhitespace(peekChar(i, false))) {
					Char = peekChar(i, false);
					break;
				}
				++i;
			}
			if (Char != -1) {
				strtemp = readTo(Char, true);
			}
			if (strtemp != null && strtemp.charAt(strtemp.length() - 1) == '/') {
				strtemp = strtemp.substring(0, strtemp.length() - 1);
			}
		}
		if (peekChar(false) != '<' && peekChar(false) != '>') {
			++bufPos;
		}
		skipWhitespace();// 跳到下一个非空格,如<js onload="module?true"/>，则跳到/上
		return strtemp;
	}

	private String readTo(int c, boolean isUrl) {
		skipWhitespace();
		int n = 0;
		for (int i = bufPos; i < bufCount; i++) {
			if (buf[i] == c) {
				break;
			}
			++n;
		}
		if (n == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer(n > 0 ? n : 32);
		int i = peekChar(false);
		int specialEnd = -1;
		while (i != c && i >= 0) {
			if (i == '&') {
				++bufPos;
				int type = 0;
				if (peekChar(false) == '#') {
					++bufPos;
					type = -1;
					if (peekChar(true) == 'x') {
						++bufPos;
						type = 1;
					}
				}
				checkEntity(type, c, sb);
				specialEnd = sb.length() - 1;
			} else {
				if (!isUrl && (i < 32 || i == 127)) {
					++bufPos;
					i = peekChar(false);
					continue;
				}
				sb.append((char) i);
				++bufPos;
			}
			i = peekChar(false);
		}
		for (int j = sb.length() - 1; j > specialEnd; j--) {
			int a = sb.charAt(j) & 0xffff;
			if (a > 32 && a != 127) {
				break;
			}
			sb.deleteCharAt(j);
		}
		return sb.toString();
	}

	private void checkEntity(int type, int c, StringBuffer sb) {// 解码
		int i = 0;
		while (peekChar(false) >= 0 && peekChar(false) != c
				&& peekChar(false) != '=' && peekChar(false) != ';') {
			++i;
			++bufPos;
		}
		String arg = null;
		switch (type) {
		case 0:
			arg = "&";
			break;
		case -1:
			arg = "&#";
			break;
		case 1:
			arg = "&#x";
			break;
		}
		if (i < 1) {
			sb.append(arg);
			return;
		}
		String s = new String(buf, bufPos - i, i);
		if (peekChar(false) != ';') {
			sb.append(arg);
			sb.append(s);
			return;
		}
		++bufPos;
		// -------------------
		if (type != 0) {
			try {
				int j = type > 0 ? Integer.parseInt(s, 16) : Integer
						.parseInt(s);
				sb.append((char) j);
			} catch (Exception e) {
				sb.append(arg);
				sb.append(s);
				sb.append(";");
			}
			return;
		}
		// 1.;
		// equal
		// error--->stringbuffer.
		// --------------------
		if (s.startsWith("am")) {// complete
			sb.append("&");
		} else if (s.startsWith("ap")) {
			sb.append("\'");
		} else if (s.startsWith("n") || s.startsWith("e")) {// nbsp; ensp;emsp;
			// if (isFir) {
			// tempn++;
			// }
			sb.append(" ");
		} else if (s.startsWith("q")) {// quo
			sb.append("\"");
		} else if (s.startsWith("g") || s.startsWith("rs")) {// rsaquo gt
			sb.append(">");
		} else if (s.startsWith("la")) {
			sb.append(">>");// laquo
		} else if (s.startsWith("lt") || s.startsWith("ls")) {// lt lsaquo
			sb.append("<");
		} else if (s.startsWith("ra")) {// raquo
			sb.append(">>");
		} else if (s.startsWith("ld")) {// ldquo;
			sb.append("“");
		} else if (s.startsWith("rd")) {// rdquo；
			sb.append("”");
		} else if (s.startsWith("re")) {// reg
			sb.append("Ｒ");
		} else if (s.startsWith("md")) {
			sb.append("—");
		} else if (s.startsWith("c")) {// copy
			sb.append("Ｃ");
		} else if (s.startsWith("t")) {// trade;
			sb.append("TM");
		} else {
			sb.append(arg);
			sb.append(s);
		}
	}

	private void checkType(boolean skipEnd) {
		do {
			skipWhitespace();
			switch (peekChar(false)) {
			case 60:// <
				++bufPos;//
				skipWhitespace();//
				switch (peekChar(false)) {//
				case -1:
					iType = Over;
					return;
				case 33: // '!'
					++bufPos;// !
					switch (peekChar(false)) {
					case 45: // '-'
						parserComment();
						iType = No;
						return;
					case 91: // '['
						parserCData();
						iType = No;
						return;
					}
					parserDoctype();
					iType = No;
					return;
				case 63: // '?'
					++bufPos;
					parserPI();
					iType = No;
					return;
				case 47: // '/'
					++bufPos;
					skipWhitespace();
					if (skipEnd) {
						skipEnd();
					}
					iType = End;
					return;
				}
				iType = Start;
				break;
			case -1:
				iType = Over;
				break;
			default:
				iType = Text;
			}
		} while (iType == No);
	}

	private int parserTagHeader() {// 解析开始标签名
		skipName();// 走完名字
		skipWhitespace();
		int i = peekChar(false);
		if (i == -1) {
			return -1;
		} else if (i == '/') {// 错误
			++bufPos;// /
			skipWhitespace();
			++bufPos;// >
			return -1;
		} else if (i == '>') {
			++bufPos;// >后
			return 0;
		}
		return 1;// 有标签名及其属性
	}

	private void skipAttriValue() {
		skipWhitespace();// 到\"或其它字符上
		int i = 0;
		int Char = -1;
		boolean checkCharOk = false;
		while (bufPos + i < bufCount) {
			if (checkCharOk && peekChar(i, false) == Char) {
				i += 1;
				break;
			} else if (checkCharOk) {
				++i;
				continue;
			} else if (peekChar(i, false) == '\'' || peekChar(i, false) == '\"') {
				Char = peekChar(i, false);
				checkCharOk = true;
			} else if (peekChar(i, false) == '>') {
				Char = '>';
				break;
			} else if (peekChar(i, false) == '/'
					&& peekChar(i + 1, false) == '>') {
				i += 1;
				Char = '>';
				break;
			} else if (peekChar(i, false) == '<') {
				Char = '<';
				break;
			} else if (isWhitespace(peekChar(i, false))) {
				Char = peekChar(i, false);
				break;
			}
			++i;
		}
		if (Char != -1) {
			bufPos += i;
		}
		skipWhitespace();
	}

	private void skipNumsChar(int i) {
		if (bufPos + i > bufCount) {
			return;
		}
		bufPos += i;
	}

	private void skipName() {// 跳过名字
		int i = peekChar(false);
		while (i < 127 && i > 32 && i != '/' && i != '>') {
			++bufPos;
			i = peekChar(false);
		}
	}

	private void parserComment() {
		++bufPos;// -
		++bufPos;// -
		while (peekChar(false) != -1) {
			skipTo(45);
			int i = 0;
			while (peekChar(true) == 45) {
				i++;
				++bufPos;
			}
			if (i >= 2 && peekChar(true) == 62) {// >
				++bufPos;
				break;
			}
		}
	}

	private void parserCData() {
		while (peekChar(false) != -1) {
			if (peekChar(false) == ']') {
				int i = 0;
				while (peekChar(false) == ']') {
					++bufPos;
					++i;
				}
				if (i >= 2 && peekChar(false) == '>') {
					++bufPos;
					break;
				}
			}
			++bufPos;
		}
	}

	private void parserDoctype() {
		skipTo('>');
		++bufPos;
	}

	private void parserPI() {
		skipTo('?');
		skipTo('>');
		++bufPos;
	}

	private void skipEnd() {
		skipTo('>');
		++bufPos;
	}

	private void skipWhitespace() {
		while (isWhitespace(peekChar(false))) {// 12
			++bufPos;
		}
	}

	private boolean isWhitespace(int Char) {
		if (Char >= 0 && Char <= 32 || Char == 127) {
			return true;
		}
		return false;
	}

	private void skipTo(int c) {
		int i = peekChar(false);
		while (i != c && i != -1) {
			++bufPos;
			i = peekChar(false);
		}
	}

	private int peekChar(boolean isTranslate) {
		return translateLower(bufPos < bufCount ? buf[bufPos] : -1, isTranslate);
	}

	private int peekChar(int i, boolean isTranslate) {
		return translateLower(bufPos + i < bufCount ? buf[bufPos + i] : -1,
				isTranslate);
	}

	private int translateLower(int c, boolean isTranslate) {
		if (!isTranslate) {
			return c;
		}
		if (c > 64 && c < 91) {
			c = c + 32;
		}
		return c;
	}

	private void backTo(int c) {
		while (peekChar(false) != c) {
			--bufPos;
			if (peekChar(false) < 0) {
				return;
			}
		}
	}
}
