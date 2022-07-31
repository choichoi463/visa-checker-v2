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
public class EternalCheckStatusTest {

    private static final String BASE_URL = "https://visa.vfsglobal.com/blr/ru/pol/book-an-appointment";
    private final String ACCESS_GRANTED_MESSAGE = "Success!!! ACCESS GRANTED! ^_______________^ at";
    private final String ACCESS_DENIED_MESSAGE = "Access Denied -_-";
    private final String GO_TO_URL_MESSAGE = ". Скорее проверяй окно по ссылке " + BASE_URL;
    private final String CRASHED_MESSAGE = "Program crashed T_T. ";

    @AfterAll
    public void sendMessage() {
        sendMessageTelegramBot("test has stopped, pls restart. " + " " + getDateTimeNow());
    }

    /**
     * This test checks if there is no "access denied" error register for a visa page is opened
     */
    @Test
    public void checkIfAccessGrantedAndSendMessageEternalTest() throws InterruptedException {

        while (true) {
            try {
                Configuration.browser = "firefox";
                Configuration.headless = true;
                //experimental feature:
                Configuration.savePageSource = true;

                ChromeOptions options = new ChromeOptions();
                options.setHeadless(true);

                Selenide.open(BASE_URL);
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
                    //this block is just to save page html to grep some html elemnts in future.
                    $(".just to fail").shouldBe(Condition.visible);
                } else {
                    if ($(byText("Access denied")).exists()) {
                        log.info(ACCESS_DENIED_MESSAGE + " " + getDateTimeNow());
                    }
                }
            } catch (Exception e) {
                log.error(CRASHED_MESSAGE + getDateTimeNow());
                log.error(e.getMessage());
                Selenide.screenshot("exception_" + System.currentTimeMillis());
                sendMessageTelegramBot(CRASHED_MESSAGE + " " + getDateTimeNow());
            } finally {
                Selenide.closeWebDriver();
                Thread.sleep(720000); //12 minutes 720000
            }
        }


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
}
