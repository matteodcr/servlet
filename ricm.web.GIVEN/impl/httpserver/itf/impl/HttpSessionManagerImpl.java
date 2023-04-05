package httpserver.itf.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import httpserver.itf.HttpSession;
import httpserver.itf.HttpSessionManager;

public class HttpSessionManagerImpl implements HttpSessionManager {
	private final int expirationDelay;
	private final Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<>();

	public HttpSessionManagerImpl(int expirationDelay) {
		this.expirationDelay = expirationDelay;
	}

	private static String generateId() {
		return UUID.randomUUID().toString();
	}

	private  static boolean isValidId(String id) {
		try {
			//noinspection ResultOfMethodCallIgnored
			UUID.fromString(id);
			return true;
		} catch (IllegalArgumentException ignored) {
			return false;
		}
	}

	@Override
	public HttpSession getSession(String id) {
		if (id == null || !isValidId(id)) {
			id = generateId();
		}

		var session = sessions.computeIfAbsent(id, HttpSessionImpl::new);
		session.refresh();
		return session;
	}

	// TODO
	private void purgeExpired() {
		sessions.values().removeIf(session -> session.isExpired(expirationDelay));
	}
}
