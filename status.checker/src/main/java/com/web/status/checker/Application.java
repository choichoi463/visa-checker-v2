package com.web.status.checker;

import com.codeborne.selenide.*;
import com.web.status.checker.app.EternalVisaStatusCheckerApp;
import com.web.status.checker.helpers.AudioPlayer;

import com.web.status.checker.helpers.Telegram;
import com.web.status.checker.model.UserDataObywatel;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.web.status.checker.model.Constants.*;

@Log4j2
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
//		runEternalVisaApplyChecker();
        runObywatelstwoRegistrationPageChecker();
    }


    /**
     * This runs ui procedures to click on Belarusian visa embassy web to find out the time when the web does not fails
     * with an error (no timeslots). But experimentally was figured out that during a week or more - there were no
     * such slots at all.
     * @throws InterruptedException
     */
    private static void runEternalVisaApplyChecker() throws InterruptedException {
        while (true) {
            try {
                EternalVisaStatusCheckerApp app = new EternalVisaStatusCheckerApp();
            } catch (Exception exception) {
                String error = "error dude";
                log.error(EXCEPTION_IN_APP);
                log.error(exception.getMessage());
                EternalVisaStatusCheckerApp appEx = new EternalVisaStatusCheckerApp(EXCEPTION_IN_APP);
            } finally {
                log.warn(PLS_RESTART_MESSAGE);
                EternalVisaStatusCheckerApp appEnd = new EternalVisaStatusCheckerApp(PLS_RESTART_MESSAGE);
            }
            Thread.sleep(10000);
        }

    }


    /**
     * This one is for Polish Urzad. Runs ui procedures to get to the page with a specific visit registration, and if
     * there is no error displayed (no timeslots available).
     * Then sends a message via Telegram indicating a success (no ui error)
     * Then tries to register two users one by one with an interval of 10.
     * On a failure -- waits on the page, sends a message to Telegram, so user can open ui and finish the
     * registration manually.
     * The whole thing runs in a loop with a limit of retry counter.
     * @throws Exception
     */
    private static void runObywatelstwoRegistrationPageChecker() throws Exception {
        String waveFile = "C:\\paste your path to the wave file here\\file.wav";
        boolean firstUserRegistered = false;
        boolean secondUserRegistered = false;
        int count = 0;
        int maxTries = 9999999;
        boolean maxRetriesFlag = false;
        while (!maxRetriesFlag) {
            try {
                Selenide.closeWebDriver();
                // setup browser
                Configuration.browser = "firefox";
                Configuration.headless = false;
                Configuration.savePageSource = true;
                ChromeOptions options = new ChromeOptions();
                options.setHeadless(false);
                final String BASE_URL = "https://rezerwacja.gdansk.uw.gov.pl:8445/qmaticwebbooking/#/";
                //do clicks
                Selenide.open(BASE_URL);
                //select urzad 1
                $("#main-container").shouldBe(Condition.visible, Duration.ofSeconds(10));
                $(byText("Wydział Spraw Obywatelskich i Cudzoziemców Pomorski Urząd Wojewódzki w Gdańsku")).shouldBe(Condition.visible);
                $(byText("Wydział Spraw Obywatelskich i Cudzoziemców Pomorski Urząd Wojewódzki w Gdańsku")).click();
                //select sprawa 2
                $(byText("Składanie wniosków i dokumentacji do wniosków już złożonych w sprawie obywatelstwa polskiego")).shouldBe(Condition.visible, Duration.ofSeconds(2));
                $(byText("Składanie wniosków i dokumentacji do wniosków już złożonych w sprawie obywatelstwa polskiego")).click();
                //select time 3
                Thread.sleep(600);
                $("#dateTimePanel").click();
                $(".timeslot-container").shouldBe(Condition.enabled, Duration.ofSeconds(2));
                if ($$("div.v-alert__content").get(0).isDisplayed()) {
                    // bad
                }
                if (!$$("div.v-alert__content").get(0).isDisplayed()) {
                    String HOORAY_MESSAGE = "Hooray, there is no ui alert, so take a look now yourself and register!";
                    Selenide.screenshot("it_worked_" + System.currentTimeMillis());
                    log.info(HOORAY_MESSAGE);
                    AudioPlayer.playSound(waveFile);
                    Telegram.sendMessage(HOORAY_MESSAGE);
                    try {
                        if (firstUserRegistered == false) {
                            UserDataObywatel user1 = new UserDataObywatel();
                            user1.setFirstName("Please set your name here");
                            user1.setLastName("Please set your surename");
                            user1.setEmail("Please set your email here");
                            user1.setPhone("Please set your phone here, 9 digits");
                            registerDatesAndConfirm(user1);
                            Telegram.sendMessage("Registered 1st user.");
                            log.info("Registered 1st user.");
                        }
                        if (secondUserRegistered == false && firstUserRegistered == true) {
                            UserDataObywatel user2 = new UserDataObywatel();
                            user2.setFirstName("Please set your name here");
                            user2.setLastName("Please set your surename");
                            user2.setEmail("Please set your email here");
                            user2.setPhone("Please set your phone here, 9 digits");
                            registerDatesAndConfirm(user2);
                            secondUserRegistered = true;
                            count = maxTries;
                            Telegram.sendMessage("Registered 2nd user.");
                            log.info("Registered 2nd user.");
                        }
                        firstUserRegistered = true;
                    } catch (Exception e) {
                        log.error("Failed to register user");
                        log.info("60 sec for you my friend to click ui manually.");
                        Thread.sleep(60000);
                    }
                    log.info("Sleeping for 10 mins now");
                    Thread.sleep(10 * 1000 * 60);
                }
                log.info("Loop ended. Retry now.");
//                throw new Exception("Retry now.");
            } catch (Exception e) {
                log.error("This shit has failed " + e);
                if (++count >= maxTries) {
                    log.info("Max tries reached");
                    throw e;
                }
            }
            if (++count == maxTries) {
                maxRetriesFlag = true;
                String MAX_TRIES_REACHED = "Max Tries reached.";
                log.info(MAX_TRIES_REACHED);
                Telegram.sendMessage(MAX_TRIES_REACHED);
            }
            log.info("Loop ended sleep 10 sec.");
            Thread.sleep(10000);
        }
    }

    private static void registerDatesAndConfirm(UserDataObywatel user) throws InterruptedException {
        SelenideElement firstDateTimeButton = $("[id^=timeButton]");
        ElementsCollection allTimeButtons = $$("[id^=timeButton]");
        if (firstDateTimeButton.isDisplayed()) {
            log.info("Time on the");
            int size = allTimeButtons.size();
            allTimeButtons.get(size/2).click();
            $("#FirstName").sendKeys(user.getFirstName());
            $("#LastName").sendKeys(user.getLastName());
            $("#Email").sendKeys(user.getEmail());
            $("#ConfirmEmail").sendKeys(user.getEmail());
            $("#phone").sendKeys(user.getPhone());
//            $("[role='checkbox']").click();
            $("#agreement").click();
            Thread.sleep(60000);
            $("[aria-label='Zamknij']]").scrollIntoView(true).click();
            $("#createAppointmentButton").shouldBe(Condition.enabled, Duration.ofMillis(300));
            $("#createAppointmentButton").click();
            Thread.sleep(2000);
            Selenide.screenshot("it_registered_" + System.currentTimeMillis());
            Thread.sleep(1000 * 10 * 60);
        }

    }

}
