package com.fbspiele.schutzenfesttimer;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

class MyBenachrichtigung {
    final static private String tag = "MyBenachrichtigung";

    int benachrichtigungsHourOfDay;
    int benachrichtigungsMin;
    String name;

    boolean inZukunftBenachrichtigen; // wenn das event noch ned is und benachrichtigt werden soll (also in 10 tagen is schüüü)
    boolean inVergangenheitBenachrichtigen; // wenn das event schon war und benachrichtigt werden soll (also vor 10 tagen is schüüü)
    boolean wennPresentBenachrichtigen; // wenn jetzt das event is und benachrichtigt werden solle (alo heute is schüüü)
    List<JedenWennKleinerGleich> benachrichtigungsConditionen;
    int benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_ERROR;

    long id;

    MyBenachrichtigung(String name, int benachrichtigungsTyp, boolean inZukunftBenachrichtigen, boolean wennPresentBenachrichtigen, boolean inVergangenheitBenachrichtigen, int hourOfDay, int min, List<JedenWennKleinerGleich> benachrichtigungsConditionen){
        this.name = name;
        this.benachrichtigungsTyp = benachrichtigungsTyp;
        this.inZukunftBenachrichtigen = inZukunftBenachrichtigen;
        this.inVergangenheitBenachrichtigen = inVergangenheitBenachrichtigen;
        this.wennPresentBenachrichtigen = wennPresentBenachrichtigen;
        this.benachrichtigungsConditionen = benachrichtigungsConditionen;
        benachrichtigungsHourOfDay = hourOfDay;
        benachrichtigungsMin = min;
        id = getNewId();
    }

    String getVergleichString(){
        //damit ich checken kann ob zwei benachrichtigungen gleich sind (außer die id)
        String string = "";
        string += "name"+ name;
        string += "benachrichtigungsHourOfDay"+ benachrichtigungsHourOfDay;
        string += "benachrichtigungsMin"+ benachrichtigungsMin;
        string += "inZukunftBenachrichtigen"+ inZukunftBenachrichtigen;
        string += "wennPresentBenachrichtigen"+ wennPresentBenachrichtigen;
        string += "inVergangenheitBenachrichtigen"+ inVergangenheitBenachrichtigen;
        string += "benachrichtigungsTyp"+ benachrichtigungsTyp;
        StringBuilder stringBuilder = new StringBuilder();
        if(benachrichtigungsConditionen != null){
            for (int i = 0; i < benachrichtigungsConditionen.size(); i++){
                stringBuilder.append(benachrichtigungsConditionen.get(i).getVergleichString());
            }
        }
        else{
            stringBuilder.append("null");
        }
        string += "benachrichtigungsConditionen"+ stringBuilder.toString();

        return string;
    }

    private long getNewId(){
        //VERÄNDERN WENN KOPIERT
        int identifier = 2;     //3 stellige nummer die sagt von was es die id is -> hier MyBenachrichtigung



        int identifierStellen = 3;
        int identifierShift = (int) Math.pow(10.0,(double) identifierStellen);
        // gibt die id (jetztinmills und dann ne 6 stellige random nummer)
        // damit wenn ich zb die schüüü calendar erstelle und die vielleicht in der selben millisec erstelle ne unique id bekommen
        long temp = Calendar.getInstance().getTimeInMillis();
        int randomRange = 1000*1000;
        int random = new Random().nextInt(randomRange);
        return temp * randomRange * identifierShift + random*identifierShift + identifier;
    }

