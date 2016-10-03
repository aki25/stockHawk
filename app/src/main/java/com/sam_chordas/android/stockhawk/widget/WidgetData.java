package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.LineGraph;

import java.util.ArrayList;

class WidgetData implements RemoteViewsService.RemoteViewsFactory{

    Cursor c = null;
    private Context context;
    Intent intent;
    ArrayList<String> symbols;
    ArrayList<String> bidPrice;
    ArrayList<String> change;

    public WidgetData(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
        symbols = new ArrayList<>();
        bidPrice = new ArrayList<>();
        change = new ArrayList<>();
    }

    public void init(){
        if (c != null) {
            c.close();
        }

        symbols.clear();
        bidPrice.clear();
        change.clear();
        final long identityToken = Binder.clearCallingIdentity();

        c = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.CHANGE},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        int count = c.getCount();

        if (count <1) {
            symbols = null;
            bidPrice = null;
            change = null;
        }

        else{

            while (c.moveToNext()) {
                symbols.add(c.getString(c.getColumnIndex(QuoteColumns.SYMBOL)));
                bidPrice.add(c.getString(c.getColumnIndex(QuoteColumns.BIDPRICE)));
                change.add(c.getString(c.getColumnIndex(QuoteColumns.CHANGE)));

            }
        }
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        init();
    }

    @Override
    public void onDataSetChanged() {
        init();
    }

    @Override
    public void onDestroy() {
        if (c != null) {
            c.close();
        }
    }

    @Override
    public int getCount() {
        if (c == null)
            return 0;
        else
            return c.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || c == null ) {
            return null;
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        remoteViews.setTextViewText(R.id.widget_stock, symbols.get(position));
        remoteViews.setTextViewText(R.id.bid_price, bidPrice.get(position));
        remoteViews.setTextViewText(R.id.widget_change, change.get(position));

        final Intent fillInIntent = new Intent(context, LineGraph.class);
        fillInIntent.putExtra("symbol_name",symbols.get(position));
        fillInIntent.putExtra("bid_price",bidPrice.get(position));
        remoteViews.setOnClickFillInIntent(R.id.widget_stock, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
