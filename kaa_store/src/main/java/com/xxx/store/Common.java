package com.xxx.store;

import java.io.UnsupportedEncodingException;

public class Common {


	public static final String[] encodes = new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1", "ISO-8859-2"};

	public static String getEncode(String str) {
		byte[] data = str.getBytes();
		byte[] b = null;
		a:
		for (int i = 0; i < encodes.length; i++) {
			try {
				b = str.getBytes(encodes[i]);
				if (b.length != data.length)
					continue;
				for (int j = 0; j < b.length; j++) {
					if (b[j] != data[j]) {
						continue a;
					}
				}
				return encodes[i];
			} catch (UnsupportedEncodingException e) {
				continue;
			}
		}
		return null;
	}

	public static String transcoding(String str, String encode) {
		String df = "ISO-8859-1";
		try {
			String en = getEncode(str);
			if (en == null)
				en = df;
			return new String(str.getBytes(en), encode);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}




}
