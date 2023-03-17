package httpserver.itf.impl;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {
    private final HttpRicmlet ricmlet;
    HashMap<String, String> queryParameters = new HashMap<>();

    public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br, HttpRicmlet ricmlet) throws IOException {
        super(hs, method, ressname, br);
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
        ricmlet.doGet(this, new HttpRicmletResponseImpl(resp));
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
