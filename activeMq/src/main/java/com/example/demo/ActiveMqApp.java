package com.example.demo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@SpringBootApplication
@EnableJms
public class ActiveMqApp {

	@Value("${spring.activemq.brokerurl}")
	private String brokerUrl;

	@Value("${spring.activemq.user}")
	private String userName;

	@Value("${spring.activemq.password}")
	private String password;

	public String getBrokerUrl() {
		return brokerUrl;
	}
	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Bean
	public ActiveMQConnectionFactory ConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setUserName(userName);
		activeMQConnectionFactory.setPassword(password);
		activeMQConnectionFactory.setBrokerURL(brokerUrl);
		return activeMQConnectionFactory;

	}
	@Bean
	public JmsTemplate JmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(ConnectionFactory());
		return jmsTemplate;
	}

	@Bean
	public DefaultJmsListenerContainerFactory containerFactory() {
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(ConnectionFactory());
		containerFactory.setConcurrency("1-1");
		return containerFactory;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ActiveMqApp.class, args);
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.send("Push Message", new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("Check this");
			}
		});
	}

	@JmsListener(destination = "Push Message", containerFactory="containerFactory")
	public String readMessage(final Message message) {
		System.out.println("Received message "+message);
		return null;

	}
}
