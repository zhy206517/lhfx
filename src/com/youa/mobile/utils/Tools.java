package com.youa.mobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.TextView;

public class Tools {
	public static Context context;
	// private String subjectPattern = "#([\\S^\\n\\r]*)#"; // 话题的模式
	// private String atPattern = "@((([0-9a-zA-Z_\\xa1-\\xff])+)?)"; // at的模式
	// private String facePattern = "(\\[[\\S^\\n\\r]\\])"; // 表情的模式
	// private String urlPattern =
	// "((http|ftp|https|file):\\/\\/([\\w\\-]+\\.)+[\\w\\-]+(\\/[\\w\\-\\.\\/?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)";
	// // url的模式
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("今天 HH:mm");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("昨天 HH:mm");

	public static void parseContent(String content, String patt) {
		Pattern pattern = Pattern.compile(patt);
		Matcher match = pattern.matcher(content);
		while (match.find())
			System.out.println(match.group(1));
	}

	public static final String translateToString(long time) {

		StringBuffer sb = new StringBuffer();
		final long now = System.currentTimeMillis();
		// final long sub = now - time;
		// if (sub < 1 * 60 * 1000) {// 5分钟
		// sb.append("1分钟前 ");
		// } else if (sub < 60 * 60 * 1000) {// 1个小时
		// sb.append(sub / 60 / 1000 + "分钟前");
		// } else {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(now);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		final long yesterday = calendar.getTimeInMillis();
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		final long lastyear = calendar.getTimeInMillis();
		calendar.setTimeInMillis(time);
		if (time > yesterday) {
			sb.append("今天 ");
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			sb.append(zeroFill(hour));
			// if (hour < 10) {
			// sb.append("0");
			// }
			// sb.append(hour);
			sb.append(":");
			int minute = calendar.get(Calendar.MINUTE);
			// if (hour < 10) {
			// sb.append("0");
			// }
			// sb.append(minute);
			sb.append(zeroFill(minute));
		}
		// else if (time > yesterday - 24 * 60 * 60 * 1000) {
		// sb.append("昨天 ");
		// }
		else {
			if (time < lastyear) {
				sb.append(calendar.get(Calendar.YEAR));
				sb.append("年");
				// sb.append("-");
			}
			int month = calendar.get(Calendar.MONTH) + 1;
			if (month < 10) {
				sb.append(0);
			}
			sb.append(month);
			sb.append("月");
			// sb.append("-");
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if (day < 10) {
				sb.append(0);
			}
			sb.append(day);
			// sb.append(" ");
			sb.append("日 ");
		}

		// }
		return sb.toString();

	}

