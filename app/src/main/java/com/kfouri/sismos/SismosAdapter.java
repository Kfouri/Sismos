package com.kfouri.sismos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SismosAdapter extends ArrayAdapter {

    private Context mContext;
    private int layoutResourceId;
    ArrayList<Sismo> data = new ArrayList<Sismo>();

    public SismosAdapter(Context context, int layoutResourceId , ArrayList<Sismo> data )
    {
        super(context, layoutResourceId, data);
        mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        SismoHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new SismoHolder();

            holder.textMag = (TextView) row.findViewById(R.id.mag);
            holder.textCiudad = (TextView) row.findViewById(R.id.ciudad);
            holder.textFecha = (TextView) row.findViewById(R.id.fecha);
            holder.textLat = (TextView) row.findViewById(R.id.lat);
            holder.textLon = (TextView) row.findViewById(R.id.lon);

            holder.btnMapa = (Button) row.findViewById(R.id.btnMapa);

            row.setTag(holder);
        }
        else
        {
            holder = (SismoHolder) row.getTag();
        }

        final Sismo sis = data.get(position);
        holder.textMag.setText(String.valueOf(sis.getMag()));
        holder.textCiudad.setText(String.valueOf(sis.getCiudad()));
        holder.textFecha.setText(String.valueOf(sis.getFecha()));
        holder.textLat.setText(String.valueOf(sis.getLat()));
        holder.textLon.setText(String.valueOf(sis.getLon()));

        if (Float.parseFloat(sis.getMag())<4)
        {
            holder.textMag.setTextColor(Color.parseColor("#4CAF50"));
        }
        else if (Float.parseFloat(sis.getMag())>=4 && Float.parseFloat(sis.getMag())<=6)
        {
            holder.textMag.setTextColor(Color.parseColor("#FBC02D"));
        }
        else
        {
            holder.textMag.setTextColor(Color.parseColor("#DD2C00"));
        }

        final SismoHolder finalHolder = holder;
        holder.btnMapa.setOnClickListener(new View.OnClickListener()
        {
           public void onClick(View v)
           {
               String tmp = finalHolder.textLat.getText().toString();
               tmp = tmp.replace("Lat: ","");
               Double xlat = Double.parseDouble(tmp);

               tmp = finalHolder.textLon.getText().toString();
               tmp = tmp.replace("Lon: ","");
               Double xlon = Double.parseDouble(tmp);
               //Toast.makeText(mContext, "Lat: "+xlat+" Lon: "+xlon,Toast.LENGTH_LONG).show();

               Intent i=new Intent(mContext,MapsActivity.class);
               Bundle b = new Bundle();
               b.putDouble ("xLat", xlat);
               b.putDouble ("xLon", xlon);
               b.putString("xMag", finalHolder.textMag.getText().toString());
               b.putString("xFecha", finalHolder.textFecha.getText().toString());
               b.putString("xCiudad", finalHolder.textCiudad.getText().toString());
               i.putExtras(b);

               mContext.startActivity(i);

           }
        });

        return row;
    }

    static class SismoHolder {
        TextView textMag;
        TextView textFecha;
        TextView textLat;
        TextView textLon;
        TextView textCiudad;
        Button btnMapa;


    }

}