package ru.vados.effectiveMobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"ru.vados.effectiveMobile"})
public class EffectiveMobileApp {

	public static void main(String[] args) {
		SpringApplication.run(EffectiveMobileApp.class, args);
	}

}
