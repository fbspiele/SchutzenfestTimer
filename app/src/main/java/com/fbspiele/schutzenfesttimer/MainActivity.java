package com.fbspiele.schutzenfesttimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.fbspiele.schutzenfesttimer.ui.main.PlaceholderFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.fbspiele.schutzenfesttimer.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static java.util.Calendar.getInstance;

public class MainActivity extends AppCompatActivity {
    final static String tag = "MainActivity";
    ViewPager viewPager;
    Handler handler;
    TabLayout tabs;
    SectionsPagerAdapter sectionsPagerAdapter;

    final static int NOTIFICATION_TYPE_ERROR = 0;        // 0 = error
    final static int NOTIFICATION_TYPE_TAGE = 1;        // 0 = error
    final static int NOTIFICATION_TYPE_STUNDEN = 2;        // 0 = error
    final static int NOTIFICATION_TYPE_MINUTEN = 3;        // 0 = error
    final static int NOTIFICATION_TYPE_SEKUNDEN = 4;        // 0 = error


    // 1 standard (in ... tage, vor ... tage, heute is), später (dann -1 = bergfest, 2 = stunden, 3 = minuten, 4 = sekunden oder so) //vielleicht als rescource file eintrag machen

    public static List<MyCalendar> myCalendarList = new ArrayList<>();

    public static List<MyBenachrichtigung> myStandardBenachrichtigungList = new ArrayList<>();

    public static List<MyCalendar> myAngezeigteCalendarList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity","onCreate");

