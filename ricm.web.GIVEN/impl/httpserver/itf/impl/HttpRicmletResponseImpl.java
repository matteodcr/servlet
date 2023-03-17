package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl implements HttpRicmletResponse {
	private final HttpResponse base;

	public HttpRicmletResponseImpl(HttpResponse base) {
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
	public PrintStream beginBody() throws IOException {
		return base.beginBody();
	}

	@Override
	public void setCookie(String name, String value) {
		throw new UnsupportedOperationException("unimplemented"); // TODO
	}
}
