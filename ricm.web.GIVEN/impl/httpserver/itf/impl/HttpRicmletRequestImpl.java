package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {
    private final Map<String, String> headers;
    private final HttpRicmlet ricmlet;
    HashMap<String, String> queryParameters = new HashMap<>();

    private Map<String, String> cookies = null;
    String sessionId = null;

    public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, HashMap<String, String> headers, BufferedReader br, HttpRicmlet ricmlet) throws IOException {
        super(hs, method, ressname, br);
        this.headers = headers;
        this.ricmlet = ricmlet;
        List<String> decompose = Arrays.asList(ressname.split("\\?"));
        if (decompose.size() == 2){
            String[] args = decompose.get(1).split("&");
            for (String s : args) {
                List<String> arg = Arrays.asList(s.split("="));
                if (arg.size() == 2) {
                    queryParameters.put(arg.get(0), arg.get(1));
                }
            }
            this.m_ressname = decompose.get(0);
        }
    }

    @Override
    public void process(HttpResponse resp) throws Exception {
        ricmlet.doGet(this, new HttpRicmletResponseImpl(this, resp));
    }

    @Override
    public HttpSession getSession() {
        var sessionId = getCookie("session-id");
        var session = m_hs.getSessions().getSession(sessionId);
        this.sessionId = session.getId();
        return session;
    }

    @Override
    public String getArg(String name) {
        return queryParameters.get(name);
    }

    /**
     * Lazy-inits and returns cookies
     */
    private Map<String, String> getCookies() {
        if (cookies == null) {
            cookies = new HashMap<>();
            var cookieHeader = headers.get("Cookie");
            if (cookieHeader != null) {
                for (var cookie : cookieHeader.split("; ")) {
                    var entry = cookie.split("=", 2);
                    if (entry.length == 2) cookies.put(entry[0], entry[1]);
                }
            }
            cookies = Collections.unmodifiableMap(cookies);
        }

        return cookies;
    }

    @Override
    public String getCookie(String name) {
        return getCookies().get(name);
    }
}
