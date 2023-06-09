package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl implements HttpRicmletResponse {
	private final HttpRicmletRequestImpl req;
	private final HttpResponse base;

	public HttpRicmletResponseImpl(HttpRicmletRequestImpl req, HttpResponse base) {
		this.req = req;
		this.base = base;
	}

	@Override
	public void setReplyOk() throws IOException {
		base.setReplyOk();
	}

	@Override
	public void setReplyError(int codeRet, String msg) throws IOException {
		base.setReplyError(codeRet, msg);
	}

	@Override
	public void setContentLength(int length) throws IOException {
		base.setContentLength(length);
	}

	@Override
	public void setContentType(String type) throws IOException {
		base.setContentType(type);
	}

	@Override
	public void setHeader(String name, String value) throws IOException {
		base.setHeader(name, value);
	}

	@Override
	public PrintStream beginBody() throws IOException {
		if (req.sessionId != null) {
			setCookie("session-id", req.sessionId);
		}

		return base.beginBody();
	}

	@Override
	public void setCookie(String name, String value) throws IOException {
		if (value != null) {
			setHeader("Set-Cookie", "%s=%s".formatted(name, value));
		} else {
			setHeader("Set-Cookie", "%s=; Max-Age=0".formatted(name));
		}
	}
}
