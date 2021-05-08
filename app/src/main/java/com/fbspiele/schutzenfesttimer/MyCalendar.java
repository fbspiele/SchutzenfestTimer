package com.fbspiele.schutzenfesttimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static java.util.Calendar.YEAR;

public class MyCalendar {

    private final static String tag = "MyCalendar";


    boolean standardBenachrichtigungenFurDiesesEventVerwenden = true;
    List<MyBenachrichtigung> eventBenachrichtigungen;



    Calendar calendarAnfang;
    Calendar calendarEnde;
    private String name = "";

    long id;
    private static final int INITIALYEARNULLVALUE = -1546484812;
    int initialYear = INITIALYEARNULLVALUE;
    boolean isSchuCalendar = false;

    MyCalendar(int year){
        initialYear = year;
        setId();
    }

    MyCalendar(long id, int year){
        initialYear = year;
        setId(id);
    }

    void addBenachrichtigung(MyBenachrichtigung benachrichtigung){
        if(eventBenachrichtigungen==null){
            eventBenachrichtigungen = new ArrayList<>();
            eventBenachrichtigungen.add(benachrichtigung);
        }
        else {
            eventBenachrichtigungen.add(benachrichtigung);
        }
    }

    void replaceBenachrichtigung(MyBenachrichtigung newBenachrichtigung){
        int indexBenachrichtigungToReplace = -1;
        for(int i = 0; i<eventBenachrichtigungen.size();i++){
            if(eventBenachrichtigungen.get(i).id==newBenachrichtigung.id){
                indexBenachrichtigungToReplace = i;
            }
        }
        if(indexBenachrichtigungToReplace!=-1){
            eventBenachrichtigungen.remove(indexBenachrichtigungToReplace);
            eventBenachrichtigungen.add(indexBenachrichtigungToReplace,newBenachrichtigung);
        }
        else {
            Log.w(tag, "replaceBenachrichtiung benachrichtigung die replaced werden soll nicht gefunden");
        }
    }

    void deleteBenachrichtigung(long id){
        int indexBenachrichtigungToReplace = -1;
        for(int i = 0; i<eventBenachrichtigungen.size();i++){
            if(eventBenachrichtigungen.get(i).id==id){
                indexBenachrichtigungToReplace = i;
            }
        }
        if(indexBenachrichtigungToReplace!=-1){
            eventBenachrichtigungen.remove(indexBenachrichtigungToReplace);
        }
        else {
            Log.w(tag, "deleteBenachrichtiung benachrichtigung die gelöscht werden soll nicht gefunden");
        }
    }

    MyCalendar(Calendar calendarAnfang, Calendar calendarEnde, String eventName){
        setId();
        standardBenachrichtigungenFurDiesesEventVerwenden = true;
        updateAnfang(calendarAnfang);
        updateEnde(calendarEnde);
        updateName(eventName);
    }

    private void setId(long Id){
        id = Id;
    }

    private long getNewId(){
        //VERÄNDERN WENN KOPIERT
        int identifier = 1;     //3 stellige nummer die sagt von was es die id is -> hier MyCalendar

        int identifierStellen = 3;
        int identifierShift = (int) Math.pow(10.0,(double) identifierStellen);
        // gibt die id (jetztinmills und dann ne 6 stellige random nummer)
        // damit wenn ich zb die schüüü calendar erstelle und die vielleicht in der selben millisec erstelle ne unique id bekommen
        long temp = Calendar.getInstance().getTimeInMillis();
        int randomRange = 1000*1000;
        int random = new Random().nextInt(randomRange);
        return temp * randomRange * identifierShift + random*identifierShift + identifier;
    }

    private void setId(){
        id = getNewId();
    }

    public String getName() {
        return name;
    }

