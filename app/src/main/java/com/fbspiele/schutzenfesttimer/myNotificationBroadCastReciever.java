package com.fbspiele.schutzenfesttimer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class myNotificationBroadCastReciever extends BroadcastReceiver {
    final static String tag =  "myNotificationBroadCastReciever";
    static int zuPostendeIntents = 7;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("myNotificationBroadCastReciever","onReceive");
        MainActivity.loadEverything(context);


        makeNotificationIntent = new Intent(context, myNotificationBroadCastReciever.class);
        makeNotificationIntent.setAction(intentActionMakeNotifications);
        Log.v("intent","intent "+intent.toString()+"\naction "+intent.getAction());
        if(Objects.equals(intent.getAction(), intentActionCancelNotifications)){
            cancelNotificationIntents(context);
            NotificationManagerCompat.from(context).cancelAll();
        }
        else if(Objects.requireNonNull(intent.getAction()).contains(intentActionMakeNotifications)){
            //intent um notification zu posten

            long calendarId = intent.getLongExtra(context.getResources().getString(R.string.intentExtraName_CalendarId),0);
            long benachrichtigungsId = intent.getLongExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsId),0);
            int benachrichtigungsTyp = intent.getIntExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsTyp),0);
            long benachrichtigungsTypValue = intent.getLongExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsTypValue),0);
            if(calendarId == 0 || benachrichtigungsId == 0 || benachrichtigungsTyp == 0){
                Log.w(tag, "getExtra hat nicht ganz funktioniert, siehe die folgenden warnungen");
                Log.w(tag, "calendarId = "+ calendarId);
                Log.w(tag, "benachrichtigungsId = "+ benachrichtigungsId);
                Log.w(tag, "benachrichtigungsTyp = "+ benachrichtigungsTyp);
                Log.w(tag, "benachrichtigungsTypValue = "+ benachrichtigungsTypValue);
            }
            else{
                MyCalendar myCalendar = null;

                for(int i = 0; i<MainActivity.myAngezeigteCalendarList.size(); i++){
                    if(calendarId == MainActivity.myAngezeigteCalendarList.get(i).id){
                        myCalendar = MainActivity.myAngezeigteCalendarList.get(i);
                    }
                }
                if(myCalendar != null){
                    myCalendar.update();
                    switch (benachrichtigungsTyp){
                        case MainActivity.NOTIFICATION_TYPE_TAGE:{
                            createNotification(context,myCalendar.getName(),myCalendar.getNochSchlafenText(context,false));
                            break;
                        }
                        case MainActivity.NOTIFICATION_TYPE_STUNDEN:{
                            String contentText = "";
                            if(benachrichtigungsTypValue>0){
                                contentText = "nur noch "+benachrichtigungsTypValue + " stunden";
                            }
                            else {
                                contentText = "vor "+Math.abs(benachrichtigungsTypValue) + " sekunden";
                            }
                            createNotification(context,myCalendar.getName(),contentText);
                            break;
                        }
                        case MainActivity.NOTIFICATION_TYPE_MINUTEN:{
                            String contentText = "";
                            if(benachrichtigungsTypValue>0){
                                contentText = "nur noch "+benachrichtigungsTypValue + " mit nutten ( . Y . )";
                            }
                            else {
                                contentText = "vor "+Math.abs(benachrichtigungsTypValue) + " mit nutten ( . Y . )";
                            }
                            createNotification(context,myCalendar.getName(),contentText);
                            break;
                        }
                        case MainActivity.NOTIFICATION_TYPE_SEKUNDEN:{
                            String contentText = "";
                            if(benachrichtigungsTypValue>0){
                                contentText = "nur noch "+benachrichtigungsTypValue + " sekunden";
                            }
                            else {
                                contentText = "vor "+Math.abs(benachrichtigungsTypValue) + " sekunden";
                            }
                            createNotification(context,myCalendar.getName(),contentText);
                            break;
                        }
                        default:{
                            Log.w(tag, "onReceive benachrichtigungsTyp matched keinen case (aber myCalendar != null)");
                        }
                    }
                }
                else{
                    Log.w(tag, "onReceive myCalendar == null");
                }
                updateNotificationIntents(context);
            }
        }
        else if(intent.getAction().contains("android.intent.action.BOOT_COMPLETED")){

            updateNotificationIntents(context);
        }
        else{
            Log.w("myNotificationBroadCastReciever OnReceive","falsche/keine intentaction gesetzt");
        }

    }




    public final static String intentActionMakeNotifications = "com.example.schutzenfesttimerMyNoBrCaReMakeNotifications";
    final String intentActionCancelNotifications = "com.example.schutzenfesttimerMyNoBrCaReCancelNotifications";






    String notificationChannelID = "schutzenfestTickerChannelID";
    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    String notificationChannelName = "schüüütimer notification channel";
    String notificationChannelDescription = "hier kommen die schüüütimer notifications";
    int notificationID; //rand
    public void createNotification(Context context, String title, String content){
        Intent goToAppIntent = new Intent(context, MainActivity.class);
        PendingIntent goToAppPendingIntent = PendingIntent.getActivity(context,0,goToAppIntent,0);

        if(android.os.Build.VERSION.SDK_INT> android.os.Build.VERSION_CODES.O){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // The user-visible name of the channel.
            CharSequence name = notificationChannelName;

            // The user-visible description of the channel.
            String description = notificationChannelDescription;

            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(notificationChannelID, name,importance);
            channel.setDescription(description);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            else{
                Log.w("myNotificationBroadCR","mNotificationManager == null, deswegen kann der notificationchannel nicht erstellt werden");
            }

        }
        builder = new NotificationCompat.Builder(context, notificationChannelID)
                .setSmallIcon(R.drawable.ic_bierkrug)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(goToAppPendingIntent)
                .addAction(new NotificationCompat.Action(R.drawable.ic_drawing,"benachrichtigungen abbrechen",getPendingIntentToThisReciever(context,intentActionCancelNotifications)))
                .setChannelId(notificationChannelID);
        notificationManagerCompat = NotificationManagerCompat.from(context);
        long millis = Calendar.getInstance().getTimeInMillis();
        millis = (millis % 100000000);
        notificationID = (int) millis;
        notificationManagerCompat.notify(notificationID, builder.build());
    }


    void cancelNotificationIntents(Context context){
        Log.v(tag, "canceling intents");
        for(int i = 0; i< zuPostendeIntents; i++){
            String intentAction = intentActionMakeNotifications + i;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if(alarmManager != null){
                alarmManager.cancel(getPendingIntentToThisReciever(context,intentAction));
            }
            else{
                Log.e(tag,"alarmManager == null");
            }
        }
        //PendingIntent.getBroadcast(context, 0,makeNotificationIntent,0).cancel();
    }

    Intent makeNotificationIntent;

    PendingIntent getPendingIntentToThisReciever(Context context, String actionString){

        Intent intent = new Intent(context, myNotificationBroadCastReciever.class);
        intent.setAction(actionString);
        return PendingIntent.getBroadcast(context, 0 ,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }


    void updateNotificationIntents(final Context context){
        //viel cpu aufwand
        //berechnet erst alle notifications und sortiert die dann und sucht sich die nächsten erst raus deswegen nicht on MainThread
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){

                cancelNotificationIntents(context);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                if (alarmManager != null) {

                    List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> benachrichtigungen = getListZuBenachrichtigendeBenachrichtigungsZeitpunkteMitInfosSortiertNachZeitpunk(zuPostendeIntents);
                    for(int i = 0; i< benachrichtigungen.size(); i++){
                        MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo benachrichtigung = benachrichtigungen.get(i);
                        String intentAction = intentActionMakeNotifications + i;


                        Intent intent = new Intent(context, myNotificationBroadCastReciever.class);
                        intent.setAction(intentAction);
                        intent.putExtra(context.getResources().getString(R.string.intentExtraName_CalendarId),benachrichtigung.calendarId);
                        intent.putExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsId),benachrichtigung.benachrichtigungsId);
                        intent.putExtra(context.getResources().getString(R.string.intentExtraName_ZeitPunkt),benachrichtigung.zeitpunkt);
                        intent.putExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsTyp),benachrichtigung.benachrichtigungsTyp);
                        intent.putExtra(context.getResources().getString(R.string.intentExtraName_BenachrichtigungsTypValue),benachrichtigung.benachrichtigungsTypValue);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0 ,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, benachrichtigung.zeitpunkt.getTimeInMillis(),pendingIntent);
                        Log.v(tag,"intenting in "+ (benachrichtigung.zeitpunkt.getTimeInMillis()-System.currentTimeMillis())/(1000 * 60 * 60 * 24) + " tagen wegen "+benachrichtigung.myToString());
                        MainActivity.log(context,benachrichtigung.myToString());
                    }
                }
                else{
                    Log.e(tag,"alarmManager == null");
                    Toast.makeText(context,"konnte keine zukünftigen benachrichtigungen posten deswegen werden die wahrscheinlich nicht erscheinen", Toast.LENGTH_LONG).show();
                }

            }
        });
        thread.start();


        /* old

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int notificationHour = sharedPreferences.getInt(context.getString(R.string.pref_key_defaultNotificationTimeHour),12);
        int notificaitonMinute = sharedPreferences.getInt(context.getString(R.string.pref_key_defaultNotificationTimeMinute),0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //wenn die notification time heute schon rum ist erst morgen benachrichtigen anonsten heute schon
        if(notificationHour<calendar.get(Calendar.HOUR_OF_DAY)){
            calendar.add(Calendar.DATE,1);
        }
        else if(notificationHour==calendar.get(Calendar.HOUR_OF_DAY)){
            if(notificaitonMinute<=calendar.get(Calendar.MINUTE)){
                calendar.add(Calendar.DATE,1);
            }
        }

        calendar.set(Calendar.HOUR_OF_DAY,notificationHour);
        calendar.set(Calendar.MINUTE,notificaitonMinute);
        calendar.set(Calendar.SECOND,0);

        AlarmManager alarme = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarme != null) {
            alarme.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntentToThisReciever(context,intentActionMakeNotifications));
            Log.v("myNotifBroCaRe","intenting in "+ (calendar.getTimeInMillis()-System.currentTimeMillis())/1000 + " sec");
        }
        else{
            Log.v("set not intent","alarme == null");
        }
        */
    }



    List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> getListZuBenachrichtigendeBenachrichtigungsZeitpunkteMitInfosSortiertNachZeitpunk(int wieviele) {
        List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> listAlle = getListAlleBenachrichtigungsZeitpunkteMitInfosSortiertNachZeitpunk();
        List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> returnList = new ArrayList<>();
        int counter = 0;
        for(int i = 0; i<listAlle.size() && counter < wieviele; i++){
            if(listAlle.get(i).zeitpunkt.getTimeInMillis()>Calendar.getInstance().getTimeInMillis()){
                returnList.add(listAlle.get(i));
                counter++;
            }
        }
        return returnList;
    }

    List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> getListAlleBenachrichtigungsZeitpunkteMitInfosSortiertNachZeitpunk(){
        List<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo> returnList = new ArrayList<>();
        //Log.v(tag, "myAngezeigteCalendarList.size() "+myAngezeigteCalendarList.size());
        for(int i = 0; i<MainActivity.myAngezeigteCalendarList.size(); i++){
            returnList.addAll(MainActivity.myAngezeigteCalendarList.get(i).getListBenachrichtigungsZeitpunktMitInfos());
        }

        Collections.sort(returnList, new Comparator<MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo>() {
            @Override
            public int compare(MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo o1, MyBenachrichtigung.BenachrichtigungsZeitpunktMitInfo o2) {
                return Long.compare(o1.zeitpunkt.getTimeInMillis(), o2.zeitpunkt.getTimeInMillis());
            }
        });

        return returnList;
    }
}
