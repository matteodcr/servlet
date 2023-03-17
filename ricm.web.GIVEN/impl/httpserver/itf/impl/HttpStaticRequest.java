package httpserver.itf.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	public void process(HttpResponse resp) throws Exception {
		var path = new File(m_hs.getFolder(), m_ressname);
		var contentType = HttpRequest.getContentType(m_ressname);

		try {
			if (path.isDirectory()) {
				path = new File(path, DEFAULT_FILE);
				contentType = HttpRequest.getContentType(DEFAULT_FILE);
			}

			var bytes = Files.readAllBytes(path.toPath());
			resp.setReplyOk();
			resp.setContentType(contentType);
			resp.setContentLength((int) path.length());
			try (var body = resp.beginBody()) {
				body.write(bytes);
			}
		} catch (IOException e) {
			resp.setReplyError(e instanceof NoSuchFileException ? 404 : 500, e.getMessage());
			if (!(e instanceof NoSuchFileException)) {
				e.printStackTrace();
			}
		}
	}

}
