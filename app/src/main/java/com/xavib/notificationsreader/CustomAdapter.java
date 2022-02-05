package com.xavib.notificationsreader;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends BaseAdapter {
    MainActivity activity;
    LayoutInflater inflater;

    public CustomAdapter(MainActivity activity) {
        this.activity = activity;
        inflater = (LayoutInflater.from(activity));
    }

    @Override
    public int getCount() {
        return activity.data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item, null);
        TextView app_name = (TextView) view.findViewById(R.id.app_name);
        app_name.setText(activity.data.get(i).app_name);
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(activity.data.get(i).text);
        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(activity, "Text copied", Toast.LENGTH_SHORT);
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", text.getText());
                clipboard.setPrimaryClip(clipData);
                return false;
            }
        });
        return view;
    }
}
