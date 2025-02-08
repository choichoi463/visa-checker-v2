package com.web.status.checker.app;

import com.codeborne.selenide.*;
import com.web.status.checker.helpers.Telegram;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.web.status.checker.helpers.Telegram.getDateTimeNow;
import static com.web.status.checker.helpers.Telegram.sendMessageTelegramBot;
import static com.web.status.checker.model.Constants.*;

@Log4j2
public class EternalVisaStatusCheckerApp {

    private final String BASE_URL = "https://visa.vfsglobal.com/blr/ru/pol/book-an-appointment";

    public EternalVisaStatusCheckerApp() throws InterruptedException {
        int i = 1;
        Configuration.browser = "firefox";
        Configuration.headless = true;
        //experimental feature:
        Configuration.savePageSource = true;

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        while (true) {
            try {
                log.info("Bro, starting test run #" + i);
                Selenide.open(BASE_URL);
                log.info("Bro, base url opened.");
                Thread.sleep(10000);
                SelenideElement registerButton = $(".lets-get-started");
                registerButton.shouldBe(Condition.visible);
                registerButton.scrollIntoView(true);
                registerButton.click();
                switchTo().window(1);
                Thread.sleep(2500);
                log.info("Bro, checking registration page now:");
                if (!$(byText("Access denied")).exists()) {
                    log.warn("Bro," + ACCESS_GRANTED_MESSAGE + " " + getDateTimeNow() + GO_TO_URL_MESSAGE);
                    sendMessageTelegramBot(ACCESS_GRANTED_MESSAGE + " " + getDateTimeNow() + GO_TO_URL_MESSAGE);
                    Selenide.screenshot("it_worked_" + System.currentTimeMillis());

                    //this block is just to save page html to grep some html elements in future.
                    log.warn("Bro, Catching now the assertion failure to save the html page");
                    $(".just to fail").shouldBe(Condition.visible);
                } else {
                    if ($(byText("Access denied")).exists()) {
                        log.info("Bro," + ACCESS_DENIED_MESSAGE + " " + getDateTimeNow());
                    }
                }
            } catch (Exception e) {
                log.error("Bro, " + EXCEPTION_MESSAGE + getDateTimeNow());
                log.error("Bro, " + e.getMessage());
                Selenide.screenshot("exception_" + System.currentTimeMillis());
                sendMessageTelegramBot(EXCEPTION_MESSAGE + " " + getDateTimeNow() + " " + e.getLocalizedMessage());
            } finally {
                log.info("Bro, checking if webdriver is still started before closing.");
                if (WebDriverRunner.hasWebDriverStarted()) {
                    log.info("Bro, closing webdriver.");
                    WebDriverRunner.closeWebDriver();
                    log.info("Bro, webdriver closed.");
                }
                log.info("Bro, going to sleep long time now z.z.z.z test run#" + i);
                Thread.sleep(720000); //12 minutes is 720000 millis
                log.info("Bro, timeout between restarts passed.");
                i++;
            }
        }
    }

    public EternalVisaStatusCheckerApp(String message) {
        Telegram.sendMessage(message);
        log.info("Bro, telegram message sent " + message);
    }

}
