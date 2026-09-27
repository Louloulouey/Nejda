package com.example.rescueapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class RescueappApplication {

	public static void main(String[] args) {
		SpringApplication.run(RescueappApplication.class, args);
	}

	@PostConstruct
	public void initFirebase() throws IOException {
		InputStream serviceAccount = getClass().getResourceAsStream("/firebase_admin.json");

        assert serviceAccount != null;
        FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}

}
