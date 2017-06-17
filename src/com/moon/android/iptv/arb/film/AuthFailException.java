package com.moon.android.iptv.arb.film;

public class AuthFailException extends AuthException {
	private static final long serialVersionUID = 1L;

	public AuthFailException() {
	}

	public AuthFailException(String paramString) {
		super(paramString);
	}

	public AuthFailException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public AuthFailException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}
