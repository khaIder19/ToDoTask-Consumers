package com.todotask.consumers.user;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.ResourceAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todotask.consumers.email.EmailService;
import com.todotask.json.user.UserIdentityItemCollection;

/**
 * Message-Driven Bean implementation class for: UserEventConsumer
 */
@MessageDriven(name = "UserEventConsumer",
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "ToDoTaskUserSignInQueue"),
		 @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		})
@ResourceAdapter(value="activemq")
public class UserEventConsumer implements MessageListener {

	private static Logger log = Logger.getLogger(UserEventConsumer.class);
	
	private static Properties p;
	
	
	private static final String USER_CONSUMER_PROP_ENV_NAME = "env.var.prop.email";
	private static final String ENV_EMAIL_USER = "env.var.email.user_name";
	private static final String ENV_USER_EMAIL_PASS = "env.var.email.pass";
	private static final String EMAIL_TEXT = "email.content";
	private static final String EMAIL_SUBJECT = "email.subject";
	private static final String EMAIL_FROM = "email.from";
	
	static {
		p = new Properties();
		try {
			p.load(new FileReader(System.getenv(USER_CONSUMER_PROP_ENV_NAME)));
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	private EmailService emailService ;
	
	private ObjectMapper mapper;
	
	private EmailService.EmailBuilder preDefinedEmailBuilder;
	
    public UserEventConsumer() {
        emailService = EmailService.getInstance(p);
        mapper = new ObjectMapper();
        preDefinedEmailBuilder = new EmailService.EmailBuilder().setAuth(System.getenv(ENV_EMAIL_USER),System.getenv(ENV_USER_EMAIL_PASS).toCharArray())
        		.setContent(p.getProperty(EMAIL_TEXT),"text/plain");
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	UserIdentityItemCollection user = null;
    	
        try {
        	if(message instanceof TextMessage) {
        		String body = ((TextMessage)message).getText();
        		user = mapper.readValue(body, UserIdentityItemCollection.class);
        		emailService.sendEmail(preDefinedEmailBuilder.setAddress(p.getProperty(EMAIL_FROM),user.getUser_email()).setSubject(p.getProperty(EMAIL_SUBJECT)).build());
        		log.info("User registration email sent successfully "+"(user_uid:"+user.getUser_id()+")"+"(user_email:"+user.getUser_email()+")");
        	}else {
        		log.warn("Unexpected message type : (message_id:"+message.getJMSMessageID()+")");
        	}
       
        } catch (Exception e) {
			log.error("User event not processed successfully",e);
		}
        
    }

}
