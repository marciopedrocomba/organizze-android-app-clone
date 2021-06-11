package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String actualDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sf.format(date);
        return dateString;
    }

    public static String monthYearChosen(String data) {

        String[] dateArray = data.split("/");
        String day = dateArray[0];
        String month = dateArray[1];
        String year = dateArray[2];
        String monthYear = month.concat(year);

        return monthYear;

    }

}
