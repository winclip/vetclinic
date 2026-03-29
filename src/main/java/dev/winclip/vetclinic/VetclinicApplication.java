package dev.winclip.vetclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dev.winclip.vetclinic.auth.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class VetclinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetclinicApplication.class, args);
	}

}
