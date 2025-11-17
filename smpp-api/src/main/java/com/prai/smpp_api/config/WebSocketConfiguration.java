package com.prai.smpp_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();;
	}

//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry registry) {
//		// Use ActiveMQ as the message broker
//		registry.enableStompBrokerRelay("/topic", "/queue")
//				.setRelayHost("localhost")       // ActiveMQ host
//				.setRelayPort(61615)             // Default STOMP port for ActiveMQ
//				.setClientLogin("admin")         // ActiveMQ username
//				.setClientPasscode("admin");
//
////		registry.enableSimpleBroker("/topic");
//		registry.setApplicationDestinationPrefixes("/app");
//	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/queue"); // Use /queue for clients to subscribe
		config.setApplicationDestinationPrefixes("/app");
	}
}

//docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 apache/activemq-artemis:latest
//docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 apache/activemq-artemis

