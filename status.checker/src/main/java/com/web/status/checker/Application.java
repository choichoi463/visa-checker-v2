package com.web.status.checker;

import com.web.status.checker.app.EternalVisaStatusCheckerApp;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		runEternalVisaApplyChecker();
	}

	@SneakyThrows
	private static void runEternalVisaApplyChecker() {
		EternalVisaStatusCheckerApp app = new EternalVisaStatusCheckerApp();
		app.onExit();
	}

}
