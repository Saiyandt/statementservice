package com.example.statementservice;

import com.example.statementservice.service.StatementConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class StatementserviceApplication {

	@Autowired
	StatementConsumer statementConsumer;

	public static void main(String[] args) {
		SpringApplication.run(StatementserviceApplication.class, args);
	}

	/*@Bean(initMethod="init")
	public StatementConsumer statementConsumerBean() {
		return new StatementConsumer();
	}*/
}
