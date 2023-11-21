package org.apache.coyote.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public SessionManager() {
    }

    public Map<String, Session> getSessions() {
        return SESSIONS;
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        throw new IllegalArgumentException("올바르지 않은 세션 ID 입니다.");
    }
}