    List<BenachrichtigungsZeitpunktMitInfo> getBenachrichtigungszeitpunkte(Calendar eventAnfang, Calendar eventEnde, long calendarId){

        List<BenachrichtigungsZeitpunktMitInfo> benachrichtigungsZeitpunktList = new ArrayList<>();
        Calendar benachrichtigungsZeitpunkt;
        switch (benachrichtigungsTyp){
            case MainActivity.NOTIFICATION_TYPE_TAGE:{

                if(inZukunftBenachrichtigen){
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(int tageVor =1; tageVor<=condition.wennKleinerGleich;tageVor++){
                            if(tageVor%condition.jeden == 0){
                                benachrichtigungsZeitpunkt = Calendar.getInstance();
                                benachrichtigungsZeitpunkt.set(Calendar.MILLISECOND, 0);
                                benachrichtigungsZeitpunkt.set(Calendar.SECOND, 0);
                                benachrichtigungsZeitpunkt.set(Calendar.MINUTE,benachrichtigungsMin);
                                benachrichtigungsZeitpunkt.set(Calendar.HOUR_OF_DAY,benachrichtigungsHourOfDay);
                                benachrichtigungsZeitpunkt.set(Calendar.DAY_OF_MONTH,eventAnfang.get(Calendar.DAY_OF_MONTH));
                                benachrichtigungsZeitpunkt.set(Calendar.MONTH,eventAnfang.get(Calendar.MONTH));
                                benachrichtigungsZeitpunkt.set(Calendar.YEAR,eventAnfang.get(Calendar.YEAR));
                                benachrichtigungsZeitpunkt.add(Calendar.DATE,-tageVor);
                                BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                                zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;

                                zeitpunktMitInfo.benachrichtigungsId = id;
                                zeitpunktMitInfo.calendarId = calendarId;
                                zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_TAGE;
                                zeitpunktMitInfo.benachrichtigungsTypValue = tageVor;
                                benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                            }
                        }
                    }
                }

                if(wennPresentBenachrichtigen){
                    //damit die die selben millis haben neue calendars
                    Calendar anfang = Calendar.getInstance();
                    Calendar ende = Calendar.getInstance();
                    anfang.clear();
                    ende.clear();
                    anfang.set(eventAnfang.get(Calendar.YEAR), eventAnfang.get(Calendar.MONTH),eventAnfang.get(Calendar.DATE));
                    ende.set(eventEnde.get(Calendar.YEAR), eventEnde.get(Calendar.MONTH),eventEnde.get(Calendar.DATE));

                    long difference = ende.getTimeInMillis() - anfang.getTimeInMillis();
                    double dauerEinesTagesInMillis = 1000*60*60*24;
                    double dauerInTagen = (difference)/(dauerEinesTagesInMillis);
                    int tage = (int) dauerInTagen;
                    //check if
                    if((double) tage != dauerInTagen){
                        Log.w(tag,"getBenachrichtigungszeitpunkte -> wennPresentBenachrichtigen: tage sind nicht ganzzahl da muss was verkehr gelaufen sein");
                    }
                    //Log.v(tag, "dauerInTagen " + dauerInTagen);
                    for(int i = 0; i<= tage; i++){
                        benachrichtigungsZeitpunkt = Calendar.getInstance();
                        benachrichtigungsZeitpunkt.set(Calendar.MILLISECOND, 0);
                        benachrichtigungsZeitpunkt.set(Calendar.SECOND, 0);
                        benachrichtigungsZeitpunkt.set(Calendar.MINUTE,benachrichtigungsMin);
                        benachrichtigungsZeitpunkt.set(Calendar.HOUR_OF_DAY,benachrichtigungsHourOfDay);
                        benachrichtigungsZeitpunkt.set(Calendar.DAY_OF_MONTH,eventAnfang.get(Calendar.DAY_OF_MONTH));
                        benachrichtigungsZeitpunkt.set(Calendar.MONTH,eventAnfang.get(Calendar.MONTH));
                        benachrichtigungsZeitpunkt.set(Calendar.YEAR,eventAnfang.get(Calendar.YEAR));


                        benachrichtigungsZeitpunkt.add(Calendar.DATE,i);
                        BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                        zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                        zeitpunktMitInfo.benachrichtigungsId = id;
                        zeitpunktMitInfo.calendarId = calendarId;
                        zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_TAGE;
                        zeitpunktMitInfo.benachrichtigungsTypValue = 0;
                        benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                    }
                }

                if(inVergangenheitBenachrichtigen){
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(int tageNach =1; tageNach<=condition.wennKleinerGleich;tageNach++){
                            if(tageNach%condition.jeden == 0){
                                benachrichtigungsZeitpunkt = Calendar.getInstance();
                                benachrichtigungsZeitpunkt.set(Calendar.MILLISECOND, 0);
                                benachrichtigungsZeitpunkt.set(Calendar.SECOND, 0);
                                benachrichtigungsZeitpunkt.set(Calendar.MINUTE,benachrichtigungsMin);
                                benachrichtigungsZeitpunkt.set(Calendar.HOUR_OF_DAY,benachrichtigungsHourOfDay);
                                benachrichtigungsZeitpunkt.set(Calendar.DAY_OF_MONTH,eventEnde.get(Calendar.DAY_OF_MONTH));
                                benachrichtigungsZeitpunkt.set(Calendar.MONTH,eventEnde.get(Calendar.MONTH));
                                benachrichtigungsZeitpunkt.set(Calendar.YEAR,eventEnde.get(Calendar.YEAR));
                                benachrichtigungsZeitpunkt.add(Calendar.DATE,tageNach);
                                BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                                zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                                zeitpunktMitInfo.benachrichtigungsId = id;
                                zeitpunktMitInfo.calendarId = calendarId;
                                zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_TAGE;
                                zeitpunktMitInfo.benachrichtigungsTypValue = -tageNach;
                                benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                            }
                        }
                    }
                }
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_STUNDEN:{


                if(inZukunftBenachrichtigen) {
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long stunden = condition.jeden; stunden<=condition.wennKleinerGleich;stunden += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            benachrichtigungsZeitpunkt.setTimeInMillis(eventAnfang.getTimeInMillis()-stunden*3600*1000);;
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_STUNDEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = -stunden;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                if(inVergangenheitBenachrichtigen){
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long stunden = condition.jeden; stunden<=condition.wennKleinerGleich;stunden += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_STUNDEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = stunden;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_MINUTEN:{

                if(inZukunftBenachrichtigen) {
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long minuten = condition.jeden; minuten<=condition.wennKleinerGleich;minuten += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            benachrichtigungsZeitpunkt.setTimeInMillis(eventAnfang.getTimeInMillis()-minuten*60*1000);;
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_MINUTEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = minuten;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                if(inVergangenheitBenachrichtigen){
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long minuten = condition.jeden; minuten<=condition.wennKleinerGleich;minuten += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            benachrichtigungsZeitpunkt.setTimeInMillis(eventEnde.getTimeInMillis()+minuten*60*1000);;
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_MINUTEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = -minuten;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_SEKUNDEN:{

                if(inZukunftBenachrichtigen) {
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long sekunden = condition.jeden; sekunden<=condition.wennKleinerGleich;sekunden += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            benachrichtigungsZeitpunkt.setTimeInMillis(eventAnfang.getTimeInMillis()-sekunden*1000);;
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_SEKUNDEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = sekunden;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                if(inVergangenheitBenachrichtigen){
                    for(int condIndex = 0; condIndex<benachrichtigungsConditionen.size(); condIndex++){
                        JedenWennKleinerGleich condition = benachrichtigungsConditionen.get(condIndex);
                        for(long sekunden = condition.jeden; sekunden<=condition.wennKleinerGleich;sekunden += condition.jeden){
                            benachrichtigungsZeitpunkt = Calendar.getInstance();
                            benachrichtigungsZeitpunkt.setTimeInMillis(eventEnde.getTimeInMillis()+sekunden*1000);;
                            BenachrichtigungsZeitpunktMitInfo zeitpunktMitInfo = new BenachrichtigungsZeitpunktMitInfo();
                            zeitpunktMitInfo.zeitpunkt = benachrichtigungsZeitpunkt;
                            zeitpunktMitInfo.benachrichtigungsId = id;
                            zeitpunktMitInfo.calendarId = calendarId;
                            zeitpunktMitInfo.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_SEKUNDEN;
                            zeitpunktMitInfo.benachrichtigungsTypValue = -sekunden;
                            benachrichtigungsZeitpunktList.add(zeitpunktMitInfo);
                        }
                    }
                }
                break;
            }
        }
        //Log.v(tag, "benachrichtigungsZeitpunktList.size() "+benachrichtigungsZeitpunktList.size());
        return benachrichtigungsZeitpunktList;
    }


    static class BenachrichtigungsZeitpunktMitInfo{
        Calendar zeitpunkt;
        int benachrichtigungsTyp;
        long benachrichtigungsId;
        long calendarId;
        long benachrichtigungsTypValue; // wenn noch tage dann die anzahl der tage, wenn noch sekunden dann die anzahl der sekunden, ...
        String benachrichtigungsName;

        String myToString(){
            String calendarName = "calendarNameNichtGefunden";
            for (MyCalendar calendar : MainActivity.myCalendarList){
                if(calendar.id == calendarId){
                    calendarName = calendar.getName();
                }
            }

            return calendarName + " wird am " + MainActivity.getSchonesDatumPlusZeit(zeitpunkt) + " benachrichtigt (benachrichtigungsName " + benachrichtigungsName +  ", benachrichtigungsTyp " + benachrichtigungsTyp+", benachrichtigungsTypValue "+benachrichtigungsTypValue+")";
        }
    }


    String getBenachrichtigungsZeit(){
        String returnString;
        if(benachrichtigungsHourOfDay<10){
            returnString = "0"+benachrichtigungsHourOfDay;
        }
        else {
            returnString = ""+benachrichtigungsHourOfDay;
        }
        returnString = returnString + ":";
        if(benachrichtigungsMin<10){
            returnString = returnString+"0"+benachrichtigungsMin;
        }
        else {
            returnString = returnString+""+benachrichtigungsMin;
        }
        return returnString;
    }



    static class JedenWennKleinerGleich{
        long jeden;
        long wennKleinerGleich;
        long id;
        JedenWennKleinerGleich(long jedenSoVielten, long wennKleinerGleichSovielEntfernt){
            id = getNewId();
            jeden = jedenSoVielten;
            wennKleinerGleich = wennKleinerGleichSovielEntfernt;
        }

        JedenWennKleinerGleich(){
            id = getNewId();
        }




        String getVergleichString() {
            //damit ich checken kann ob zwei JedenWennKleinerGleich gleich sind (außer die id)
            String string = "";
            string += "jeden" + jeden;
            string += "wennKleinerGleich" + wennKleinerGleich;
            return string;
        }

        private long getNewId(){
            //VERÄNDERN WENN KOPIERT
            int identifier = 3;     //3 stellige nummer die sagt von was es die id is -> hier JedenWennKleinerGleich



            int identifierStellen = 3;
            int identifierShift = (int) Math.pow(10.0,(double) identifierStellen);
            // gibt die id (jetztinmills und dann ne 6 stellige random nummer)
            // damit wenn ich zb die schüüü calendar erstelle und die vielleicht in der selben millisec erstelle ne unique id bekommen
            long temp = Calendar.getInstance().getTimeInMillis();
            int randomRange = 1000*1000;
            int random = new Random().nextInt(randomRange);
            return temp * randomRange * identifierShift + random*identifierShift + identifier;
        }
    }
}


