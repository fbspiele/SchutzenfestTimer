package com.fbspiele.schutzenfesttimer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


public class myQuickSettingsTile extends TileService {
    final static String tag = "myQuickSettingsTile";


    @Override
    public void onClick() {
        super.onClick();
        tryMakeTileActive();
        MyCalendar nextSchuuuCalendar = getNextSchuuuCalendar();
        nextSchuuuCalendar.update();
        setClipboardText("#"+nextSchuuuCalendar.getSchlafeAbstand(getBaseContext()));
        Toast.makeText(getBaseContext(), nextSchuuuCalendar.getName() + "\n" + nextSchuuuCalendar.getNochSchlafenText(getBaseContext(),false),Toast.LENGTH_LONG).show();
    }

    MyCalendar getNextSchuuuCalendar(){
        List<MyCalendar> myCalendarList = MainActivity.loadMyCalendarList(getBaseContext());
        MyCalendar returnCalendar = null;
        for(MyCalendar calendar : myCalendarList){
            //check if schu calendar
            if(calendar.isSchuCalendar){

                //check if schü noch nicht gewesen (oder besser gesagt noch nicht vorbei (kann ja sein dass es grad is))
                if(calendar.calendarEnde.getTimeInMillis()> Calendar.getInstance().getTimeInMillis()){
                    //wenn s der erst calendar is auf den das zupasst dann ist der der kandidat
                    if(returnCalendar==null){
                        returnCalendar = calendar;
                    }
                    //wenn ned vergleich ich welcher näher dran is also früher is
                    else {
                        if(calendar.calendarAnfang.getTimeInMillis() <  returnCalendar.calendarAnfang.getTimeInMillis()){
                            returnCalendar = calendar;
                        }
                    }
                }
            }
        }
        return returnCalendar;
    }

    @Override
    public void onCreate() {
        Log.v(tag, "onCreate()");
        super.onCreate();
        tryMakeTileActive();
    }

    void tryMakeTileActive(){
        Tile tile = getQsTile();
        if(tile == null){
            Log.w(tag, "tile == null");
        }
        else {
            tile.setState(Tile.STATE_ACTIVE);
        }
    }

    void setClipboardText(String text){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("schütimer noch tage", text);

        if(clipboardManager!=null){
            clipboardManager.setPrimaryClip(clip);
        }
        else{
            Log.v(tag,"setClipboardText clipboardManager == null");
        }
    }
}
