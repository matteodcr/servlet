package httpserver.itf.impl;

import httpserver.itf.HttpSession;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionImpl implements HttpSession {
	private final String id;
	private final Map<Object, Object> kv = new ConcurrentHashMap<>();
	private volatile Instant lastUpdate;

	public HttpSessionImpl(String id) {
		this.id = id;
	}

	public void refresh() {
		lastUpdate = Instant.now();
	}

	public boolean isExpired(int expirationDelay) {
		return kv.isEmpty() || Duration.between(lastUpdate, Instant.now()).getSeconds() > expirationDelay;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getValue(String key) {
		return kv.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		kv.put(key, value);
	}
}
