package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;


/**
 * Basic HTTP Server Implementation
 *
 * Only manages static requests
 * The url for a static ressource is of the form: "http//host:port/<path>/<ressource name>"
 * For example, try accessing the following urls from your brower:
 *    http://localhost:<port>/
 *    http://localhost:<port>/voile.jpg
 *    ...
 */
public class HttpServer {

	private static final String RICMLET_URL_BASE = "/ricmlets/";
	private int m_port;
	private File m_folder;  // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	private final Map<String, HttpRicmlet> ricmlets = new HashMap<>();

	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator))
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc=new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e );
			System.exit(1);
		}
	}

	public File getFolder() {
		return m_folder;
	}



	public HttpRicmlet getInstance(String clsname) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		var ricmlet = ricmlets.get(clsname);

		if (ricmlet == null) {
			Class<?> ricmletClass = getClass().getClassLoader().loadClass(clsname);
			Constructor<?> constructor = ricmletClass.getDeclaredConstructor();
			ricmlet = (HttpRicmlet) constructor.newInstance();
			ricmlets.put(clsname, ricmlet);
		}

		return ricmlet;
	}


	public HttpRicmlet getInstanceByURL(String url) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		if (url.startsWith(RICMLET_URL_BASE)) {
			var path = url.substring(RICMLET_URL_BASE.length());
			var inter = path.indexOf('?');
			if (inter > -1) {
				path = path.substring(0, inter);
			}

			var pkg = path.replace('/', '.');
			return getInstance(pkg);
		} else {
			return null;
		}
	}


	/*
	 * Reads a request on the given input stream and returns the corresponding HttpRequest object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		String startline = br.readLine();
		StringTokenizer parseline = new StringTokenizer(startline);
		String method = parseline.nextToken().toUpperCase();
		String ressname = parseline.nextToken();

		if (method.equals("GET")) {
			try {
				var ricmlet = getInstanceByURL(ressname);
				if (ricmlet != null) {
					return new HttpRicmletRequestImpl(this, method, ressname, br, ricmlet);
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				return new UnknownRequest(this, method, ressname);
			}

			return new HttpStaticRequest(this, method, ressname);
		} else
			return new UnknownRequest(this, method, ressname);
	}


	/*
	 * Returns an HttpResponse object associated to the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		return new HttpResponseImpl(this, req, ps);
	}


	/*
	 * Server main loop
	 */
	protected void loop() {
		try {
			while (true) {
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			hs.loop();
		}
	}

}

