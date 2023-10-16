package com.example.mobile_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobile_client.R;
import com.example.mobile_client.model.ReservationModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.List;
import javax.annotation.Nullable;

/*
 * File Name: ReservationAdapter.java
 * Description: Communicate data between reservation model.
 * Author: IT20168704
 */

public class ReservationAdapter extends ArrayAdapter<ReservationModel> {

    private Context context;
    private int resource;
    List<ReservationModel> reservations;


    ReservationAdapter(Context context, int resource, List<ReservationModel> reservations){
        super(context,resource,reservations);
        this.context = context;
        this.resource = resource;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(resource,parent,false);

        TextView title = row.findViewById(R.id.heading);
        TextView description = row.findViewById(R.id.description);
        TextView t1 = (TextView) row.findViewById(R.id.total);

        //deliveries [obj1,obj2,obj3]
        ReservationModel reservation = reservations.get(position);
        title.setText(reservation.getName());
        description.setText(reservation.getAddress());

        t1.setText("Rs: "+""+reservation.getQuentity()*100);
        return row;

    }

}
