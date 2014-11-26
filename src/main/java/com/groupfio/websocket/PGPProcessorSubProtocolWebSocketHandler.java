package com.groupfio.websocket;

import org.apache.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import com.groupfio.pgp.PGPProcessor;

public class PGPProcessorSubProtocolWebSocketHandler extends SubProtocolWebSocketHandler {
	
	private Logger log = Logger.getLogger(PGPProcessorSubProtocolWebSocketHandler.class.getName());

	public PGPProcessorSubProtocolWebSocketHandler(MessageChannel clientInboundChannel,
			SubscribableChannel clientOutboundChannel) {
		super(clientInboundChannel, clientOutboundChannel);
	}

	/**
	 * Handle an inbound message from a WebSocket client.
	 */
	@Override
	public void handleMessage(WebSocketSession session,
			WebSocketMessage<?> message) throws Exception {
		
		TextMessage textMessage = (TextMessage) message;
		log.debug("INBOUND: handleMessage: textMessage payload:" + textMessage.getPayload());
		if (!textMessage.getPayload().startsWith("CONNECT")
			&& !textMessage.getPayload().startsWith("SUBSCRIBE")) {
			try {
				log.debug("INBOUND: handleMessage: decryption process - does not start with CONNECT/SUBSCRIBE");
				byte[] pgpDecryptedPayload = PGPProcessor
						.decryptByteArray(textMessage.asBytes());
				log.debug("INBOUND: handleMessage: textMessage pgpDecryptedPayload:"
						+ new String(pgpDecryptedPayload));
				message = new TextMessage(pgpDecryptedPayload);
			} catch (Throwable e) {e.printStackTrace();}
		}else{
			log.debug("INBOUND: handleMessage: skipping decryption - starts with CONNECT/SUBSCRIBE");
		}
		super.handleMessage(session, message);
	}

	/**
	 * Handle an outbound Spring Message to a WebSocket client.
	 */
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		/*TextMessage textMessage = (TextMessage) message;
		log.debug("INBOUND: handleMessage: textMessage payload:" + textMessage.getPayload());
		if (!textMessage.getPayload().startsWith("CONNECT")
			&& !textMessage.getPayload().startsWith("SUBSCRIBE")) {
			try {
				log.debug("INBOUND: handleMessage: decryption process - does not start with CONNECT/SUBSCRIBE");
				byte[] pgpDecryptedPayload = PGPProcessor
						.decryptByteArray(textMessage.asBytes());
				log.debug("INBOUND: handleMessage: textMessage pgpDecryptedPayload:"
						+ new String(pgpDecryptedPayload));
				message = new TextMessage(pgpDecryptedPayload);
			} catch (Throwable e) {e.printStackTrace();}
		}else{
			log.debug("INBOUND: handleMessage: skipping decryption - starts with CONNECT/SUBSCRIBE");
		}*/
		super.handleMessage(message);
	}
	


}
