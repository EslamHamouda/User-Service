package com.ecommerce.userservice.utils;

import java.time.LocalDateTime;

public final class DateUtils {
    private DateUtils(){}
    public static LocalDateTime currentLocalDateTime(){
        return LocalDateTime.now();
    }
}
