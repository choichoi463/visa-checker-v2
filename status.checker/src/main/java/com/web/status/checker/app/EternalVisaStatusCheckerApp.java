package com.web.status.checker.app;

import com.codeborne.selenide.*;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
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
        sendMessage(message);
        log.info("Bro, telegram message sent " + message);
    }

    /**
     * Send message using telegram bot https://rieckpil.de/howto-send-telegram-bot-notifications-with-java/
     *
     * @param message
     */
    private void sendMessageTelegramBot(String message) {
        // Create your bot passing the token received from @BotFather
        TelegramBot bot = new TelegramBot("set yours auth key"); // set yours auth key

        // Register for updates
        bot.setUpdatesListener(updates -> {
            // ... process updates
            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        // Send messages
        long chatId = 342962942; //set yours chat id
        SendResponse response = bot.execute(new SendMessage(chatId, message));
    }

    /**
     * Get time now for the message
     *
     * @return
     */
    private String getDateTimeNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public void sendMessage(String message) {
        sendMessageTelegramBot(message + " " + getDateTimeNow());
    }

}
