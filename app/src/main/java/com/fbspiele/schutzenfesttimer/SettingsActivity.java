package com.fbspiele.schutzenfesttimer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.Preferences;

public class SettingsActivity extends PreferenceActivity {
    final static String tag = "SettingsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    public static class NotificationFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);


            addPreferencesFromResource(R.xml.pref_notification);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    public static class StandardNotificationsFragment extends PreferenceFragment{
        PreferenceScreen preferenceScreen;
        Preference prefNeueHinzufugen;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            addPreferencesFromResource(R.xml.pref_standardnotifications);


            preferenceScreen = (PreferenceScreen) findPreference(getString(R.string.pref_key_benachrichtigungPrefScreenAlle));





            prefNeueHinzufugen = findPreference(getString(R.string.pref_key_standardNotificationsNeueHinzufg));
            prefNeueHinzufugen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final myBenachrichtigungsVerandernAlertDialogBuilder builder = new myBenachrichtigungsVerandernAlertDialogBuilder(getContext());


                    builder.setTitle("benachrichtigung");
                    View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_benachrichtigung_verandern, null);

                    builder.benachrichtigung = new MyBenachrichtigung("name", MainActivity.NOTIFICATION_TYPE_ERROR,true,true,true,12,0,new ArrayList());



                    builder.setView(v);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            MyBenachrichtigung benachrichtigung = builder.benachrichtigung;
                            MainActivity.myStandardBenachrichtigungList.add(benachrichtigung);
                            MainActivity.saveMyStandardBenachrichtigungList(getContext());
                            onResume();
                        }
                    });
                    builder.setNegativeButton("nö", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();



                    return false;
                }
            });







        }

        @Override
        public void onResume() {
            super.onResume();

            preferenceScreen.removeAll();
            preferenceScreen.addPreference(prefNeueHinzufugen);

            for(int i = 0; i<MainActivity.myStandardBenachrichtigungList.size();i++){
                final MyBenachrichtigung myBenachrichtigung = MainActivity.myStandardBenachrichtigungList.get(i);
                final Preference benachrichtigungsPref = new Preference(getContext());
                benachrichtigungsPref.setTitle(myBenachrichtigung.name);
                if(myBenachrichtigung.benachrichtigungsTyp == MainActivity.NOTIFICATION_TYPE_TAGE){
                    benachrichtigungsPref.setSummary(myBenachrichtigung.getBenachrichtigungsZeit());
                }


                benachrichtigungsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        //AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                        final myBenachrichtigungsVerandernAlertDialogBuilder builder = new myBenachrichtigungsVerandernAlertDialogBuilder(getContext());


                        builder.setTitle("benachrichtigung");
                        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_benachrichtigung_verandern, null);

                        builder.initialize(myBenachrichtigung);




                        builder.setView(v);
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MyBenachrichtigung benachrichtigung = builder.benachrichtigung;

                                int benachrichtigungsIndexInList = -1;
                                for(int i = 0; i<MainActivity.myStandardBenachrichtigungList.size();i++){
                                    if(MainActivity.myStandardBenachrichtigungList.get(i).id==benachrichtigung.id){
                                        benachrichtigungsIndexInList = i;
                                    }
                                }
                                if(benachrichtigungsIndexInList!=-1){
                                    MainActivity.myStandardBenachrichtigungList.remove(benachrichtigungsIndexInList);
                                    MainActivity.myStandardBenachrichtigungList.add(benachrichtigungsIndexInList, benachrichtigung);
                                }
                                else {
                                    Log.w("SettingsActivity","wollte beim speichern standardbenachrichtigung "+benachrichtigung.name+" (id="+benachrichtigung.id+") in myStandardBenachrichtigungList austauschen aber hab sie nicht gefunden");
                                }



                                MainActivity.saveMyStandardBenachrichtigungList(getContext());
                                onResume();
                            }
                        });
                        builder.setNegativeButton("nö", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNeutralButton("löschen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MyBenachrichtigung benachrichtigung = builder.benachrichtigung;

                                int benachrichtigungsIndexInList = -1;
                                for(int i = 0; i<MainActivity.myStandardBenachrichtigungList.size();i++){
                                    if(MainActivity.myStandardBenachrichtigungList.get(i).id==benachrichtigung.id){
                                        benachrichtigungsIndexInList = i;
                                    }
                                }
                                if(benachrichtigungsIndexInList!=-1){
                                    MainActivity.myStandardBenachrichtigungList.remove(benachrichtigungsIndexInList);
                                }
                                else {
                                    Log.w("SettingsActivity","wollte beim löschen standardbenachrichtigung "+benachrichtigung.name+" (id="+benachrichtigung.id+") in myStandardBenachrichtigungList löschen aber hab sie nicht gefunden");
                                }

                                MainActivity.saveMyStandardBenachrichtigungList(getContext());
                                onResume();
                            }
                        });
                        builder.show();
                        builder.setBenachrichtigungsTyp(myBenachrichtigung.benachrichtigungsTyp);



                        return false;
                    }
                });
                preferenceScreen.addPreference(benachrichtigungsPref);

            }

            //check if standardBenachrichtigungsliste resetten anzeigen
            boolean standardBenachrichtigungListJeGenandert = false;
            List<MyBenachrichtigung> aktuelleList = MainActivity.myStandardBenachrichtigungList;
            List<MyBenachrichtigung> resetteteList = MainActivity.getResettedMyStandardBenachrichtigungList();
            if(aktuelleList.size()!=resetteteList.size()){
                standardBenachrichtigungListJeGenandert = true;
            }
            else {
                for(int i = 0; i < resetteteList.size(); i++){
                    if(!aktuelleList.get(i).getVergleichString().equals(resetteteList.get(i).getVergleichString())){
                        standardBenachrichtigungListJeGenandert = true;
                        Log.v(tag,"ungleiche vergleichstrings hier die string\naktuelleliste\n"+aktuelleList.get(i).getVergleichString()+"\nresettet\n"+resetteteList.get(i).getVergleichString());
                    }
                }
            }

            if(standardBenachrichtigungListJeGenandert){
                Preference myStandardBenachrichtigungListResettenPref = new Preference(getContext());
                myStandardBenachrichtigungListResettenPref.setTitle("standardbenachrichtigungen resetten?");
                myStandardBenachrichtigungListResettenPref.setSummary("resettet die standardbenachrichtigungen");
                myStandardBenachrichtigungListResettenPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("standardbenachrichtigungen resetten?");
                        builder.setMessage("setzt die standardbenachrichtigungen wieder in den anfangszustand zurück");
                        builder.setPositiveButton("jo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.myStandardBenachrichtigungList = MainActivity.getResettedMyStandardBenachrichtigungList();
                                MainActivity.saveMyStandardBenachrichtigungList(getContext());
                                onResume();
                            }
                        });
                        builder.setNeutralButton("doch ned",null);
                        builder.show();
                        return false;
                    }
                });
                preferenceScreen.addPreference(myStandardBenachrichtigungListResettenPref);
            }
        }

        ViewGroup container;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);
            this.container = container;
            return v;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    public static class GeneralFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            final String nextDayErklarText = "daraus wird berechnet wie oft noch schlafen bis zum event\nwenn auf 00:00 dann stimmt das mit den tagen überein";

            final Preference nextDayPref = findPreference(getString(R.string.pref_key_nextDayTime));
            int nextDayHourOfDay = sharedPreferences.getInt(getString(R.string.pref_key_nextDayTimeHourOfDay), 5);
            int nextDayMin = sharedPreferences.getInt(getString(R.string.pref_key_nextDayTimeMin), 0);
            nextDayPref.setSummary("um " + getBeautifulStringFromTime(nextDayHourOfDay, nextDayMin) + "\n"+nextDayErklarText);
            nextDayPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {   new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                        sharedPreferencesEditor.putInt(getString(R.string.pref_key_nextDayTimeHourOfDay),hourOfDay);
                        sharedPreferencesEditor.putInt(getString(R.string.pref_key_nextDayTimeMin),minute);
                        sharedPreferencesEditor.apply();

                        //intents aktualisieren
                        new myNotificationBroadCastReciever().updateNotificationIntents(getContext());

                        nextDayPref.setSummary("um " + getBeautifulStringFromTime(hourOfDay, minute) + "\n"+nextDayErklarText);
                    }
                }, sharedPreferences.getInt(getString(R.string.pref_key_nextDayTimeHourOfDay), 5), sharedPreferences.getInt(getString(R.string.pref_key_nextDayTimeMin), 0), true).show();
                    return false;
                }
            });

            final Preference allesResettenPref = findPreference(getString(R.string.pref_key_allesResetten));
            allesResettenPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("alles zurücksetzen?!?!?!");
                    builder.setMessage("willst du wirklich alles resetten und auf den ausgangszustand zurücksetzen?");
                    builder.setPositiveButton("jo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.resetEverything(getContext());
                        }
                    });
                    builder.setNegativeButton("nö doch ned", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    return false;
                }
            });


            final ListPreference firstDayOfWeekPref = (ListPreference) findPreference(getString(R.string.pref_key_startOfWeekListPreference));
            int firstDayOfWeekPrefIndex = sharedPreferences.getInt(getString(R.string.pref_key_startOfWeek),0);      //0 = system default, 1 = sonntag, 2 = montag, ...
            firstDayOfWeekPref.setValueIndex(firstDayOfWeekPrefIndex);
            firstDayOfWeekPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String[] pref_stringArray_firstDaysOfWeek = getResources().getStringArray(R.array.pref_stringArray_firstDaysOfWeek);
                    int index = 0;
                    for (int i = 0; i<pref_stringArray_firstDaysOfWeek.length; i++){
                        if(newValue.equals(pref_stringArray_firstDaysOfWeek[i])){
                            index = i;
                        }

                    }

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(getString(R.string.pref_key_startOfWeek),index);      //0 = system default, 1 = sonntag, 2 = montag, ...
                    editor.apply();
                    firstDayOfWeekPref.setValueIndex(index);

                    return false;
                }
            });

            setHasOptionsMenu(true);


        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    public static class DebuggingFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_debugging);
            final SwitchPreference debugLoggingSwitchPref = (SwitchPreference) findPreference(getString(R.string.pref_key_debugLogging));
            debugLoggingSwitchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(!debugLoggingSwitchPref.isChecked()){
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getContext().getString(R.string.pref_key_logString),"");
                        editor.apply();
                    }
                    return true;
                }
            });
            final Preference logAnzeigenPref = findPreference(getString(R.string.pref_key_logAnzeigen));
            logAnzeigenPref.setDependency(getString(R.string.pref_key_debugLogging));
            logAnzeigenPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder logDialogBuilder = new AlertDialog.Builder(getContext());
                    logDialogBuilder.setTitle("log");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String log = sharedPreferences.getString(getContext().getResources().getString(R.string.pref_key_logString),"");
                    logDialogBuilder.setMessage(log);
                    logDialogBuilder.setPositiveButton("ok",null);
                    logDialogBuilder.show();
                    return false;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
        }
    }


    static boolean checkIfSchuHasBeenModified(MyCalendar calendar){
        if(calendar.isSchuCalendar){
            MySchuCalendar newSchuCalendar = new MySchuCalendar(calendar.initialYear);
            return (calendar.calendarAnfang.getTimeInMillis()!= newSchuCalendar.calendarAnfang.getTimeInMillis()||
                    calendar.calendarEnde.getTimeInMillis()!=newSchuCalendar.calendarEnde.getTimeInMillis()||
                    !calendar.getName().equals(newSchuCalendar.getName()));
        }
        else{
            return false;
        }
    }


    public static class EntryFragement extends PreferenceFragment{

        MyCalendar entryCalendar;

        public EntryFragement(){
            Calendar anfang = Calendar.getInstance();
            Calendar ende = Calendar.getInstance();
            anfang.set(Calendar.SECOND,0);
            ende.set(Calendar.SECOND,0);
            entryCalendar = new MyCalendar(anfang, ende, "eventname");
        }


        boolean schuResettenAngezeigt = false;
        Preference prefName;
        Preference prefStartDatum;
        Preference prefStartZeit;
        Preference prefEndDatum;
        Preference prefEndZeit;
        SwitchPreference prefStandardBenachrichtigungenVerwenden;
        Preference prefNeueBenachrichtigung;
        PreferenceGroup preferenceGroupIndividuelleBenachrichtigungen;

        PreferenceScreen prefScreenBenachrichtigungen;

        @Override
        public void onResume() {
            super.onResume();

            prefScreenBenachrichtigungen.removeAll();

            prefStandardBenachrichtigungenVerwenden.setChecked(entryCalendar.standardBenachrichtigungenFurDiesesEventVerwenden);
            prefScreenBenachrichtigungen.addPreference(prefStandardBenachrichtigungenVerwenden);
            prefScreenBenachrichtigungen.addPreference(prefNeueBenachrichtigung);


            if(entryCalendar.eventBenachrichtigungen!=null){

                if(entryCalendar.eventBenachrichtigungen.size()>0){
                    preferenceGroupIndividuelleBenachrichtigungen.removeAll();
                    for(int i = 0; i<entryCalendar.eventBenachrichtigungen.size();i++){
                        final MyBenachrichtigung benachrichtigung = entryCalendar.eventBenachrichtigungen.get(i);
                        Preference eventBenachrichtigungPref = new Preference(getContext());
                        eventBenachrichtigungPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {

                                final myBenachrichtigungsVerandernAlertDialogBuilder builder = new myBenachrichtigungsVerandernAlertDialogBuilder(getContext());


                                builder.setTitle("benachrichtigung");
                                View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_benachrichtigung_verandern, null);

                                builder.initialize(benachrichtigung);




                                builder.setView(v);
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        entryCalendar.replaceBenachrichtigung(builder.benachrichtigung);

                                        MainActivity.saveMyCalendarList(getContext());

                                        onResume();
                                    }
                                });
                                builder.setNegativeButton("nö", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setNeutralButton("löschen", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        entryCalendar.deleteBenachrichtigung(builder.benachrichtigung.id);

                                        MainActivity.saveMyCalendarList(getContext());
                                        onResume();
                                    }
                                });
                                builder.show();
                                builder.setBenachrichtigungsTyp(benachrichtigung.benachrichtigungsTyp);

                                return false;
                            }
                        });
                        eventBenachrichtigungPref.setTitle(benachrichtigung.name);
                        if(benachrichtigung.benachrichtigungsTyp == MainActivity.NOTIFICATION_TYPE_TAGE){
                            eventBenachrichtigungPref.setSummary(benachrichtigung.getBenachrichtigungsZeit());
                        }
                        preferenceGroupIndividuelleBenachrichtigungen.addPreference(eventBenachrichtigungPref);
                    }
                    prefScreenBenachrichtigungen.addPreference(preferenceGroupIndividuelleBenachrichtigungen);

                }
            }

            if(checkIfSchuHasBeenModified(entryCalendar)){
                createResettPreference();
            }
        }



        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_entry);

            prefStandardBenachrichtigungenVerwenden = (SwitchPreference)  findPreference(getString(R.string.pref_key_calendarPrefEventBenachrichtigungenStandardBenachrichtigungenVerwenden));
            prefStandardBenachrichtigungenVerwenden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean warChecked = ((SwitchPreference) preference).isChecked();
                    entryCalendar.standardBenachrichtigungenFurDiesesEventVerwenden = !warChecked;

                    MainActivity.saveMyCalendarList(getContext());

                    return true;
                }
            });

            prefNeueBenachrichtigung = findPreference(getString(R.string.pref_key_eventNotificationsNeueHinzufg));
            prefScreenBenachrichtigungen = (PreferenceScreen) findPreference(getString(R.string.pref_key_calendarPrefScreenBenachrichtigungen));
            preferenceGroupIndividuelleBenachrichtigungen = (PreferenceGroup) prefScreenBenachrichtigungen.findPreference(getString(R.string.pref_key_calendarPrefCategoryIndividuelleBenachrichtigungen));


            prefName = findPreference(getString(R.string.pref_key_temp_name));
            prefName.setSummary(entryCalendar.getName());
            prefName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newName = newValue.toString();
                    entryCalendar.updateName(newName);
                    prefName.setSummary(newName);

                    MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                    MainActivity.saveMyCalendarList(getContext());

                    if(checkIfSchuHasBeenModified(entryCalendar)){
                        createResettPreference();
                    }
                    return false;
                }
            });



            prefStartZeit = findPreference(getString(R.string.pref_key_temp_startZeit));
            prefStartZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarAnfang));
            prefStartZeit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            entryCalendar.calendarAnfang.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            entryCalendar.calendarAnfang.set(Calendar.MINUTE,minute);
                            prefStartZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarAnfang));

                            MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                            MainActivity.saveMyCalendarList(getContext());


                            if(checkIfSchuHasBeenModified(entryCalendar)){
                                createResettPreference();
                            }
                        }
                    }, entryCalendar.calendarAnfang.get(Calendar.HOUR_OF_DAY), entryCalendar.calendarAnfang.get(Calendar.MINUTE), true).show();
                    return false;
                }
            });

            prefStartDatum = findPreference(getString(R.string.pref_key_temp_startDatum));
            prefStartDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarAnfang));
            prefStartDatum.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            entryCalendar.calendarAnfang.set(Calendar.YEAR, year);
                            entryCalendar.calendarAnfang.set(Calendar.MONTH, month);
                            entryCalendar.calendarAnfang.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            prefStartDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarAnfang));

                            MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                            MainActivity.saveMyCalendarList(getContext());


                            if(checkIfSchuHasBeenModified(entryCalendar)){
                                createResettPreference();
                            }
                        }
                    }, entryCalendar.calendarAnfang.get(Calendar.YEAR), entryCalendar.calendarAnfang.get(Calendar.MONTH), entryCalendar.calendarAnfang.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setFirstDayOfWeek(getFirstDayOfWeek());
                    dialog.show();
                    return false;
                }
            });


            prefEndZeit = findPreference(getString(R.string.pref_key_temp_endZeit));
            prefEndZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarEnde));
            prefEndZeit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            entryCalendar.calendarEnde.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            entryCalendar.calendarEnde.set(Calendar.MINUTE,minute);
                            prefEndZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarEnde));

                            MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                            MainActivity.saveMyCalendarList(getContext());

                            if(checkIfSchuHasBeenModified(entryCalendar)){
                                createResettPreference();
                            }
                        }
                    }, entryCalendar.calendarEnde.get(Calendar.HOUR_OF_DAY), entryCalendar.calendarEnde.get(Calendar.MINUTE), true).show();
                    return false;
                }
            });



            prefEndDatum = findPreference(getString(R.string.pref_key_temp_endDatum));
            prefEndDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarEnde));
            prefEndDatum.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            entryCalendar.calendarEnde.set(Calendar.YEAR, year);
                            entryCalendar.calendarEnde.set(Calendar.MONTH, month);
                            entryCalendar.calendarEnde.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            prefEndDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarEnde));

                            MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                            MainActivity.saveMyCalendarList(getContext());


                            if(checkIfSchuHasBeenModified(entryCalendar)){
                                createResettPreference();
                            }
                        }
                    }, entryCalendar.calendarEnde.get(Calendar.YEAR), entryCalendar.calendarEnde.get(Calendar.MONTH), entryCalendar.calendarEnde.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setFirstDayOfWeek(getFirstDayOfWeek());
                    dialog.show();
                    return false;
                }
            });

            Preference prefNeueBenachrichtigung = findPreference(getString(R.string.pref_key_eventNotificationsNeueHinzufg));
            prefNeueBenachrichtigung.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {



                    final myBenachrichtigungsVerandernAlertDialogBuilder builder = new myBenachrichtigungsVerandernAlertDialogBuilder(getContext());


                    builder.setTitle("benachrichtigung");
                    View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_benachrichtigung_verandern, null);

                    builder.benachrichtigung = new MyBenachrichtigung("name",MainActivity.NOTIFICATION_TYPE_ERROR, true,true,true,12,0, new ArrayList());

                    builder.setView(v);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            MyBenachrichtigung benachrichtigung = builder.benachrichtigung;
                            entryCalendar.addBenachrichtigung(benachrichtigung);
                            MainActivity.saveMyCalendarList(getContext());
                            onResume();
                        }
                    });
                    builder.setNegativeButton("nö", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                    return false;
                }
            });


            Preference prefCalendarLoschen = findPreference(getString(R.string.pref_key_calendarsCalendarLoschen));
            prefCalendarLoschen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(entryCalendar.getName() + " löschen???");
                    builder.setMessage("willst du wirklich das event "+ entryCalendar.getName() +" löschen");
                    builder.setPositiveButton("jo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.deleteCalendarInMyCalendarList(entryCalendar);
                            MainActivity.saveMyCalendarList(getContext());
                            getActivity().getFragmentManager().popBackStack();
                        }
                    });
                    builder.setNegativeButton("nö doch ned", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    return false;
                }
            });




            setHasOptionsMenu(true);
        }

        public void initialSetVariables(MyCalendar calendar){
            entryCalendar = calendar;
        }


        int getFirstDayOfWeek(){
            int systemDefault = Calendar.getInstance().getFirstDayOfWeek();

            //0 = system default, 1 = sonntag, 2 = montag, ...
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int saved = sharedPreferences.getInt(getString(R.string.pref_key_startOfWeek),systemDefault);

            if(saved==0){
                saved = systemDefault;
            }
            return saved;
        }





        void createResettPreference(){
            if(schuResettenAngezeigt){
                return;
            }
            //reset button
            final Preference resetPreference = new Preference(getContext());
            resetPreference.setTitle("schüüü resetten");
            resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if(entryCalendar.isSchuCalendar){
                        entryCalendar = new MySchuCalendar(entryCalendar.id,entryCalendar.initialYear);
                    }

                    prefName.setSummary(entryCalendar.getName());
                    prefStartDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarAnfang));
                    prefStartZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarAnfang));
                    prefEndZeit.setSummary(entryCalendar.getSchoneZeit(entryCalendar.calendarEnde));
                    prefEndDatum.setSummary(entryCalendar.getSchonesDatum(entryCalendar.calendarEnde));

                    PreferenceScreen preferenceScreen = getPreferenceScreen();
                    preferenceScreen.removePreference(resetPreference);
                    schuResettenAngezeigt = false;

                    MainActivity.replaceCalendarInMyCalendarList(entryCalendar);
                    MainActivity.saveMyCalendarList(getContext());


                    return false;
                }
            });

            PreferenceScreen preferenceScreen = this.getPreferenceScreen();
            preferenceScreen.addPreference(resetPreference);
            schuResettenAngezeigt = true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class EntriesFragment extends PreferenceFragment{
        PreferenceScreen preferenceScreen;
        PreferenceGroup preferenceGroupIndividualEvents, preferenceGroupSchus;
        SwitchPreference prefSchusAktiviert;
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_entries);


            findPreference(getString(R.string.pref_key_calendarsNeuerCalendar)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    EntryFragement entryFragement = new EntryFragement();

                    MainActivity.myCalendarList.add(entryFragement.entryCalendar);
                    MainActivity.saveMyCalendarList(getContext());


                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(container.getId(), entryFragement);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return false;
                }
            });


            preferenceScreen = this.getPreferenceScreen();
            preferenceGroupIndividualEvents = (PreferenceGroup) findPreference(getString(R.string.pref_key_calendarsCategoryIndividuals));
            preferenceGroupSchus = (PreferenceGroup) findPreference(getString(R.string.pref_key_calendarsCategorySchus));
            preferenceScreen.addPreference(preferenceGroupIndividualEvents);
            preferenceScreen.addPreference(preferenceGroupSchus);

            setHasOptionsMenu(true);
        }

        @Override
        public void onResume() {
            super.onResume();
            //Log.v("SettingsActivityEntries","onResume");
            preferenceGroupIndividualEvents.removeAll();
            preferenceGroupSchus.removeAll();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean schuAktiviert = sharedPreferences.getBoolean(getString(R.string.pref_key_calendarsSchusAktiviert),true);
            addCalendarPreferencesToPreferenceGroups(schuAktiviert);
        }

        ViewGroup container;

        void addCalendarPreferencesToPreferenceGroups(boolean schusAktiviert){

            prefSchusAktiviert = new SwitchPreference(getContext());
            prefSchusAktiviert.setTitle(getString(R.string.pref_title_calendarsSchusAktiviert));


            //Log.v("temp","schusAktiviert " + schusAktiviert);

            prefSchusAktiviert.setChecked(schusAktiviert);
            prefSchusAktiviert.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean warChecked = ((SwitchPreference) preference).isChecked();

                    //Log.v("temp","checked:" + warChecked);

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.pref_key_calendarsSchusAktiviert),!warChecked);
                    editor.apply();

                    preferenceGroupIndividualEvents.removeAll();
                    preferenceGroupSchus.removeAll();
                    addCalendarPreferencesToPreferenceGroups(!warChecked);

                    //intents aktualisieren
                    new myNotificationBroadCastReciever().updateNotificationIntents(getContext());
                    return true;
                }
            });
            preferenceGroupSchus.addPreference(prefSchusAktiviert);


            boolean alleSchusResettenAnzeigen = false;

            for(MyCalendar calendar:MainActivity.myCalendarList){
                if(calendar.isSchuCalendar){
                    if(checkIfSchuHasBeenModified(calendar)){
                        alleSchusResettenAnzeigen = true;
                    }
                }
            }

            if(alleSchusResettenAnzeigen){
                Preference prefAlleSchusResetten = new Preference(getContext());
                prefAlleSchusResetten.setTitle(getString(R.string.pref_title_alleSchusResetten));
                prefAlleSchusResetten.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getString(R.string.pref_title_alleSchusResetten));
                        builder.setMessage(getString(R.string.pref_message_alleSchusResetten));
                        builder.setPositiveButton("jo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<MyCalendar> myCalendarList = MainActivity.myCalendarList;

                                MyCalendar tempCalendar;
                                for(int i = 0; i<myCalendarList.size(); i++){
                                    if(myCalendarList.get(i).isSchuCalendar){
                                        tempCalendar = myCalendarList.get(i);
                                        tempCalendar = new MySchuCalendar(tempCalendar.id,tempCalendar.initialYear);

                                        MainActivity.replaceCalendarInMyCalendarList(tempCalendar);
                                    }
                                }
                                MainActivity.saveMyCalendarList(getContext());

                                onResume();
                                Log.v("TODO","ok resetten");
                            }
                        });
                        builder.setNegativeButton("nö",null);
                        builder.show();
                        return false;
                    }
                });

                if(!schusAktiviert){
                    prefAlleSchusResetten.setEnabled(false);
                    prefAlleSchusResetten.setShouldDisableView(true);
                }

                preferenceGroupSchus.addPreference(prefAlleSchusResetten);
            }

            for(int i = 0; i< MainActivity.myCalendarList.size();i++){
                MyCalendar calendarInLoop = MainActivity.myCalendarList.get(i);

                final int finalI = i;

                Preference pref = new Preference(getContext());
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        EntryFragement entryFragement = new EntryFragement();
                        entryFragement.initialSetVariables(MainActivity.myCalendarList.get(finalI));
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager
                                .beginTransaction();
                        fragmentTransaction.replace(container.getId(), entryFragement);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        return false;
                    }
                });

                pref.setTitle(calendarInLoop.getName());
                pref.setSummary(calendarInLoop.summaryText());
                if(calendarInLoop.isSchuCalendar){
                    if(!schusAktiviert){
                        pref.setEnabled(false);
                        pref.setShouldDisableView(true);
                    }

                    preferenceGroupSchus.addPreference(pref);
                }
                else{
                    preferenceGroupIndividualEvents.addPreference(pref);
                }
            }

        }


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);
            this.container = container;
            return v;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationFragment.class.getName().equals(fragmentName)
                || StandardNotificationsFragment.class.getName().equals(fragmentName)
                || GeneralFragment.class.getName().equals(fragmentName)
                || EntriesFragment.class.getName().equals(fragmentName)
                || DebuggingFragment.class.getName().equals(fragmentName);
    }



    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.v("prefChanged","preference "+preference.toString()+"\nvalue"+value.toString());



            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                //preference.setSummary(stringValue);
                return true;
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static String getBeautifulStringFromTime(int hoursOfDay, int minutes){

        String timeInBeautiful = "";
        if(hoursOfDay < 10){
            timeInBeautiful = timeInBeautiful+"0"+hoursOfDay;
        }
        else {
            timeInBeautiful = timeInBeautiful+""+hoursOfDay;
        }
        timeInBeautiful = timeInBeautiful + ":";
        if(minutes<10){
            timeInBeautiful = timeInBeautiful+"0"+minutes;
        }
        else {
            timeInBeautiful = timeInBeautiful+""+minutes;
        }
        return timeInBeautiful;
    }



}
