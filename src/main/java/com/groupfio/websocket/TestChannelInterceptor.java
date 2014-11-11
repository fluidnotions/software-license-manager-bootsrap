package com.groupfio.websocket;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.GenericMessage;

import com.groupfio.pgp.PGPDecrypt;

public class TestChannelInterceptor extends ChannelInterceptorAdapter {
	
	private String channelName;
	private PGPDecrypt pgpDecrypt;
	
	
	public TestChannelInterceptor(String channelName) {
		super();
		this.channelName = channelName;
		this.pgpDecrypt = new PGPDecrypt();
		this.pgpDecrypt.setPassphrase("testpass");
		
		
		
	}

	private Logger log = Logger.getLogger(TestChannelInterceptor.class.getName());
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		String decoded = null;
		byte[] pgp = null;
		Message<?> plainmsg = null;
		try {
			 decoded = new String((byte[]) message.getPayload(), "UTF-8");
			 if("INBOUND".equals(channelName)){
				 pgp = pgpDecrypt.decrypt((byte[]) message.getPayload());
				//create new message with unencrypted payload
				plainmsg = new GenericMessage<byte[]>(pgp, message.getHeaders());
			 }else if("OUTBOUND".equals(channelName)){
				
			 } 
			 
			 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("("+channelName+") preSend: "+message.toString());
		log.debug("("+channelName+") payload: "+(decoded!=null? decoded:"IS NULL"));
		log.debug("("+channelName+") pgp payload: "+(pgp!=null? pgp:"IS NULL"));
		
		
		return (plainmsg!=null? plainmsg:message);
	}
	
	

}
