package httpserver.itf.impl;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HttpDynamicRequest extends HttpRicmletRequest {
    static final String DEFAULT_FILE = "index.html";

    HashMap<String, String> queryParameters = new HashMap<>();

    public HttpDynamicRequest(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
        super(hs, method, ressname, br);
        List<String> decompose = Arrays.asList(ressname.split("\\?"));
        if (decompose.size() == 2){
            List<String> args = Arrays.asList(decompose.get(1).split("&"));
            for (int i = 0; i<args.size()-1; i++){
                List<String> arg = Arrays.asList(args.get(i).split("="));
                if (arg.size() == 2) {
                    queryParameters.put(arg.get(0), arg.get(1));
                }
            }
            this.m_ressname = decompose.get(0);
        }
    }
    @Override
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

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String getArg(String name) {
        return queryParameters.get(name);
    }

    @Override
    public String getCookie(String name) {
        return null;
    }
}