	public static final String translateTime(long time) {

		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		final long lastyear = calendar.getTimeInMillis();
		calendar.setTimeInMillis(time);
		if (time < lastyear) {
			sb.append(calendar.get(Calendar.YEAR));
			sb.append("\n");
			// sb.append("-");
		}
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			sb.append(0);
		}
		sb.append(month);
		sb.append("月");
		return sb.toString();

	}

	// "time_line":"2012-06-01 00:00:00"
	public static final String translateToString(String time) {
		int index = time.indexOf(" ");
		String str = time.substring(0, index);
		int[] data = getData(str, "-");
		int year = data[0];
		int month = data[1];
		int date = data[2];
		str = time.substring(index + 1);
		data = getData(str, ":");
		int hour = data[0];
		int minute = data[1];
		// int second = data[2];
		// --------------------------------------------
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTimeInMillis(System.currentTimeMillis());
		// int nowSecond = calendar.get(Calendar.SECOND);
		// int nowMinute = calendar.get(Calendar.MINUTE);
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		int nowDate = calendar.get(Calendar.DAY_OF_MONTH);
		int nowMonth = calendar.get(Calendar.MONTH) + 1;
		int nowYear = calendar.get(Calendar.YEAR);
		StringBuffer sb = new StringBuffer();
		if (year < nowYear) {
			sb.append(year);
			sb.append("年");
			sb.append(zeroFill(month));
			sb.append("月");
			sb.append(zeroFill(date));
			sb.append("日");
		} else if (month < nowMonth || date < nowDate) {
			sb.append(zeroFill(month));
			sb.append("月");
			sb.append(zeroFill(date));
			sb.append("日");
		} else if (hour < nowHour) {
			sb.append("今天");
			sb.append(zeroFill(hour));
			sb.append(":");
			sb.append(zeroFill(minute));
		}
		// }else if(minute<nowMinute){
		// sb.append(minute);
		// sb.append("分钟前");
		// }else{
		// sb.append("1分钟前");
		// }
		return sb.toString();
	}

	public static String translateTimelineToString(String timeline) {
		if (TextUtils.isEmpty(timeline)) {
			return "";
		}
		Date date = null;
		try {
			date = format.parse(timeline);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.clear(Calendar.MILLISECOND);
			calendar.add(Calendar.DATE, -1);
			Date yestoday = calendar.getTime();
			yestoday.setHours(0);
			yestoday.setMinutes(0);
			yestoday.setSeconds(0);
			Date publishDate = new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0);
			// LogUtil.d("Tools", "translateTimelineToString. yestoday = " +
			// yestoday.getTime() + ", time = "
			// + publishDate.getTime());
			if (publishDate.after(yestoday)) { // 今天
			// LogUtil.d("Tools", "translateTimelineToString. 今天");
				return dateFormat1.format(date);
			} else if (publishDate.before(yestoday)) { // 前天及以前
			// LogUtil.d("Tools", "translateTimelineToString. 前天以前");
				return dateFormat.format(date);
			} else { // 昨天
			// LogUtil.d("Tools", "translateTimelineToString. 昨天");
				return dateFormat2.format(date);
			}
		}
		return "";
	}

	private static int[] getData(String str, String split) {
		if (str == null) {
			return null;
		}
		int index = 0;
		int startIndex = 0;
		int[] data = new int[3];
		for (int i = 0; i < 3; i++) {
			index = str.indexOf(split, startIndex);
			if (index > 0) {
				try {
					data[i] = Integer.parseInt(str.substring(startIndex, index));
				} catch (Exception e) {
					data[i] = 0;
				}
				startIndex = index + 1;
			} else if (i == 2) {
				try {
					data[i] = Integer.parseInt(str.substring(startIndex));
				} catch (Exception e) {
					data[i] = 0;
				}
			}
		}
		return data;
	}

	/**
	 * 不够两位数前面补零，否则输出该整数的字符串形式
	 * 
	 * @param value
	 *            一个整数
	 * @return 返回一个不小于两位的字符串
	 */
	private static String zeroFill(int value) {
		String ret = "";
		if (value < 10) {
			ret = "0" + value;
		} else {
			ret = value + "";
		}
		return ret;

	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void setLimitText(TextView tv, int screenWidth, int needLine, int exWidth) {
		if (screenWidth <= 0) {
			return;
		}
		Paint paint = tv.getPaint();
		int width = screenWidth - tv.getPaddingLeft() - tv.getPaddingRight();
		CharSequence charSepuece = tv.getText();
		// tv.setTag(charSepuece);
		tv.setText("");
		float sbcWidth = paint.measureText("a");
		float fontWidth = paint.measureText("正");
		int i = 0, lines = 1;
		char c = 0;
		float measureWidth = 0;
		for (; i < charSepuece.length(); i++) {
			c = charSepuece.charAt(i);
			if (c < 127) {
				measureWidth += sbcWidth;
			} else {
				measureWidth += fontWidth;
			}
			if (measureWidth >= width) {
				lines++;
				i = measureWidth > width && i > 0 ? i - 1 : i;
				measureWidth = 0;
			}
			if (lines > needLine) {
				break;
			}
		}
		if (lines <= needLine) {
			tv.setText(charSepuece);
			return;
		}
		if (i >= charSepuece.length()) {
			i -= 1;
		}
		float w = exWidth + paint.measureText("...");
		int j = i;
		for (; j >= 0; j--) {
			c = charSepuece.charAt(j);
			if (c < 127) {
				measureWidth += sbcWidth;
			} else {
				measureWidth += fontWidth;
			}
			if (measureWidth >= w) {
				j = measureWidth > w ? j - 1 : j;
				break;
			}
		}
		if (j < 0) {
			j = 0;
		}
		tv.append(charSepuece.subSequence(0, j));
		tv.append("...");
	}
}
