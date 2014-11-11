package com.groupfio.websocket;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class DaemonHandshakeHandler extends DefaultHandshakeHandler {

	private Logger log = Logger.getLogger(DaemonHandshakeHandler.class
			.getName());

	@Override
	protected Principal determineUser(ServerHttpRequest request,
			WebSocketHandler wsHandler, Map<String, Object> attributes) {

		// if there are ever attributes - so far there haven't been any
		String atts = "";
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			atts += entry.getKey() + " -> " + entry.getValue() + "|";
		}
		if (atts.length() > 0)
			log.debug("determineUser: attributes: " + atts);

		Principal principal = request.getPrincipal();
		if (principal != null) {
			log.debug("determineUser: request.getPrincipal() class type: "
					+ principal.getClass().getName());
		}
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(
				request.getHeaders());
		log.debug("determineUser: WebSocketHttpHeaders headers: " + headers);
		List<String> cookies = headers.get("cookie");
		String username = null;
		for (String cookie : cookies) {
			if (cookie.startsWith("user=")) {
				username = cookie.substring(cookie.indexOf("=\"") + 2,
						cookie.length() - 1);
			}
			log.debug("determineUser: cookie: " + cookie
					+ (username != null ? ", username: " + username : ""));// user="testserial01"
		}
		final String name = username;
		Principal user = new Principal() {

			@Override
			public String getName() {
				return name;
			}

		};
		return user;
	}

}
