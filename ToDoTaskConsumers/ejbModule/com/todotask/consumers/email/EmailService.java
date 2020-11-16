package com.todotask.consumers.email;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

public class EmailService {

	private static Logger log = Logger.getLogger(EmailService.class);
	
	private static EmailService sInstance;
		
	private Properties sessionProp;
	
	private EmailService(Properties p) {
		this.sessionProp = p;
	}
	
	public static EmailService getInstance(Properties p) {
		if(sInstance == null) {
			sInstance = new EmailService(p); 
		}
		return sInstance;
	}
	
	public void sendEmail(EmailMessage message){
		
		Session s = Session.getInstance(sessionProp,new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(message.getName(),String.valueOf(message.getPass()));
			}
		});
		
		MimeMessage mex = new MimeMessage(s);
		
		try {
		
		mex.setFrom(new InternetAddress(message.getFrom()));
		
		mex.setRecipients(RecipientType.TO, InternetAddress.parse(message.getTo()));
		
		mex.setContent(message.getContent(),message.getContentType());
		
		if(message.getSubject() != null) {
			mex.setSubject(message.getSubject());
		}
		
		Transport.send(mex);
		
		}catch(Exception e) {
			log.error("Error sending email",e);
			throw new RuntimeException();
		}
	}
	
	
	
	
	public static class EmailMessage{
		
		private String from;
		private String to;
		
		private String content;
		private String contentType;
		
		private String name;
		private char[] pass;
		
		private String subject;
		
		public EmailMessage(String from, String to, String content, String contentType, String name, char[] pass,String subject) {
			super();
			this.from = from;
			this.to = to;
			this.content = content;
			this.contentType = contentType;
			this.name = name;
			this.pass = pass;
			this.subject = subject;
		}
		
		public String getSubject() {
			return subject;
		}

		public String getFrom() {
			return from;
		}

		public String getTo() {
			return to;
		}

		public String getContent() {
			return content;
		}

		public String getContentType() {
			return contentType;
		}

		public String getName() {
			return name;
		}

		public char[] getPass() {
			return pass;
		}
		
				
	}
	
	public static class EmailBuilder{
			
		private String from;
		private String to;
		
		private String content;
		private String contentType;
		
		private String name;
		private char[] pass;
		
		private String subject;
		
		public EmailBuilder() {
			
		}
		
		public EmailBuilder setContent(String content,String type) {
			this.content = content;
			this.contentType = type;
			return this;
		}
		
		public EmailBuilder setAddress(String from,String to) {
			this.from = from;
			this.to = to;
			return this;
		}
		
		public EmailBuilder setAuth(String name,char[] pass) {
			this.name = name;
			this.pass = pass;
			return this;
		}
		
		public EmailBuilder setSubject(String subject) {
			this.subject = subject;
			return this;
		}
		
		public EmailMessage build() {
			return new EmailMessage(from,to,content,contentType,name,pass,subject);
		}
	}
	
	
}
