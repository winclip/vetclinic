package dev.winclip.vetclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dev.winclip.vetclinic.auth.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class VetclinicApplication {

	public static void main(String[] args) {
		if (useRailwayProdProfile()) {
			System.setProperty("spring.profiles.active", "prod");
		}
		SpringApplication.run(VetclinicApplication.class, args);
	}

	/**
	 * Railway sets RAILWAY_ENVIRONMENT; we have no committed application-local.yaml in the image,
	 * so default "local" would start without a datasource. Activate prod when the user did not
	 * override profiles (e.g. {@code SPRING_PROFILES_ACTIVE=prod} in the dashboard still works).
	 */
	private static boolean useRailwayProdProfile() {
		String fromEnv = System.getenv("SPRING_PROFILES_ACTIVE");
		if (fromEnv != null && !fromEnv.isBlank()) {
			return false;
		}
		String fromSys = System.getProperty("spring.profiles.active");
		if (fromSys != null && !fromSys.isBlank()) {
			return false;
		}
		return System.getenv("RAILWAY_ENVIRONMENT") != null;
	}

}
