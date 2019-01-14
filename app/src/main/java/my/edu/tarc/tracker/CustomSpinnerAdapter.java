package my.edu.tarc.tracker;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.dellpc.tracker.R;

import java.util.ArrayList;

/**
 * Created by KweeTeck on 3/26/2017.
 */

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context activity;
    private ArrayList<String> asr;

    public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
        this.asr=asr;
        activity = context;
    }

    public int getCount()
    {
        return asr.size();
    }

    public Object getItem(int i)
    {
        return asr.get(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(asr.get(position));
        txt.setTextColor(Color.BLACK);
        return  txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_white, 0);
        txt.setText(asr.get(i));
        txt.setTextColor(Color.WHITE);
        return  txt;
    }
}
