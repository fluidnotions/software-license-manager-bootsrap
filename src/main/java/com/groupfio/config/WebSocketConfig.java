package com.groupfio.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import com.groupfio.websocket.DaemonHandshakeHandler;
import com.groupfio.websocket.TestChannelInterceptor;

@Configuration
@EnableScheduling
@ComponentScan("com.groupfio")
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	private Logger log = Logger.getLogger(WebSocketConfig.class.getName());

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/queue");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		StompWebSocketEndpointRegistration endpoint = registry
				.addEndpoint("/wscon");
		endpoint.setHandshakeHandler(new DaemonHandshakeHandler());

	}
	
	/*@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(new TestChannelInterceptor("INBOUND"));
	}
	
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(new TestChannelInterceptor("OUTBOUND"));
	}*/

}
