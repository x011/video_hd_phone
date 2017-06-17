package com.mooncloud.heart.beat;

import java.io.*;

public class Base64 {

	private static final char S_BASE64CHAR[] = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };
	private static final char S_BASE64PAD = 61;
	private static final byte S_DECODETABLE[];

	public Base64() {
		
	}

	private static int decode0(char ibuf[], byte obuf[], int wp)

	{
		int outlen = 3;
		if (ibuf[3] == '=') {
			outlen = 2;
		}
		if (ibuf[2] == '=') {
			outlen = 1;
		}
		int b0 = S_DECODETABLE[ibuf[0]];
		int b1 = S_DECODETABLE[ibuf[1]];
		int b2 = S_DECODETABLE[ibuf[2]];
		int b3 = S_DECODETABLE[ibuf[3]];
		switch (outlen) {
		case 1: // '\001'
			obuf[wp] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
			return 1;

		case 2: // '\002'
			obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
			obuf[wp] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
			return 2;

		case 3: // '\003'
			obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
			obuf[wp++] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
			obuf[wp] = (byte) (b2 << 6 & 0xc0 | b3 & 0x3f);
			return 3;
		}
		// throw new RuntimeException(Messages.getMessage("internalError00"));

		return -1;
	}

	public static byte[] decode(char data[], int off, int len) {
		char ibuf[] = new char[4];
		int ibufcount = 0;
		byte obuf[] = new byte[(len / 4) * 3 + 3];
		int obufcount = 0;
		for (int i = off; i < off + len; i++) {
			char ch = data[i];
			if (ch == '=' || ch < S_DECODETABLE.length
					&& S_DECODETABLE[ch] != 127) {
				ibuf[ibufcount++] = ch;
				if (ibufcount == ibuf.length) {
					ibufcount = 0;
					obufcount += decode0(ibuf, obuf, obufcount);
				}
			}
		}

		if (obufcount == obuf.length) {
			return obuf;
		} else {
			byte ret[] = new byte[obufcount];
			System.arraycopy(obuf, 0, ret, 0, obufcount);
			return ret;
		}
	}

	public static byte[] decode(String data) {
		char ibuf[] = new char[4];
		int ibufcount = 0;
		byte obuf[] = new byte[(data.length() / 4) * 3 + 3];
		int obufcount = 0;
		for (int i = 0; i < data.length(); i++) {
			char ch = data.charAt(i);
			if (ch == '=' || ch < S_DECODETABLE.length
					&& S_DECODETABLE[ch] != 127) {
				ibuf[ibufcount++] = ch;
				if (ibufcount == ibuf.length) {
					ibufcount = 0;
					obufcount += decode0(ibuf, obuf, obufcount);
				}
			}
		}

		if (obufcount == obuf.length) {
			return obuf;
		} else {
			byte ret[] = new byte[obufcount];
			System.arraycopy(obuf, 0, ret, 0, obufcount);
			return ret;
		}
	}

	public static void decode(char data[], int off, int len,
			OutputStream ostream) throws IOException {
		byte[] by = decode(data, off, len);
		ostream.write(by, 0, by.length);
	}

	public static void decode(String data, OutputStream ostream)
			throws IOException {
		byte[] by = decode(data);
		ostream.write(by, 0, by.length);
	}

	public static String encode(byte data[]) {
		return encode(data, 0, data.length);
	}

	public static String encode(byte data[], int off, int len) {
		if (len <= 0) {
			return "";
		}
		char out[] = new char[(len / 3) * 4 + 4];
		int rindex = off;
		int windex = 0;
		int rest;
		for (rest = len - off; rest >= 3; rest -= 3) {
			int i = ((data[rindex] & 0xff) << 16)
					+ ((data[rindex + 1] & 0xff) << 8)
					+ (data[rindex + 2] & 0xff);
			out[windex++] = S_BASE64CHAR[i >> 18];
			out[windex++] = S_BASE64CHAR[i >> 12 & 0x3f];
			out[windex++] = S_BASE64CHAR[i >> 6 & 0x3f];
			out[windex++] = S_BASE64CHAR[i & 0x3f];
			rindex += 3;
		}

		if (rest == 1) {
			int i = data[rindex] & 0xff;
			out[windex++] = S_BASE64CHAR[i >> 2];
			out[windex++] = S_BASE64CHAR[i << 4 & 0x3f];
			out[windex++] = '=';
			out[windex++] = '=';
		} else if (rest == 2) {
			int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
			out[windex++] = S_BASE64CHAR[i >> 10];
			out[windex++] = S_BASE64CHAR[i >> 4 & 0x3f];
			out[windex++] = S_BASE64CHAR[i << 2 & 0x3f];
			out[windex++] = '=';
		}
		return new String(out, 0, windex);
	}

	public static void encode(byte data[], int off, int len,
			OutputStream ostream) throws IOException {
		if (len <= 0) {
			return;
		}
		String str = encode(data, off, len);
		byte[] by = str.getBytes();
		ostream.write(by, 0, by.length);
	}

	public static void encode(byte data[], int off, int len, Writer writer)
			throws IOException {
		if (len <= 0) {
			return;
		}
		String str = encode(data, off, len);
		// byte[] by =str.getBytes();
		writer.write(str, 0, str.length());
	}

	static {
		S_DECODETABLE = new byte[128];
		for (int i = 0; i < S_DECODETABLE.length; i++) {
			S_DECODETABLE[i] = 127;

		}
		for (int i = 0; i < S_BASE64CHAR.length; i++) {
			S_DECODETABLE[S_BASE64CHAR[i]] = (byte) i;

		}
	}
}
