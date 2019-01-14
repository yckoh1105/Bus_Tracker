package my.edu.tarc.tracker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dellpc.tracker.R;

import java.util.ArrayList;

/**
 * Created by Dell PC on 15/10/2018.
 */

public class BusAdapter extends ArrayAdapter<RealTimeBus> {
    private ArrayList<RealTimeBus> buses;
    private LayoutInflater inflater;

    public BusAdapter(Activity context, int resource, ArrayList<RealTimeBus> busList) {
        super(context, resource, busList);
        buses = busList;
        inflater = context.getWindow().getLayoutInflater();
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
//        if(convertView == null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bus_list,parent,false);
//        }

        TextView txtSpeed = (TextView) convertView.findViewById(R.id.tvSpeed);
        TextView txtPlateNumber = (TextView) convertView.findViewById(R.id.tvPlateNumber);
        txtPlateNumber.setText(buses.get(position).getBusPlateNum());
        txtSpeed.setText(String.format("%.2f",buses.get(position).getSpeed()));
        return inflater.inflate(R.layout.item_bus_list,parent,false);
    }
}
