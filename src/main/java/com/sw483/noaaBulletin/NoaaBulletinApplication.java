package com.sw483.noaaBulletin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import controller.Test;

@ComponentScan(basePackageClasses = Test.class)


@SpringBootApplication
public class NoaaBulletinApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoaaBulletinApplication.class, args);
	}
}
