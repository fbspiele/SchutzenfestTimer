package com.fbspiele.schutzenfesttimer;

import android.util.Log;
import java.util.Calendar;


public class MySchuCalendar extends MyCalendar{

    private int schuBeginnHourOfDay = 18;
    private int schuBeginnMinute = 0;
    private int schuBeginnSecond = 0;
    private int schuEndeHourOfDay = 23;
    private int schuEndeMinute = 0;
    private int schuEndeSecond = 0;
    private int schuBeginnMillisecond = 0;




    public MySchuCalendar(int year){
        super(year);
        super.isSchuCalendar = true;
        resetSchuCalendar();
    }

    MySchuCalendar(long id, int year){
        super(id,year);
        super.isSchuCalendar = true;
        resetSchuCalendar();
    }

    private void resetSchuCalendar(){
        updateAnfang(getSchuBeginnCalendar(super.initialYear));
        updateEnde(getSchuEnde(super.initialYear));
        String schuuuuName = "schüüü "+super.initialYear;
        if (super.initialYear == 2020 || super.initialYear == 2021){
            schuuuuName = "kein " + schuuuuName + " :(";
        }
        updateName(schuuuuName);
        update();
    }

    private Calendar getSchuEnde(int year){
        Calendar schuEnde = getSchuBeginnCalendar(year);
        schuEnde.add(Calendar.DATE, 3);
        schuEnde.set(Calendar.HOUR_OF_DAY, schuEndeHourOfDay);
        schuEnde.set(Calendar.MINUTE, schuEndeMinute);
        schuEnde.set(Calendar.SECOND, schuEndeSecond);
        return schuEnde;
    }




    private int getOsterSonntagMonth(int year){
        switch(year) {
            case 2017:
            case 2020:
            case 2018:
            case 2019:
            case 2021:
            case 2022:
            case 2023:
            case 2025:
            case 2026:
            case 2028:
            case 2029:
            case 2030:
            case 2031:
            case 2033:
            case 2034:
            case 2036:
            case 2037: {
                return Calendar.APRIL;
            }
            case 2024:
            case 2035:
            case 2027:
            case 2032: {
                return Calendar.MARCH;
            }
            default:{
                Log.e("getOsterSonntag","calendar ostersonntag ist nicht zwischen 2017 und 2037 und da man ostern nur extrem scheiße bestimmen kann hab ich des nur für die jahre gemacht wo ich des datum nachschauen kann\nreturn -1");
                return -1;
            }
        }
        
    }

    private int getOsterSonntagDay(int year){
        switch(year) {
            case 2017:
            case 2028: {
                return 16;
            }
            case 2018:
            case 2029: {
                return 1;
            }
            case 2019:
            case 2030: {
                return 21;
            }
            case 2020: {
                return 12;
            }
            case 2021: {
                return 4;
            }
            case 2022:
            case 2033: {
                return 17;
            }
            case 2023:
            case 2034: {
                return 9;
            }
            case 2024: {
                return 31;
            }
            case 2025: {
                return 20;
            }
            case 2026:
            case 2037: {
                return 5;
            }
            case 2027:
            case 2032: {
                return 28;
            }
            case 2031:
            case 2036: {
                return 13;
            }
            case 2035: {
                return 25;
            }
            default:{
                Log.e("getosterSonntag","calendar ostersonntag ist nicht zwischen 2017 und 2037 und da man ostern nur extrem scheiße bestimmen kann hab ich des nur für die jahre gemacht wo ich des datum nachschauen kann\nreturn -1");
                return 0;
            }
        }

    }

    private Calendar getOsterSonntagCalendar(int year){
        Calendar calendar = Calendar.getInstance();
        if(getOsterSonntagDay(year)==-1||getOsterSonntagMonth(year)==-1){
            Log.e("MySchuCalendar","getOsterSonntagCalendar year falsch, returning null");
            return null;
        }

        calendar.set(year,getOsterSonntagMonth(year),getOsterSonntagDay(year),0,0,0);

        return calendar;
    }

    private Calendar getSchuBeginnCalendar(int year){
        Calendar schuBeginnCalendar;
        schuBeginnCalendar = getOsterSonntagCalendar(year);
        assert schuBeginnCalendar != null;
        schuBeginnCalendar.add(Calendar.DATE,7*7-1);
        schuBeginnCalendar.set(Calendar.HOUR_OF_DAY, schuBeginnHourOfDay);
        schuBeginnCalendar.set(Calendar.MINUTE, schuBeginnMinute);
        schuBeginnCalendar.set(Calendar.SECOND, schuBeginnSecond);
        schuBeginnCalendar.set(Calendar.MILLISECOND,schuBeginnMillisecond);
        return schuBeginnCalendar;
    }


}