    List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> getListBenachrichtigungsZeitpunktMitInfos(){
        List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> returnList = new ArrayList<>();
        if(eventBenachrichtigungen!=null){
            //Log.v(tag, "eventBenachrichtigungen.size() "+eventBenachrichtigungen.size());
            for(int i = 0; i<eventBenachrichtigungen.size();i++){
                MyBenachrichtigung benachrichtigung = eventBenachrichtigungen.get(i);
                List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> benachrichtigung_Zeitpunkte = benachrichtigung.getBenachrichtigungszeitpunkte(calendarAnfang,calendarEnde, id);
                for(MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo eintragInBenachrichtigung_Zeitpunkte : benachrichtigung_Zeitpunkte){
                    eintragInBenachrichtigung_Zeitpunkte.benachrichtigungsName = benachrichtigung.name;
                    boolean anDemZeitpunktWirdDiesesEventSchonBenachrichtigt = false;
                    for(MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo eintragInreturnList : returnList){
                        if (eintragInBenachrichtigung_Zeitpunkte.zeitpunkt.getTimeInMillis() == eintragInreturnList.zeitpunkt.getTimeInMillis()) {
                            anDemZeitpunktWirdDiesesEventSchonBenachrichtigt = true;
                            break;
                        }
                    }
                    if(!anDemZeitpunktWirdDiesesEventSchonBenachrichtigt){
                        returnList.add(eintragInBenachrichtigung_Zeitpunkte);
                    }
                }
            }
        }
        if(standardBenachrichtigungenFurDiesesEventVerwenden){
            if(MainActivity.myStandardBenachrichtigungList!=null){
                //Log.v(tag, "MainActivity.myStandardBenachrichtigungList.size() "+MainActivity.myStandardBenachrichtigungList.size());
                for(int i = 0; i<MainActivity.myStandardBenachrichtigungList.size(); i++){
                    MyBenachrichtigung benachrichtigung = MainActivity.myStandardBenachrichtigungList.get(i);
                    List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> benachrichtigung_Zeitpunkte = benachrichtigung.getBenachrichtigungszeitpunkte(calendarAnfang,calendarEnde, id);

                    for(MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo eintragInBenachrichtigung_Zeitpunkte : benachrichtigung_Zeitpunkte){
                        eintragInBenachrichtigung_Zeitpunkte.benachrichtigungsName = benachrichtigung.name;
                        boolean anDemZeitpunktWirdDiesesEventSchonBenachrichtigt = false;
                        for(MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo eintragInreturnList : returnList){
                            if (eintragInBenachrichtigung_Zeitpunkte.zeitpunkt.getTimeInMillis() == eintragInreturnList.zeitpunkt.getTimeInMillis()) {
                                //Log.v(tag, "temp wird schon benachrichtigt");
                                anDemZeitpunktWirdDiesesEventSchonBenachrichtigt = true;
                                break;
                            }
                        }
                        if(!anDemZeitpunktWirdDiesesEventSchonBenachrichtigt){
                            returnList.add(eintragInBenachrichtigung_Zeitpunkte);
                        }
                    }
                }
            }
        }
        return returnList;
    }

    void updateAnfang(Calendar calendarAnfang){
        if(initialYear == INITIALYEARNULLVALUE){
            initialYear = calendarAnfang.get(YEAR);
        }
        this.calendarAnfang = calendarAnfang;
    }
    void updateEnde(Calendar calendarEnde){
        this.calendarEnde = calendarEnde;
    }
    void updateName(String eventName){
        this.name = eventName;
    }

    private int sekundenBisBeginn;
    private int sekundenSeitEnde;

    private int inZukunftJetztVergangenheit; //zukunft = 1, jetzt = 0; vergangenheit = -1;

    public void update(){


        Calendar jetzt = Calendar.getInstance();
        long gesMillisBeginn = 0;
        if(calendarAnfang==null){
            Log.e("MyCalendar","calendarAnfang==null bei update ("+name+")");
        }
        else{
            gesMillisBeginn = calendarAnfang.getTimeInMillis();
        }
        long gesMillisEnde = 0;
        if(calendarEnde==null){
            Log.e("MyCalendar","calendarEnde==null bei update ("+name+")");
        }
        else{
            gesMillisEnde = calendarEnde.getTimeInMillis();
        }
        long jetztMillis = jetzt.getTimeInMillis();


        sekundenBisBeginn = Math.toIntExact(Math.round((gesMillisBeginn - jetztMillis)/1000.0));
        sekundenSeitEnde = Math.toIntExact(Math.round((jetztMillis - gesMillisEnde)/1000.0));
        if(sekundenBisBeginn>0){
            inZukunftJetztVergangenheit = 1;
        }else if(sekundenSeitEnde>0){
            inZukunftJetztVergangenheit = -1;
        }
        else{
            inZukunftJetztVergangenheit = 0;
        }

    }





