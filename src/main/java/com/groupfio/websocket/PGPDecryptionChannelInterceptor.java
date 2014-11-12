package com.groupfio.websocket;

import org.apache.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;

import com.groupfio.pgp.PGPProcessor;

public class PGPDecryptionChannelInterceptor extends ChannelInterceptorAdapter {
	
	public PGPDecryptionChannelInterceptor() {
		super();
	}

	private Logger log = Logger.getLogger(PGPDecryptionChannelInterceptor.class.getName());
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		 Message<?> pgpDecryptedMessage = null;
		
		 String msgAsString = null;
		 String decodedMsgAsString = null;
		 if (message.getPayload()!=null 
				 && (((byte[]) message.getPayload()).length > 0)
				 && !(new String((byte[]) message.getPayload()).isEmpty()) ) {
			 
			msgAsString = new String((byte[]) message.getPayload());
			byte[] pgpDecryptedPayload = PGPProcessor
					.decryptByteArray((byte[]) message.getPayload());
			decodedMsgAsString = new String(pgpDecryptedPayload);
			//create new message with decrypted payload
			pgpDecryptedMessage = new GenericMessage<byte[]>(
					pgpDecryptedPayload, message.getHeaders());
			
		}
		log.debug("INBOUND: preSend: " + message.toString());
		log.debug("INBOUND: pgp encrypted payload: "+(msgAsString!=null? msgAsString:"IS NULL"));
		log.debug("INBOUND: pgp decrypted payload: "+(decodedMsgAsString!=null? decodedMsgAsString:"IS NULL"));
		
		
		return (pgpDecryptedMessage!=null? pgpDecryptedMessage:message);
	}
	
	

}
