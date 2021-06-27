package com.ada.mybuffet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.ada.mybuffet.R;
import com.ada.mybuffet.screens.myShares.model.PortfolioValueByDate;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class MPChartCustomMarkerView extends MarkerView {
    private final TextView tvContent;
    private ArrayList<PortfolioValueByDate> portfolioValuesByDate;

    public MPChartCustomMarkerView(Context context, int layoutResource, ArrayList<PortfolioValueByDate> portfolioValuesByDate) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
        this.portfolioValuesByDate = portfolioValuesByDate;
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int arrayIndex = (int) e.getX();
        Timestamp timestamp = portfolioValuesByDate.get(arrayIndex).getDate();
        Date date = timestamp.toDate();

        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(date);

        tvContent.setText(formattedDate);

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
