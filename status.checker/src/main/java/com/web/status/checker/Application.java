package com.web.status.checker;

import com.web.status.checker.app.EternalVisaStatusCheckerApp;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.web.status.checker.model.Constants.*;

@Log4j2
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		runEternalVisaApplyChecker();
	}

	private static void runEternalVisaApplyChecker() {
		try {
			EternalVisaStatusCheckerApp app = new EternalVisaStatusCheckerApp();
		}
		catch (Exception exception) {
			String error = "";
			log.error(EXCEPTION_IN_APP);
			log.error(exception.getMessage());
			EternalVisaStatusCheckerApp appEx = new EternalVisaStatusCheckerApp(EXCEPTION_IN_APP);
		}
		finally {
			log.warn(PLS_RESTART_MESSAGE);
			EternalVisaStatusCheckerApp appEnd = new EternalVisaStatusCheckerApp(PLS_RESTART_MESSAGE);
		}

	}

}
