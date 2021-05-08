package com.fbspiele.schutzenfesttimer;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class myBenachrichtigungsVerandernAlertDialogBuilder extends AlertDialog.Builder {
    final static String tag = "myBenachrichtigungsVerandernAlertDialogBuilder";
    private Context mContext;


    myBenachrichtigungsVerandernAlertDialogBuilder(Context context) {
        super(context);
        mContext = context;
    }

    MyBenachrichtigung benachrichtigung;

    void initialize(MyBenachrichtigung benachrichtigung){
        this.benachrichtigung = benachrichtigung;
    }




    private View v;

    @Override
    public AlertDialog.Builder setView(View view) {
        v = view;
        return super.setView(view);
    }

    @Override
    public AlertDialog show() {
        AlertDialog alertDialog = super.show();


        final TextView uhrZeitTitleTextView = v.findViewById(R.id.uhrzeitTitle);
        final TextView uhrZeitTextView = v.findViewById(R.id.uhrzeitAuswahlen);

        uhrZeitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        benachrichtigung.benachrichtigungsHourOfDay = hourOfDay;
                        benachrichtigung.benachrichtigungsMin = minute;
                        uhrZeitTextView.setText(uhrzeitZuString(hourOfDay,minute));
                    }
                }, benachrichtigung.benachrichtigungsHourOfDay, benachrichtigung.benachrichtigungsMin, true).show();
            }
        });


        EditText nameEditText = v.findViewById(R.id.nameChange);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                benachrichtigung.name = s.toString();
            }
        });
        final Switch zukunftSwitch = v.findViewById(R.id.switchZukunft);
        zukunftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                benachrichtigung.inZukunftBenachrichtigen = isChecked;
            }
        });
        final Switch presentSwitch = v.findViewById(R.id.switchPresent);
        presentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                benachrichtigung.wennPresentBenachrichtigen = isChecked;
            }
        });
        final Switch vergangenheitSwitch = v.findViewById(R.id.switchVergangenheit);
        vergangenheitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                benachrichtigung.inVergangenheitBenachrichtigen = isChecked;
            }
        });
        if(benachrichtigung!=null){
            nameEditText.setText(benachrichtigung.name);
            zukunftSwitch.setChecked(benachrichtigung.inZukunftBenachrichtigen);
            presentSwitch.setChecked(benachrichtigung.wennPresentBenachrichtigen);
            vergangenheitSwitch.setChecked(benachrichtigung.inVergangenheitBenachrichtigen);
            uhrZeitTextView.setText(benachrichtigung.getBenachrichtigungsZeit());
        }
        final LinearLayout conditionsListLayout = v.findViewById(R.id.ListConditionsFurBenachrichtigung);
        final TextView alleSovieleTitle = v.findViewById(R.id.alleSovieleTitle);
        final TextView abstandWenigerTitle = v.findViewById(R.id.abstandWenigerTitle);


        if(benachrichtigung!=null){
            if(benachrichtigung.benachrichtigungsConditionen!= null){
                for(int i = 0; i<benachrichtigung.benachrichtigungsConditionen.size(); i++){
                    conditionsListLayout.addView(getConditionView(conditionsListLayout,benachrichtigung.benachrichtigungsConditionen.get(i)));
                }
            }
        }
        Button neueCondition = v.findViewById(R.id.ListConditionsFurBenachrichtigungNewCondition);
        neueCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBenachrichtigung.JedenWennKleinerGleich condition = new MyBenachrichtigung.JedenWennKleinerGleich();
                conditionsListLayout.addView(getConditionView(conditionsListLayout,condition));
                if(benachrichtigung.benachrichtigungsConditionen == null){
                    benachrichtigung.benachrichtigungsConditionen = new ArrayList<>();
                }
                benachrichtigung.benachrichtigungsConditionen.add(condition);
            }
        });


        RadioGroup benachrichtigungsTypRadioGroup = v.findViewById(R.id.radioGroupTyp);
        benachrichtigungsTypRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.v(tag, "checkedId " + checkedId );
                switch (checkedId){
                    case R.id.radioButtonTage:{
                        uhrZeitTitleTextView.setVisibility(View.VISIBLE);
                        uhrZeitTextView.setVisibility(View.VISIBLE);
                        zukunftSwitch.setVisibility(View.VISIBLE);
                        presentSwitch.setVisibility(View.VISIBLE);
                        vergangenheitSwitch.setVisibility(View.VISIBLE);
                        alleSovieleTitle.setText(R.string.pref_title_notificationAlleSoviele_tage);
                        abstandWenigerTitle.setText(R.string.pref_title_notificationHochstAbstandFurBenachrichtigung_tage);
                        alleSovieleTitle.setVisibility(View.VISIBLE);
                        abstandWenigerTitle.setVisibility(View.VISIBLE);
                        benachrichtigung.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_TAGE;
                        break;
                    }
                    case R.id.radioButtonStunden:{
                        uhrZeitTitleTextView.setVisibility(View.GONE);
                        uhrZeitTextView.setVisibility(View.GONE);
                        presentSwitch.setVisibility(View.GONE);
                        alleSovieleTitle.setText(R.string.pref_title_notificationAlleSoviele_stunden);
                        abstandWenigerTitle.setText(R.string.pref_title_notificationHochstAbstandFurBenachrichtigung_stunden);
                        alleSovieleTitle.setVisibility(View.VISIBLE);
                        abstandWenigerTitle.setVisibility(View.VISIBLE);
                        benachrichtigung.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_STUNDEN;
                        break;
                    }
                    case R.id.radioButtonMinuten:{
                        uhrZeitTitleTextView.setVisibility(View.GONE);
                        uhrZeitTextView.setVisibility(View.GONE);
                        zukunftSwitch.setVisibility(View.VISIBLE);
                        presentSwitch.setVisibility(View.GONE);
                        vergangenheitSwitch.setVisibility(View.VISIBLE);
                        alleSovieleTitle.setText(R.string.pref_title_notificationAlleSoviele_minuten);
                        abstandWenigerTitle.setText(R.string.pref_title_notificationHochstAbstandFurBenachrichtigung_minuten);
                        alleSovieleTitle.setVisibility(View.VISIBLE);
                        abstandWenigerTitle.setVisibility(View.VISIBLE);
                        benachrichtigung.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_MINUTEN;
                        break;

                    }
                    case R.id.radioButtonSekunden:{
                        uhrZeitTitleTextView.setVisibility(View.GONE);
                        uhrZeitTextView.setVisibility(View.GONE);
                        zukunftSwitch.setVisibility(View.VISIBLE);
                        presentSwitch.setVisibility(View.GONE);
                        vergangenheitSwitch.setVisibility(View.VISIBLE);
                        alleSovieleTitle.setText(R.string.pref_title_notificationAlleSoviele_sekunden);
                        abstandWenigerTitle.setText(R.string.pref_title_notificationHochstAbstandFurBenachrichtigung_sekunden);
                        alleSovieleTitle.setVisibility(View.VISIBLE);
                        abstandWenigerTitle.setVisibility(View.VISIBLE);
                        benachrichtigung.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_SEKUNDEN;
                        break;

                    }
                    default:{
                        uhrZeitTitleTextView.setVisibility(View.INVISIBLE);
                        uhrZeitTextView.setVisibility(View.INVISIBLE);
                        zukunftSwitch.setVisibility(View.INVISIBLE);
                        presentSwitch.setVisibility(View.INVISIBLE);
                        vergangenheitSwitch.setVisibility(View.INVISIBLE);
                        alleSovieleTitle.setVisibility(View.INVISIBLE);
                        abstandWenigerTitle.setVisibility(View.INVISIBLE);
                        benachrichtigung.benachrichtigungsTyp = MainActivity.NOTIFICATION_TYPE_ERROR;
                        break;
                    }
                }
            }
        });

        return alertDialog;
    }


    public void setBenachrichtigungsTyp(int typ){
        final RadioGroup benachrichtigungsTypRadioGroup = v.findViewById(R.id.radioGroupTyp);
        switch (typ){
            case MainActivity.NOTIFICATION_TYPE_TAGE:{
                benachrichtigungsTypRadioGroup.check(R.id.radioButtonTage);
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_STUNDEN:{
                benachrichtigungsTypRadioGroup.check(R.id.radioButtonStunden);
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_MINUTEN:{
                benachrichtigungsTypRadioGroup.check(R.id.radioButtonMinuten);
                break;
            }
            case MainActivity.NOTIFICATION_TYPE_SEKUNDEN:{
                benachrichtigungsTypRadioGroup.check(R.id.radioButtonSekunden);
                break;
            }
            default:
                benachrichtigungsTypRadioGroup.clearCheck();
                break;
        }
    }


    private View getConditionView(LinearLayout layout, final MyBenachrichtigung.JedenWennKleinerGleich condition ){
        final LinearLayout finalLayout = layout;


        final long conditionId = condition.id;
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_benachrichtigung_verandern_conditions, finalLayout, false);
        EditText alleSoviele = view.findViewById(R.id.alleSovieleEditText);
        EditText abstand = view.findViewById(R.id.abstandWenigerEditText);
        ImageButton loschenButton = view.findViewById(R.id.conditionLoschen);
        alleSoviele.setText(String.valueOf(condition.jeden));
        alleSoviele.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int valueOfS = -1;
                try {
                    valueOfS = Integer.parseInt(s.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if(valueOfS > 0){
                    condition.jeden = valueOfS;
                }
                else {
                    Log.w("myBenachVerAlerDialBuilder","jeden sovielten ist entweder <= 0 was keinen sinn macht oder keine ganze zahl");
                    Toast.makeText(getContext(), "zahl muss ganzzahl > 0 sein sonst macht es ja auch kein sinn",Toast.LENGTH_LONG).show();
                }

            }
        });
        abstand.setText(String.valueOf(condition.wennKleinerGleich));
        abstand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int valueOfS = -1;
                try {
                    valueOfS = Integer.parseInt(s.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if(valueOfS > -1){
                    condition.wennKleinerGleich = valueOfS;
                }
                else {
                    Log.w("myBenachVerAlerDialBuilder","jeden wenn abstand ist entweder <= -1 was keinen sinn macht oder keine ganze zahl");
                    Toast.makeText(getContext(), "zahl muss ganzzahl > -1 sein sonst macht es ja auch kein sinn (0=immer)",Toast.LENGTH_LONG).show();
                }

            }
        });
        loschenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemToRemove = -1;
                for (int k = 0; k<benachrichtigung.benachrichtigungsConditionen.size();k++){
                    if(benachrichtigung.benachrichtigungsConditionen.get(k).id==conditionId){
                        itemToRemove = k;
                    }
                }
                if(itemToRemove!=-1){
                    benachrichtigung.benachrichtigungsConditionen.remove(itemToRemove);
                }
                finalLayout.removeView(view);
            }
        });
        return view;
    }


    private String uhrzeitZuString(int hourOfDay, int min){
        String returnString;
        if(hourOfDay<10){
            returnString = "0"+hourOfDay;
        }
        else{
            returnString = ""+hourOfDay;
        }
        returnString = returnString + ":";
        if(min<10){
            returnString = returnString + "0"+min;
        }
        else{
            returnString = returnString + min;
        }
        return returnString;
    }

}

