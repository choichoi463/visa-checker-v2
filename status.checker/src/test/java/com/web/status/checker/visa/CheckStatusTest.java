package com.web.status.checker.visa;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckStatusTest {

    private final String ACCESS_GRANTED_MESSAGE = "Success!!! ACCESS GRANTED! ^_______________^ at";
    private final String ACCESS_DENIED_MESSAGE = "Access Denied -_-";
    private final String GO_TO_URL_MESSAGE = ". Скорее проверяй окно по ссылке https://visa.vfsglobal.com/blr/ru/pol/book-an-appointment";

    @AfterAll
    public void sendMessage() {
        sendMessageTelegramBot("test crashed , pls restart. " + " " + getDateTimeNow());
    }

    /**
     * This test checks if there is no "access denied" error register for a visa page is opened
     */
    @Test
    public void checkIfAccessGrantedAndSendMessageTest() throws InterruptedException {
        Configuration.browser = "firefox";
        Configuration.headless = true;

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);

        Selenide.open("https://visa.vfsglobal.com/blr/ru/pol/book-an-appointment");
        $(".lets-get-started").shouldBe(Condition.visible);
        $(".lets-get-started").scrollIntoView(true);
        $(".lets-get-started").click();
        switchTo().window(1);
        Thread.sleep(2500);
        if (!$(byText("Access denied")).exists()) {
            log.info(ACCESS_GRANTED_MESSAGE + " " + getDateTimeNow());
            System.out.println(ACCESS_GRANTED_MESSAGE + " " + getDateTimeNow());
            sendMessageTelegramBot(ACCESS_GRANTED_MESSAGE + " " + getDateTimeNow() + GO_TO_URL_MESSAGE);
            Selenide.screenshot("it_worked_" + System.currentTimeMillis());
        } else {
            if ($(byText("Access denied")).exists()) {
                log.info(ACCESS_DENIED_MESSAGE + " " + getDateTimeNow());
                //below code is commented - can be used for debug.
                // sendMessageTelegramBot(ACCESS_DENIED_MESSAGE + " " + getDateTimeNow());
                // Selenide.screenshot("it_did_not_work_" + System.currentTimeMillis());
            } else {
                log.info("ниче не работает. " + getDateTimeNow());
                Selenide.screenshot("nothing_is_working_T_T_" + System.currentTimeMillis());
            }
        }
        goForLoop();
    }

    /**
     * Send message using telegram bot https://rieckpil.de/howto-send-telegram-bot-notifications-with-java/
     *
     * @param message
     */
    private void sendMessageTelegramBot(String message) {
        // Create your bot passing the token received from @BotFather
        TelegramBot bot = new TelegramBot("5445144170:AAH-zbHLAbGRhyF0IptPyJH1JhfKJ5xDvTw");

        // Register for updates
        bot.setUpdatesListener(updates -> {
            // ... process updates
            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        // Send messages
        long chatId = 342962942;
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

    /**
     * Loops the test with interval each 12 minutes
     * @throws InterruptedException
     */
    private void goForLoop() throws InterruptedException {
        Selenide.closeWebDriver();
        Thread.sleep(720000); //12 minutes 720000
        checkIfAccessGrantedAndSendMessageTest();
    }
}
