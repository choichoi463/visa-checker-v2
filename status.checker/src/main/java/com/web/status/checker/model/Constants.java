package com.web.status.checker.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    private final String VISA_BEL_URL = "https://visa.vfsglobal.com/blr/ru/pol/book-an-appointment";
    public final String ACCESS_GRANTED_MESSAGE = "Bro, Success!!! ACCESS GRANTED! ^_______________^ at";
    public final String ACCESS_DENIED_MESSAGE = "Bro, Access Denied -_-";
    public final String GO_TO_URL_MESSAGE = "Bro, Скорее проверяй окно по ссылке " + VISA_BEL_URL;
    public final String EXCEPTION_MESSAGE = "Bro, Exception caught block ";
    public final String PLS_RESTART_MESSAGE = "Bro, X_X test has stopped, pls restart. ";
    public final String EXCEPTION_IN_APP = "Bro, app error - exception. ";

}
