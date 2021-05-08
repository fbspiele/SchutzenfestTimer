package com.fbspiele.schutzenfesttimer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.fbspiele.schutzenfesttimer.MainActivity;
import com.fbspiele.schutzenfesttimer.MyCalendar;
import com.fbspiele.schutzenfesttimer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    static PlaceholderFragment newInstance(int index) {
        final PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }


    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        int index = 1;

        if (getArguments() != null) {
            //Log.v("PlaceholderFragment.getArguments",getArguments().toString());
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        calendar = MainActivity.myAngezeigteCalendarList.get(index-1);
    }

    private TextView textViewName, textViewNochSchlafen, textViewDatum, textViewNochSekunden;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        textViewName = root.findViewById(R.id.textViewName);
        textViewNochSchlafen = root.findViewById(R.id.textViewNochSchlafen);
        textViewDatum = root.findViewById(R.id.textViewDatum);
        textViewNochSekunden = root.findViewById(R.id.textViewSekunden);
        updateView();
        return root;
    }


    private MyCalendar calendar;

    public void updateCalendar(MyCalendar calendar){
        this.calendar=calendar;
    }


    public void updateView(){
        if(textViewNochSchlafen != null){
            calendar.update();

            textViewName.setText(Html.fromHtml(calendar.getName(),Html.FROM_HTML_MODE_COMPACT));
            textViewNochSchlafen.setText(Html.fromHtml(calendar.getNochSchlafenText(context, true),Html.FROM_HTML_MODE_COMPACT));
            textViewDatum.setText(Html.fromHtml(calendar.amZwischenText(),Html.FROM_HTML_MODE_COMPACT));
            textViewNochSekunden.setText(Html.fromHtml(calendar.getNochSekundenText(),Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            Log.w("PlaceholderFragment.updateView","thisTextView is null");
        }
    }
}