        //check ob die app zum allerersten mal geöffnet wird

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(getString(R.string.pref_key_appZumAllerErstenMalGeoffnet),true)){
            Log.v(tag, "app wird zum allerersten mal geöffnet");
            resetEverything(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_key_appZumAllerErstenMalGeoffnet),false);
            editor.apply();
        }



        loadEverything(getApplicationContext());


        if(myCalendarList==null){
            myCalendarList = new ArrayList<>();
        }

        if(myCalendarList.size()==0){
            Toast.makeText(this, "anscheinend hast du alle events gelöscht (oder ich habs verkackt beim programmieren), deswegen resette die app jetzt wieder alle events",Toast.LENGTH_LONG).show();
            resetMyCalendarList(this);
        }

        /*
        if(myStandardBenachrichtigungList==null){
            myStandardBenachrichtigungList = new ArrayList<>();
        }
        if(myStandardBenachrichtigungList.size()==0){
            myStandardBenachrichtigungList = getResettedMyStandardBenachrichtigungList();
        }*/


        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);







        //setNotificationIntent();



        handler = new Handler();
        handler.postDelayed(updateTimes,1000);


        findViewById(R.id.settings).setOnClickListener(v -> {
            Intent intent_settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent_settings);
        });



        //intents aktualisieren
        new myNotificationBroadCastReciever().updateNotificationIntents(getApplicationContext());
    }

    //DONE die notifications werden noch nicht gepostet
        // alle die zur selben zeit notified werden sollen bündeln
        // ids in eine vernünftige list (eigentlich langt ja calendar id)
                //später dann wenn nach bergfest oder sekunden oder ... notified werden soll vielleicht noch ein indikator dazu
        // diese liste als bundle (oder wie auch immer das funktioniert) zur notification time die app wecken
                //die app liest dann das bundle aus sieht welche art von notification sie posten muss
    //die schlafenzeit (tageswechselzeit) lässt sich noch nicht einstellen
    //quick settings tile

    //TODO
    // jahre monate wochen einbauen
    // unterscheiden zwischen standardnotifications und standardnotifications für individuelle events
    // spätere schüüüs nach https://nn.wikipedia.org/wiki/P%C3%A5skeformelen#Formeloppbygging berechnen
    //TODO bergfest für schüüs


    @Override
    protected void onResume() {
        super.onResume();


        resortMyCalendarList();
        Fragment fragment;


        loadMyAngezeigteCalendarList(getApplicationContext());


        for(int i = 0; i<myAngezeigteCalendarList.size();i++){
            fragment = sectionsPagerAdapter.getFragmentByPosition(i);
            if(fragment instanceof PlaceholderFragment){
                ((PlaceholderFragment) fragment).updateCalendar(myAngezeigteCalendarList.get(i));
            }
        }




        viewPager.setAdapter(sectionsPagerAdapter);
        //anfangsTab herausfinden
        long jetzt = Calendar.getInstance().getTimeInMillis();
        int anfangsTab = 0;
        for(int i = 0; i<myAngezeigteCalendarList.size();i++){
            MyCalendar calendar = myAngezeigteCalendarList.get(i);
            if(calendar.calendarEnde.getTimeInMillis()<jetzt){
                anfangsTab++;
            }
        }
        viewPager.setCurrentItem(anfangsTab);

        if(myAngezeigteCalendarList.size()==0){
            Toast.makeText(getApplicationContext(),"keine events die angezeigt werden einstellungen -> einträge -> neues event hinzufügen / schüüüüüs aktivieren",Toast.LENGTH_LONG).show();
        }


    }
    static void resetEverything(Context context){

        resetMyCalendarList(context);
        resetMyStandardBenachrichtigungsList(context);
    }

    static void resetMyCalendarList(Context context){

        int startYear = Integer.parseInt(context.getResources().getString(R.string.startYear));
        int endYear = Integer.parseInt(context.getResources().getString(R.string.endYear));

        myCalendarList = new ArrayList<>();
        Log.w("MainActivity", "reset of myCalendarList");


        // gottes geburtstag
        Calendar meinGeburtstag = getInstance();
        meinGeburtstag.set(1993, Calendar.JULY, 2, 18, 4);
        MyCalendar geburtGottes = new MyCalendar(meinGeburtstag, meinGeburtstag, "geburt gottes");
        List<MyBenachrichtigung.JedenWennKleinerGleich> conditionen = new ArrayList<>();
        long hundertMillionen = 100*1000*1000;
        MyBenachrichtigung.JedenWennKleinerGleich condition = new MyBenachrichtigung.JedenWennKleinerGleich(hundertMillionen,hundertMillionen*32);      //32 * hundertMillionen sec sind ungefähr tausend jahre
        conditionen.add(condition);
        MyBenachrichtigung hundertMillionenSekBenachrichtigung = new MyBenachrichtigung("alle 100.000.000 sekunden",NOTIFICATION_TYPE_SEKUNDEN,true,true,true,12,0,conditionen);
        geburtGottes.addBenachrichtigung(hundertMillionenSekBenachrichtigung);
        myCalendarList.add(geburtGottes);

        // g m standesamtliche hochzeit
        Calendar gmHochzeitStandesamtlichAnfang = getInstance();
        gmHochzeitStandesamtlichAnfang.set(2021, Calendar.MAY, 22, 10, 0);
        Calendar gmHochzeitStandesamtlichEnde = getInstance();
        gmHochzeitStandesamtlichEnde.set(2021, Calendar.MAY, 22, 18, 0);
        MyCalendar gmStandesamtlichHochzeit = new MyCalendar(gmHochzeitStandesamtlichAnfang, gmHochzeitStandesamtlichEnde, "g*** m*** hochzeit");
        gmStandesamtlichHochzeit.addBenachrichtigung(hundertMillionenSekBenachrichtigung);
        myCalendarList.add(gmStandesamtlichHochzeit);


        // g m hochzeit
        Calendar gmHochzeitAnfang = getInstance();
        gmHochzeitAnfang.set(2021, Calendar.AUGUST, 21, 14, 0);
        Calendar gmHochzeitEnde = getInstance();
        gmHochzeitEnde.set(2021, Calendar.AUGUST, 22, 5, 0);
        MyCalendar gmHochzeit = new MyCalendar(gmHochzeitAnfang, gmHochzeitEnde, "g*** m*** hochzeitsparty");
        gmHochzeit.addBenachrichtigung(hundertMillionenSekBenachrichtigung);
        myCalendarList.add(gmHochzeit);


        // a s hochzeit
        Calendar asHochzeitAnfang = getInstance();
        asHochzeitAnfang.set(2022, Calendar.AUGUST, 22, 14, 0);
        Calendar asHochzeitEnde = getInstance();
        asHochzeitEnde.set(2022, Calendar.AUGUST, 23, 5, 0);
        MyCalendar asHochzeit = new MyCalendar(asHochzeitAnfang, asHochzeitEnde, "a*** s*** hochzeit");
        asHochzeit.addBenachrichtigung(hundertMillionenSekBenachrichtigung);
        myCalendarList.add(asHochzeit);




        for (int year = startYear; year <= endYear; year++) {
            MyCalendar schuCalendar = new  MySchuCalendar(year);
            myCalendarList.add(schuCalendar);
        }

        saveMyCalendarList(context);
    }

    static void resetMyStandardBenachrichtigungsList(Context context){
        myStandardBenachrichtigungList = getResettedMyStandardBenachrichtigungList();
        saveMyStandardBenachrichtigungList(context);


        //intents aktualisieren
        new myNotificationBroadCastReciever().updateNotificationIntents(context);
    }


    static void loadEverything(Context context){

        myCalendarList = loadMyCalendarList(context);
        myStandardBenachrichtigungList = loadMyStandardBenachrichtigungList(context);
        loadMyAngezeigteCalendarList(context);

    }

    static void loadMyAngezeigteCalendarList(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean schusAktiviert = sharedPreferences.getBoolean(context.getString(R.string.pref_key_calendarsSchusAktiviert),true);

        if(schusAktiviert){
            myAngezeigteCalendarList = myCalendarList;
        }
        else{
            myAngezeigteCalendarList = new ArrayList<>();

            for(int i = 0; i<myCalendarList.size();i++){
                MyCalendar myCalendar = myCalendarList.get(i);
                if(!myCalendar.isSchuCalendar){
                    myAngezeigteCalendarList.add(myCalendar);
                }
            }
        }
    }


    static public List<MyBenachrichtigung> getResettedMyStandardBenachrichtigungList(){
        List<MyBenachrichtigung> list = new ArrayList<>();




        List<MyBenachrichtigung.JedenWennKleinerGleich> benachrichtigungsConditionenDavorUndAmEvent = new ArrayList<>();

        MyBenachrichtigung.JedenWennKleinerGleich unter100000 = new MyBenachrichtigung.JedenWennKleinerGleich(10000,100000);
        MyBenachrichtigung.JedenWennKleinerGleich unter10000 = new MyBenachrichtigung.JedenWennKleinerGleich(1000,10000);
        MyBenachrichtigung.JedenWennKleinerGleich unter1000 = new MyBenachrichtigung.JedenWennKleinerGleich(100,1000);
        MyBenachrichtigung.JedenWennKleinerGleich unter100 = new MyBenachrichtigung.JedenWennKleinerGleich(10,100);
        MyBenachrichtigung.JedenWennKleinerGleich unter50 = new MyBenachrichtigung.JedenWennKleinerGleich(5,50);
        MyBenachrichtigung.JedenWennKleinerGleich unter25 = new MyBenachrichtigung.JedenWennKleinerGleich(1,25);

        benachrichtigungsConditionenDavorUndAmEvent.add(unter100000);
        benachrichtigungsConditionenDavorUndAmEvent.add(unter10000);
        benachrichtigungsConditionenDavorUndAmEvent.add(unter1000);
        benachrichtigungsConditionenDavorUndAmEvent.add(unter100);
        benachrichtigungsConditionenDavorUndAmEvent.add(unter50);
        benachrichtigungsConditionenDavorUndAmEvent.add(unter25);

        MyBenachrichtigung benachrichtigungDavor = new MyBenachrichtigung("18 uhr davor",MainActivity.NOTIFICATION_TYPE_TAGE,true,false,false,18,0,benachrichtigungsConditionenDavorUndAmEvent);
        list.add(benachrichtigungDavor);

        MyBenachrichtigung benachrichtigungAm = new MyBenachrichtigung("18 uhr am event",MainActivity.NOTIFICATION_TYPE_TAGE,false,true,false,18,0,null);
        list.add(benachrichtigungAm);

        List<MyBenachrichtigung.JedenWennKleinerGleich> benachrichtigungsConditionenDanach = new ArrayList<>();

        MyBenachrichtigung.JedenWennKleinerGleich unter100000Danach = new MyBenachrichtigung.JedenWennKleinerGleich(10000,100000);
        MyBenachrichtigung.JedenWennKleinerGleich unter10000Danach = new MyBenachrichtigung.JedenWennKleinerGleich(1000,10000);
        MyBenachrichtigung.JedenWennKleinerGleich unter1000Danach = new MyBenachrichtigung.JedenWennKleinerGleich(100,1000);

        benachrichtigungsConditionenDanach.add(unter100000Danach);
        benachrichtigungsConditionenDanach.add(unter10000Danach);
        benachrichtigungsConditionenDanach.add(unter1000Danach);

        MyBenachrichtigung benachrichtigungDanach = new MyBenachrichtigung("18 uhr danach",MainActivity.NOTIFICATION_TYPE_TAGE, false,false,true,18,0,benachrichtigungsConditionenDanach);


        list.add(benachrichtigungDanach);

        List<MyBenachrichtigung.JedenWennKleinerGleich> benachrichtigungsConditionenAlleMilliarden = new ArrayList<>();
        long eineMillarden = 1000*1000*1000;    //eine milliarde sek sind 31 jahre -> bis 310 jahre is genug
        MyBenachrichtigung.JedenWennKleinerGleich alleMilliardenImmer = new MyBenachrichtigung.JedenWennKleinerGleich(eineMillarden,eineMillarden*10);
        benachrichtigungsConditionenAlleMilliarden.add(alleMilliardenImmer);
        MyBenachrichtigung alleMilliardenSekunden = new MyBenachrichtigung("jede 1.000.000.000 sekunden", MainActivity.NOTIFICATION_TYPE_SEKUNDEN,true,true,true,12,0,benachrichtigungsConditionenAlleMilliarden);

        list.add(alleMilliardenSekunden);

        List<MyBenachrichtigung.JedenWennKleinerGleich> benachrichtigungsConditionenAlleMillionen = new ArrayList<>();
        MyBenachrichtigung.JedenWennKleinerGleich alleMillionen = new MyBenachrichtigung.JedenWennKleinerGleich(1000000,1000000);
        benachrichtigungsConditionenAlleMillionen.add(alleMillionen);
        MyBenachrichtigung eineMillionen = new MyBenachrichtigung("jede 1.000.000 sekunden", MainActivity.NOTIFICATION_TYPE_SEKUNDEN,true,true,true,12,0,benachrichtigungsConditionenAlleMillionen);


        list.add(eineMillionen);

        return list;

    }

    static List<MyCalendar> loadMyCalendarList(Context context){
        Log.v(tag, "loading calendar list ...");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getString(R.string.pref_key_myCalendarListSaveKey),"");
        List<MyCalendar> calendarList = gson.fromJson(json, new TypeToken<List<MyCalendar>>(){}.getType());
        StringBuilder sb = new StringBuilder();
        if(calendarList==null){
            calendarList = new ArrayList<>();
        }
        for(int i = 0;i < calendarList.size(); i++){
            sb.append("\n").append(calendarList.get(i).getName());
        }
        Log.v("MainAcitivity","loaded calendarlist "+sb.toString());
        return calendarList;
    }


    static List<MyBenachrichtigung> loadMyStandardBenachrichtigungList(Context context){
        Log.v(tag, "loading standardBenachrichtungList list ...");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getString(R.string.pref_key_mStandardBenachrichtigungListSaveKey),"");
        List<MyBenachrichtigung> list = gson.fromJson(json, new TypeToken<List<MyBenachrichtigung>>(){}.getType());
        if(list == null){
            Log.w(tag, "loadMyStandardBenachrichtigungList hat ne null list geladen werde ne neue leere list weitergeben");
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();

        for(int i = 0;i < list.size(); i++){
            sb.append("\n").append(list.get(i).name);
        }
        Log.v("MainAcitivity","loaded standardBenachrichtigungslist "+sb.toString());
        return list;
    }

    public static void deleteCalendarInMyCalendarList(MyCalendar calendarToDelete){
        int index = -1;
        for (int i = 0; i<myCalendarList.size(); i++){
            if(myCalendarList.get(i).id==calendarToDelete.id){
                index = i;
            }
        }
        if(index!=-1){
            myCalendarList.remove(index);
        }
        else {
            Log.w(tag, "deleteCalendarInMyCalendarList\t calendar "+calendarToDelete.getName()+" (id="+calendarToDelete.id+") nicht gefunden");
        }
    }

    public static void replaceCalendarInMyCalendarList(MyCalendar newCalendar){
        int index = -1;
        for(int i = 0; i<myCalendarList.size(); i++){
            if(myCalendarList.get(i).id==newCalendar.id){
                index = i;
            }
        }
        if(index!=-1){
            myCalendarList.remove(index);
            myCalendarList.add(index,newCalendar);
        }
        else {
            Log.w(tag, "replaceCalendarInMyCalendarList\t calendar "+newCalendar.getName()+" (id="+newCalendar.id+") nicht gefunden");
        }
    }

    static String getSchonesDatumPlusZeit(Calendar calendar){
        String string = MyCalendar.getSchonesDatum(calendar);
        string += "-";
        string += calendar.get(Calendar.HOUR_OF_DAY) + ":";

        if(calendar.get(Calendar.MINUTE)<10){
            string += "0";
        }
        string += calendar.get(Calendar.MINUTE) + ":";

        if(calendar.get(Calendar.SECOND)<10){
            string += "0";
        }
        string += calendar.get(Calendar.SECOND);
        return string;
    }

    public static void resortMyCalendarList(){
        final long jetzt = Calendar.getInstance().getTimeInMillis();
        myCalendarList.sort((o1, o2) -> {
            long o1Anf = o1.calendarAnfang.getTimeInMillis();
            long o1End = o1.calendarEnde.getTimeInMillis();
            long o2Anf = o2.calendarAnfang.getTimeInMillis();
            long o2End = o2.calendarEnde.getTimeInMillis();

            //wenn o1 eher anfängt und endet
            // o1Anf    o1End                 o2Anf    o2End
            if (o1Anf < o2Anf && o1End < o2End) {
                //Log.w(tag,o1.getName()+ " ("+getSchonesDatumPlusZeit(o1.calendarAnfang)+ ")"+" ganz vor "+o2.getName() + " ("+getSchonesDatumPlusZeit(o2.calendarAnfang)+ ")"+" typ 1");
                return -1;
            }
            //wenn o1 später anfängt und endet
            // o2Anf    o2End                 o1Anf    o1End
            if (o1Anf > o2Anf && o1End > o2End) {
                //Log.w(tag,o2.getName()+" ganz vor "+o1.getName() + " typ 2");
                return 1;
            }

            //wenn o1 anfängt dann fängt o2 an dann hört o1 auf dann hört o2 auf is in wenn o1 eher anfängt und endet drin


            //wenn 2 innerhalb von 1 liegt dann kommts drauf an ob 2 in der zukunft oder vergangenheit liegt
            if (o1Anf < o2Anf && o1End > o2End) {
                //Log.w(tag,o2.getName()+" in "+o1.getName()+" typ 1");
                //wenn 2 in zukunft dann erst 1 (weils ja eher anfängt oder schon angefangen hat)
                //              o1Anf       o2Anf       o2End       o1End
                if (o2Anf > jetzt) {
                    return -1;
                } else if (o2End < jetzt) {
                    return 1;
                } else {
                    //dann müsste ja jetzt folgendes sein
                    //              o1Anf       o2Anf       jetzt       o2End       o1End
                    //des heißt o2 läuft also is des wichtiger da kürzer also weiter "links"
                    return 1;
                }
            }

            //andersrum andersrum
            if (o1Anf > o2Anf && o1End < o2End) {
                //Log.w(tag,o1.getName()+" in "+o2.getName() + " typ 2");
                //              o2Anf       o1Anf       o1End       o2End
                //wenn 1 in zukunft dann erst 2 (weils ja eher anfängt oder schon angefangen hat)
                if (o1Anf > jetzt) {
                    return 1;
                } else if (o1End < jetzt) {
                    return -1;
                } else {
                    //dann müsste ja jetzt folgendes sein
                    //              o2Anf       o1Anf       jetzt       o1End       o2End
                    //des heißt o1 läuft also is des wichtiger da kürzer also weiter "links"
                    return -1;
                }
            }


            //wenn eins der beiden enden eher is als der anfang (was ja kein sinn macht dann einfach den durchschnitt vergleichen (weil was sonst))
            if (o1Anf > o1End || o2Anf > o2End) {
                long o1AvgMal2 = o1Anf + o1End;
                long o2AvgMal2 = o2Anf + o2End;
                return Long.compare(o1AvgMal2, o2AvgMal2);
            }

            Log.e(tag, "MyCalendar comparator case nicht behandelt zwischen " + o1.getName() + " und " + o2.getName());
            return 0;
        });

    }

    public static void saveMyCalendarList(Context context){
        resortMyCalendarList();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myCalendarList);
        editor.putString(context.getString(R.string.pref_key_myCalendarListSaveKey),json);
        editor.apply();

        //intents aktualisieren
        new myNotificationBroadCastReciever().updateNotificationIntents(context);
    }

    public static void log(Context context, String newLog){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String log = sharedPreferences.getString(context.getResources().getString(R.string.pref_key_logString),"");
        String logTime = getSchonesDatumPlusZeit(Calendar.getInstance());
        log = logTime+"\n"+newLog+"\n\n"+log;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.pref_key_logString),log);
        editor.apply();
    }

    public static void saveMyStandardBenachrichtigungList(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myStandardBenachrichtigungList);
        editor.putString(context.getString(R.string.pref_key_mStandardBenachrichtigungListSaveKey),json);
        editor.apply();


        //intents aktualisieren
        new myNotificationBroadCastReciever().updateNotificationIntents(context);
    }

    Runnable updateTimes = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            //Log.v("updateTimes","currentItem "+currentItem);
            Fragment fragment = sectionsPagerAdapter.getFragmentByPosition(currentItem);
            if(fragment instanceof PlaceholderFragment){
                ((PlaceholderFragment) fragment).updateView();
            }
            handler.postDelayed(updateTimes,1000);
        }
    };
}