    public int getSchlafeAbstand(Context context){
        SharedPreferences sharedPreferences;
        try{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"sharedPrefs konnten in getSchlafeAbstand nicht geladen werden",Toast.LENGTH_LONG).show();
            return -99999;
        }

        int tagesWechselHourOfDay = sharedPreferences.getInt(context.getString(R.string.pref_key_nextDayTimeHourOfDay),5);
        int tagesWechselMin = sharedPreferences.getInt(context.getString(R.string.pref_key_nextDayTimeMin),0);
        int secOfDayTagesWechsel = tagesWechselHourOfDay * 60 * 60 + tagesWechselMin * 60;

        if(sekundenBisBeginn>0){
            int secOfDaySchuBeginn = calendarAnfang.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendarAnfang.get(Calendar.MINUTE) * 60 + calendarAnfang.get(Calendar.SECOND);

            int secZwischenLetztenZeitTagesWechselUndSchuBeginn = secOfDaySchuBeginn - secOfDayTagesWechsel;
            if(secZwischenLetztenZeitTagesWechselUndSchuBeginn<0){
                secZwischenLetztenZeitTagesWechselUndSchuBeginn += 24*60*60;//wenn zb tageswechsel 23 uhr und schübeginn 1 uhr -> -22 stunden -> +24 stunden -> 2 stunden
            }

            int secBisLetztesMalSchlafen = sekundenBisBeginn - secZwischenLetztenZeitTagesWechselUndSchuBeginn;
            double schlafeBisSchu = secBisLetztesMalSchlafen / ((double) (24 * 60 * 60));
            schlafeBisSchu = Math.floor(schlafeBisSchu) + 1;
            return (int) schlafeBisSchu;
        }else if(sekundenSeitEnde>0){
            int secOfDaySchuEnde = calendarEnde.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendarEnde.get(Calendar.MINUTE) * 60 + calendarEnde.get(Calendar.SECOND);
            int secZwischenSchuEndeUndTagesWechselDavor = secOfDayTagesWechsel - secOfDaySchuEnde;
            if(secZwischenSchuEndeUndTagesWechselDavor<0){
                secZwischenSchuEndeUndTagesWechselDavor += 24*60*60;//wenn zb schüende 23 uhr und tageswechsel 1 uhr und  -> -22 stunden -> +24 stunden -> 2 stunden
            }

            int secSeitErstesMalschlafen = sekundenSeitEnde - secZwischenSchuEndeUndTagesWechselDavor;
            double schlafeSeitSchu = secSeitErstesMalschlafen / ((double) (24 * 60 * 60));
            schlafeSeitSchu = Math.floor(schlafeSeitSchu) + 1;
            return (int) schlafeSeitSchu;
        }
        else{
            return 0;
        }
    }

    private String getStringSchlafeAbstand(Context context, boolean kannDerTextFettSein){
        if(kannDerTextFettSein){
            return "<b>" + getSchlafeAbstand(context) + "</b> ";
        }
        else{
            return ""+getSchlafeAbstand(context);
        }
    }


    public String getNochSchlafenText(Context context, boolean kannDerTextFettSein) {
        switch (inZukunftJetztVergangenheit){
            case 1:{
                if(getSchlafeAbstand(context) == 0){
                    return "is heute :)";
                }
                else{
                    return  "nur noch " +
                            getStringSchlafeAbstand(context, kannDerTextFettSein) +
                            " mal schlafen";
                }
            }
            case -1:{
                if(getSchlafeAbstand(context) == 0){
                    return "war heute";
                }
                else{
                    return "vor "+
                            getStringSchlafeAbstand(context, kannDerTextFettSein) +
                            " mal schlafen";
                }
            }
            case 0:{
                if(kannDerTextFettSein){
                    return "<b> is jetzt :) </b>";
                }
                else{
                    return "is jetzt :)";
                }
            }
            default: {
                Log.e(tag, "getNochSchlafenText() kein case getroffen");
                return "error";

            }
        }
    }

    int getSekundenBisAnfang(){
        return sekundenBisBeginn;
    }


    int getSekundenSeitEnde(){
        return sekundenSeitEnde;
    }



    public String getNochSekundenText(){
        switch (inZukunftJetztVergangenheit){
            case 1:{
                return "("+
                        intToSchonString(sekundenBisBeginn) +
                        " sekunden ≈ " + doubleToString((double) (sekundenBisBeginn)/(60*60*24),2) +
                        " tage)";
            }
            case -1:{
                return "("+
                        intToSchonString(sekundenSeitEnde) +
                        " sekunden ≈ " + doubleToString((double) (sekundenSeitEnde)/(60*60*24),2) +
                        " tage)";
            }
            default: {
                return "";
            }
        }
    }

    private String doubleToString(double zahl, int nachKommaStellen){
        String formatString = "%."+nachKommaStellen+"f";          //  %.2f heißt 2 nachkommastellen
        String ergebnis =  String.format (formatString, zahl);
        ergebnis = ergebnis.replace(".",",");
        return ergebnis;
    }

    String summaryText(){
        if(calendarAnfang==null || calendarEnde== null){
            Log.e("MyCalendar","am/zwischen text calendar == null ("+name+")");
            return "";
        }
        if(
                calendarAnfang.get(Calendar.YEAR)==calendarEnde.get(Calendar.YEAR)
                        &&calendarAnfang.get(Calendar.MONTH)==calendarEnde.get(Calendar.MONTH)
                        &&calendarAnfang.get(Calendar.DAY_OF_MONTH)==calendarEnde.get(Calendar.DAY_OF_MONTH)
        ){
            return getSchonesDatum(calendarAnfang);
        }
        else{
            return getSchonesDatum(calendarAnfang) + " - " + getSchonesDatum(calendarEnde);
        }
    }

    public String amZwischenText(){
        if(calendarAnfang==null || calendarEnde== null){
            Log.e("MyCalendar","am/zwischen text calendar == null ("+name+")");
            return "";
        }
        if(
                calendarAnfang.get(Calendar.YEAR)==calendarEnde.get(Calendar.YEAR)
                &&calendarAnfang.get(Calendar.MONTH)==calendarEnde.get(Calendar.MONTH)
                &&calendarAnfang.get(Calendar.DAY_OF_MONTH)==calendarEnde.get(Calendar.DAY_OF_MONTH)
        ){
            return getSchonesDatum(calendarAnfang);
        }
        else{
            return getSchonesDatum(calendarAnfang) + " - " + getSchonesDatum(calendarEnde);
        }
    }

    String getSchoneZeit(Calendar calendar){
        if(calendar==null){
            Log.e("MyCalendar","calendar==null bei getSchoneZeit");
            return "";
        }
        String string;
        if(calendar.get(Calendar.HOUR_OF_DAY)<10){
            string = "0"+ calendar.get(Calendar.HOUR_OF_DAY);
        }
        else{
            string = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }
        string = string + ":";
        if((calendar.get(Calendar.MINUTE))<10){
            string = string + "0"+ (calendar.get(Calendar.MINUTE));
        }
        else{
            string = string + (calendar.get(Calendar.MINUTE));
        }
        return string;

    }

    static String getSchonesDatum(Calendar calendar){
        if(calendar==null){
            Log.e("MyCalendar","calendar==null bei getSchonesDatum");
            return "";
        }
        String datumString;
        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            datumString = "0"+ calendar.get(Calendar.DAY_OF_MONTH);
        }
        else{
            datumString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }
        datumString = datumString + ".";
        if((calendar.get(Calendar.MONTH)+1)<10){
            datumString = datumString + "0"+ (calendar.get(Calendar.MONTH)+1);
        }
        else{
            datumString = datumString + (calendar.get(Calendar.MONTH)+1);
        }
        datumString = datumString + "." + calendar.get(Calendar.YEAR);
        return datumString;
    }

    private String intToSchonString(int integer){
        return longToSchonString((long) integer);
    }

    String longToSchonString(long longteger){
        String string = String.valueOf(Math.abs(longteger));
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        double anzahlPunkte = Math.floor((double) (string.length()-1)/3.0);
        for(int i = 1; i<=anzahlPunkte; i++){
            sb.insert((int) Math.round(string.length()-3.0*i),".");
        }
        string = sb.toString();
        if(longteger<0){
            string = "-"+string;
        }
        //Log.v("int to string",integer + "->"+string);
        return string;
    }





}
