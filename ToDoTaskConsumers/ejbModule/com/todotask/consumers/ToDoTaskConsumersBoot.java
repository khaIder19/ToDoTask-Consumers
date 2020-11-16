package com.todotask.consumers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@Startup
@Singleton
public class ToDoTaskConsumersBoot {

	private static Properties p;
	
	public static final String ENV_START_UP_PROPS ="env.var.prop.consumers.startup";
	public static final String LOGGER_CONFIG_PATH ="log.config.path";
	
	static {
		p = new Properties();
		try {
			p.load(new FileInputStream(System.getenv().get(ENV_START_UP_PROPS)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ToDoTaskConsumersBoot() {
	
	}
	
	@PostConstruct
	public void init() {
		try {
			Properties logConfigProp = new Properties();
			logConfigProp.load(new FileInputStream(p.getProperty(LOGGER_CONFIG_PATH)));
			PropertyConfigurator.configure(logConfigProp);
		}  catch (IOException e) {
			e.printStackTrace();
		}
		
		Logger log = Logger.getLogger(ToDoTaskConsumersBoot.class);
		
			
        log.info("ToDoTask consumers ejb initialization completed");
	}
	
}
